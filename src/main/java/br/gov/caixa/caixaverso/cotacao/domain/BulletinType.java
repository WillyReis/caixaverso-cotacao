package br.gov.caixa.caixaverso.cotacao.domain;

import java.util.Arrays;
import java.util.Optional;

public enum BulletinType {
    ABERTURA("Abertura"),
    ENCERRAMENTO("Fechamento PTAX");

    private final String bacenName;

    BulletinType(String bacenName) {
        this.bacenName = bacenName;
    }

    public static Optional<BulletinType> fromBacen(String name) {
        return Arrays.stream(values()).filter(type -> type.bacenName.equals(name)).findFirst();
    }
}