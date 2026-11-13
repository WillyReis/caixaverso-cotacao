package br.gov.caixa.caixaverso.cotacao.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("CA-001")
@Tag("CA-002")
class QuotationTest {
    private final LocalDate date = LocalDate.of(2026, 11, 12);
    private final LocalDateTime quotedAt = date.atTime(10, 6, 10, 203409000);

    @Test
    void retainsDecimalPrecisionWithoutDoubleConversion() {
        BigDecimal purchase = new BigDecimal("5.602200000000000001");
        Quotation quotation = new Quotation(date, Currency.USD, BulletinType.ABERTURA,
                purchase, new BigDecimal("5.6028"), quotedAt);
        assertEquals(0, purchase.compareTo(quotation.purchase()));
    }

    @Test
    void rejectsValuesThatCannotBeStoredExactly() {
        assertThrows(IllegalArgumentException.class, () -> quotation("5.0000000000000000001"));
        assertThrows(IllegalArgumentException.class, () -> quotation("100000000000000000000"));
        assertThrows(IllegalArgumentException.class, () -> quotation("0"));
        assertThrows(IllegalArgumentException.class, () -> quotation("-1"));
    }

    @Test
    void rejectsQuotationForAnotherDate() {
        assertThrows(IllegalArgumentException.class,
                () -> new Quotation(date, Currency.EUR, BulletinType.ENCERRAMENTO,
                        new BigDecimal("6.1"), new BigDecimal("6.2"), quotedAt.plusDays(1)));
    }

    private Quotation quotation(String purchase) {
        return new Quotation(date, Currency.USD, BulletinType.ABERTURA,
                new BigDecimal(purchase), new BigDecimal("5.7"), quotedAt);
    }
}