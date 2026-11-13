package br.gov.caixa.caixaverso.cotacao.persistence;

import br.gov.caixa.caixaverso.cotacao.domain.BulletinType;
import br.gov.caixa.caixaverso.cotacao.domain.CollectionKey;
import br.gov.caixa.caixaverso.cotacao.domain.CollectionOutcome;
import br.gov.caixa.caixaverso.cotacao.domain.CollectionState;
import br.gov.caixa.caixaverso.cotacao.domain.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "collection_item", uniqueConstraints = @UniqueConstraint(
        name = "uk_collection_item", columnNames = {"quotation_date", "currency", "bulletin"}))
public class CollectionItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "quotation_date", nullable = false)
    public LocalDate quotationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    public Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    public BulletinType bulletin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    public CollectionState state;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "first_attempt_at")
    public Instant firstAttemptAt;

    @Column(name = "last_attempt_at")
    public Instant lastAttemptAt;

    @Column(name = "next_attempt_at", nullable = false)
    public Instant nextAttemptAt;

    @Column(name = "attempt_count", nullable = false)
    public long attemptCount;

    @Column(name = "claim_token")
    public UUID claimToken;

    @Column(name = "claim_until")
    public Instant claimUntil;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_outcome", length = 32)
    public CollectionOutcome lastOutcome;

    @Column(name = "last_error", length = 256)
    public String lastError;

    @Version
    @Column(nullable = false)
    public long version;

    public CollectionKey key() {
        return new CollectionKey(quotationDate, currency, bulletin);
    }
}