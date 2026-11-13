package br.gov.caixa.caixaverso.cotacao.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.gov.caixa.caixaverso.cotacao.application.CollectionStore;
import br.gov.caixa.caixaverso.cotacao.domain.BulletinType;
import br.gov.caixa.caixaverso.cotacao.domain.CollectionKey;
import br.gov.caixa.caixaverso.cotacao.domain.CollectionOutcome;
import br.gov.caixa.caixaverso.cotacao.domain.CollectionState;
import br.gov.caixa.caixaverso.cotacao.domain.Currency;
import br.gov.caixa.caixaverso.cotacao.domain.Quotation;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Tag("CA-001")
@Tag("CA-003")
@Tag("CA-005")
@Tag("RNF-002")
@Tag("RNF-006")
class CollectionStoreTest {
    private final LocalDate date = LocalDate.of(2026, 11, 12);
    private final Instant now = Instant.parse("2026-11-12T12:00:00Z");
    private final CollectionKey key = new CollectionKey(date, Currency.USD, BulletinType.ABERTURA);

    @Inject
    CollectionStore store;

    @Inject
    CollectionItemRepository items;

    @Inject
    QuotationRepository quotations;

    @BeforeEach
    void cleanDatabase() {
        QuarkusTransaction.requiringNew().run(() -> {
            items.deleteAll();
            quotations.deleteAll();
        });
    }

    @Test
    void createsOnlyOneItemAndStoresOnlyOneQuotation() {
        store.ensure(key, now);
        store.ensure(key, now.plusSeconds(1));
        assertEquals(1, items.count());
        var claim = store.claim(itemId(), now, Duration.ofMinutes(2)).orElseThrow();
        assertTrue(store.complete(claim, quotation(), now.plusSeconds(1)));
        assertFalse(store.complete(claim, quotation(), now.plusSeconds(2)));
        store.ensure(key, now.plusSeconds(3));
        assertEquals(1, quotations.count());
        assertEquals(CollectionState.COMPLETED, item().state);
        assertTrue(store.claim(itemId(), now.plusSeconds(4), Duration.ofMinutes(2)).isEmpty());
    }

    @Test
    void failurePreservesIdentityAndSchedulesFiveMinutesLater() {
        store.ensure(key, now);
        var claim = store.claim(itemId(), now, Duration.ofMinutes(2)).orElseThrow();
        store.fail(claim, CollectionOutcome.TECHNICAL_FAILURE, "BACEN_HTTP_503", now);
        var item = item();
        assertEquals(CollectionState.PENDING, item.state);
        assertEquals(key, item.key());
        assertEquals(now.plus(Duration.ofMinutes(5)), item.nextAttemptAt);
        assertEquals(CollectionOutcome.TECHNICAL_FAILURE, item.lastOutcome);
        assertEquals(1, item.attemptCount);
        assertEquals(0, quotations.count());
    }

    @Test
    void rejectsLateCompletionFromAnOldClaimAfterRestartRecovery() {
        store.ensure(key, now);
        var oldClaim = store.claim(itemId(), now, Duration.ofMinutes(2)).orElseThrow();
        assertEquals(1, store.recoverOnStartup(now.plusSeconds(10)));
        var newClaim = store.claim(itemId(), now.plusSeconds(10), Duration.ofMinutes(2)).orElseThrow();
        assertFalse(store.complete(oldClaim, quotation(), now.plusSeconds(11)));
        assertTrue(store.complete(newClaim, quotation(), now.plusSeconds(12)));
        assertEquals(1, quotations.count());
        assertEquals(now, item().firstAttemptAt);
        assertEquals(2, item().attemptCount);
    }

    @Test
    void doesNotClaimAnActiveItemButReclaimsAnExpiredLease() {
        store.ensure(key, now);
        var firstClaim = store.claim(itemId(), now, Duration.ofMinutes(2)).orElseThrow();
        assertTrue(store.claim(itemId(), now.plusSeconds(1), Duration.ofMinutes(2)).isEmpty());
        var secondClaim = store.claim(itemId(), now.plus(Duration.ofMinutes(2)),
                Duration.ofMinutes(2)).orElseThrow();
        assertFalse(store.complete(firstClaim, quotation(), now.plus(Duration.ofMinutes(2))));
        assertTrue(store.complete(secondClaim, quotation(), now.plus(Duration.ofMinutes(2))));
    }

    @Test
    void separatesPreviousDaysFromCurrentDay() {
        CollectionKey previous = new CollectionKey(date.minusDays(1), Currency.EUR,
                BulletinType.ENCERRAMENTO);
        store.ensure(previous, now.minus(Duration.ofDays(1)));
        store.ensure(key, now);
        assertEquals(1, store.dueIds(date, false, now, 20).size());
        var historicalIds = store.dueIds(date, true, now, 20);
        assertEquals(1, historicalIds.size());
        assertEquals(previous, items.findById(historicalIds.getFirst()).key());
    }

    @Test
    void twoConcurrentWorkersCannotOwnTheSameItem() throws Exception {
        store.ensure(key, now);
        Long itemId = itemId();
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> store.claim(itemId, now, Duration.ofMinutes(2)));
            var second = executor.submit(() -> store.claim(itemId, now, Duration.ofMinutes(2)));
            long owners = (first.get().isPresent() ? 1 : 0) + (second.get().isPresent() ? 1 : 0);
            assertEquals(1, owners);
        }
    }

    private Long itemId() {
        return item().id;
    }

    private CollectionItemEntity item() {
        return QuarkusTransaction.requiringNew().call(() -> items.findByKey(key).orElseThrow());
    }

    private Quotation quotation() {
        return new Quotation(date, Currency.USD, BulletinType.ABERTURA,
                new BigDecimal("5.602200000000000001"), new BigDecimal("5.6028"), date.atTime(10, 6));
    }
}