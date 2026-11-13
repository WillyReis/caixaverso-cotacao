package br.gov.caixa.caixaverso.cotacao.domain;

public enum CollectionOutcome {
    COLLECTED,
    NO_BULLETIN,
    UNEXPECTED_BULLETIN,
    INVALID_PAYLOAD,
    TECHNICAL_FAILURE
}