package br.gov.caixa.caixaverso.cotacao.domain;

import java.time.Instant;
import java.util.UUID;

public record CollectionClaim(long itemId, CollectionKey key, UUID token,
                              Instant firstAttemptAt, Instant attemptedAt) {
}