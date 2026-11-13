package br.gov.caixa.caixaverso.cotacao.persistence;

import br.gov.caixa.caixaverso.cotacao.domain.BulletinType;
import br.gov.caixa.caixaverso.cotacao.domain.Currency;
import br.gov.caixa.caixaverso.cotacao.domain.Quotation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "quotation", uniqueConstraints = @UniqueConstraint(
        name = "uk_quotation_item", columnNames = {"quotation_date", "currency", "bulletin"}))
public class QuotationEntity {
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

    @Column(nullable = false, precision = 38, scale = 18)
    public BigDecimal purchase;

    @Column(nullable = false, precision = 38, scale = 18)
    public BigDecimal sale;

    @Column(name = "quoted_at", nullable = false)
    public LocalDateTime quotedAt;

    @Column(name = "collected_at", nullable = false)
    public Instant collectedAt;

    public static QuotationEntity from(Quotation quotation, Instant collectedAt) {
        QuotationEntity entity = new QuotationEntity();
        entity.quotationDate = quotation.date();
        entity.currency = quotation.currency();
        entity.bulletin = quotation.bulletin();
        entity.purchase = quotation.purchase();
        entity.sale = quotation.sale();
        entity.quotedAt = quotation.quotedAt();
        entity.collectedAt = collectedAt;
        return entity;
    }

    public Quotation toDomain() {
        return new Quotation(quotationDate, currency, bulletin, purchase, sale, quotedAt);
    }
}