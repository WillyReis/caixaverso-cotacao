package br.gov.caixa.caixaverso.cotacao.domain;

import java.time.LocalDate;
import java.util.Objects;

public record CollectionKey(LocalDate date, Currency currency, BulletinType bulletin) {
    public CollectionKey {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(currency, "currency");
        Objects.requireNonNull(bulletin, "bulletin");
    }
}