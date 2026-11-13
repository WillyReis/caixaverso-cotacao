package br.gov.caixa.caixaverso.cotacao.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public record Quotation(LocalDate date, Currency currency, BulletinType bulletin,
                        BigDecimal purchase, BigDecimal sale, LocalDateTime quotedAt) {
    public Quotation {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(bulletin, "bulletin");
        Objects.requireNonNull(quotedAt, "quotedAt");
        if (!quotedAt.toLocalDate().equals(date)) {
            throw new IllegalArgumentException("Quotation timestamp does not match collection date");
        }
        purchase = exactMonetaryValue(purchase);
        sale = exactMonetaryValue(sale);
    }

    public CollectionKey key() {
        return new CollectionKey(date, currency, bulletin);
    }

    private static BigDecimal exactMonetaryValue(BigDecimal value) {
        Objects.requireNonNull(value, "monetary value");
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("Monetary values must be positive");
        }
        try {
            BigDecimal stored = value.setScale(18, RoundingMode.UNNECESSARY);
            if (stored.precision() > 38) {
                throw new IllegalArgumentException("Monetary value exceeds DECIMAL(38,18)");
            }
        } catch (ArithmeticException exception) {
            throw new IllegalArgumentException("Monetary value cannot be stored without rounding", exception);
        }
        return value.stripTrailingZeros();
    }
}