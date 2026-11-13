package br.gov.caixa.caixaverso.cotacao.application;

import br.gov.caixa.caixaverso.cotacao.domain.CollectionClaim;
import br.gov.caixa.caixaverso.cotacao.domain.CollectionKey;
import br.gov.caixa.caixaverso.cotacao.domain.CollectionOutcome;
import br.gov.caixa.caixaverso.cotacao.domain.CollectionState;
import br.gov.caixa.caixaverso.cotacao.domain.Quotation;
import br.gov.caixa.caixaverso.cotacao.domain.RetryPolicy;
import br.gov.caixa.caixaverso.cotacao.persistence.CollectionItemEntity;
import br.gov.caixa.caixaverso.cotacao.persistence.CollectionItemRepository;
import br.gov.caixa.caixaverso.cotacao.persistence.QuotationEntity;
import br.gov.caixa.caixaverso.cotacao.persistence.QuotationRepository;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CollectionStore {
    private final CollectionItemRepository items;
    private final QuotationRepository quotations;

    @Inject
    public CollectionStore(CollectionItemRepository items, QuotationRepository quotations) {
        this.items = items;
        this.quotations = quotations;
    }

    public synchronized void ensure(CollectionKey key, Instant now) {
        QuarkusTransaction.requiringNew().run(() -> {
            if (items.findByKey(key).isPresent()) {
                return;
            }
            CollectionItemEntity item = new CollectionItemEntity();
            item.quotationDate = key.date();
            item.currency = key.currency();
            item.bulletin = key.bulletin();
            item.state = CollectionState.PENDING;
            item.createdAt = now;
            item.nextAttemptAt = now;
            items.persist(item);
        });
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Optional<CollectionClaim> claim(Long id, Instant now, Duration lease) {
        if (lease.isNegative() || lease.isZero()) {
            throw new IllegalArgumentException("Lease must be positive");
        }
        Optional<CollectionItemEntity> found = items.lock(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        CollectionItemEntity item = found.get();
        if (item.state == CollectionState.COMPLETED
                || (item.state == CollectionState.PENDING && item.nextAttemptAt.isAfter(now))
                || (item.state == CollectionState.PROCESSING && item.claimUntil.isAfter(now))) {
            return Optional.empty();
        }
        if (item.firstAttemptAt == null) {
            item.firstAttemptAt = now;
        }
        item.state = CollectionState.PROCESSING;
        item.lastAttemptAt = now;
        item.attemptCount++;
        item.claimToken = UUID.randomUUID();
        item.claimUntil = now.plus(lease);
        return Optional.of(new CollectionClaim(item.id, item.key(), item.claimToken,
                item.firstAttemptAt, now));
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean complete(CollectionClaim claim, Quotation quotation, Instant now) {
        Optional<CollectionItemEntity> found = items.lock(claim.itemId());
        if (found.isEmpty() || !owns(found.get(), claim)) {
            return false;
        }
        if (!quotation.key().equals(claim.key())) {
            throw new IllegalArgumentException("Quotation does not match the claimed item");
        }
        if (quotations.findByKey(claim.key()).isEmpty()) {
            quotations.persist(QuotationEntity.from(quotation, now));
        }
        CollectionItemEntity item = found.get();
        item.state = CollectionState.COMPLETED;
        item.lastOutcome = CollectionOutcome.COLLECTED;
        item.lastError = null;
        item.claimToken = null;
        item.claimUntil = null;
        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean fail(CollectionClaim claim, CollectionOutcome outcome, String error, Instant now) {
        if (outcome == CollectionOutcome.COLLECTED) {
            throw new IllegalArgumentException("A collected item cannot be marked failed");
        }
        Optional<CollectionItemEntity> found = items.lock(claim.itemId());
        if (found.isEmpty() || !owns(found.get(), claim)) {
            return false;
        }
        CollectionItemEntity item = found.get();
        item.state = CollectionState.PENDING;
        item.nextAttemptAt = RetryPolicy.nextAttempt(item.firstAttemptAt, now);
        item.lastOutcome = outcome;
        item.lastError = error == null ? null : error.substring(0, Math.min(error.length(), 256));
        item.claimToken = null;
        item.claimUntil = null;
        return true;
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public int recoverOnStartup(Instant now) {
        return (int) items.update("state = ?1, claimToken = null, claimUntil = null, nextAttemptAt = ?2, "
                + "version = version + 1 where state = ?3", CollectionState.PENDING, now,
                CollectionState.PROCESSING);
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public List<Long> dueIds(LocalDate today, boolean historical, Instant now, int limit) {
        if (limit < 1) {
            throw new IllegalArgumentException("Batch limit must be positive");
        }
        return items.dueIds(today, historical, now, limit);
    }

    private boolean owns(CollectionItemEntity item, CollectionClaim claim) {
        return item.state == CollectionState.PROCESSING && claim.token().equals(item.claimToken);
    }
}