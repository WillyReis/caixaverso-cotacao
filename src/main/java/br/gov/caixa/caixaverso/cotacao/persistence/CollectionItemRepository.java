package br.gov.caixa.caixaverso.cotacao.persistence;

import br.gov.caixa.caixaverso.cotacao.domain.CollectionKey;
import br.gov.caixa.caixaverso.cotacao.domain.CollectionState;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CollectionItemRepository implements PanacheRepository<CollectionItemEntity> {
    public Optional<CollectionItemEntity> findByKey(CollectionKey key) {
        return find("quotationDate = ?1 and currency = ?2 and bulletin = ?3",
                key.date(), key.currency(), key.bulletin()).firstResultOptional();
    }

    public Optional<CollectionItemEntity> lock(Long id) {
        return find("id", id).withLock(LockModeType.PESSIMISTIC_WRITE).firstResultOptional();
    }

    public List<Long> dueIds(LocalDate today, boolean historical, Instant now, int limit) {
        String comparison = historical ? "<" : "=";
        return find("quotationDate " + comparison + " ?1 and "
                        + "((state = ?2 and nextAttemptAt <= ?3) or (state = ?4 and claimUntil <= ?3)) "
                        + "order by nextAttemptAt, id", today, CollectionState.PENDING, now,
                CollectionState.PROCESSING).page(0, limit).list().stream().map(item -> item.id).toList();
    }
}