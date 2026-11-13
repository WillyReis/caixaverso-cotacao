package br.gov.caixa.caixaverso.cotacao.domain;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class RetryPolicy {
    private static final Duration INITIAL_PHASE = Duration.ofHours(3);
    private static final Duration INITIAL_INTERVAL = Duration.ofMinutes(5);
    private static final Duration LATER_INTERVAL = Duration.ofHours(1);

    private RetryPolicy() {
    }

    public static Instant nextAttempt(Instant firstAttemptAt, Instant attemptedAt) {
        Objects.requireNonNull(firstAttemptAt, "firstAttemptAt");
        Objects.requireNonNull(attemptedAt, "attemptedAt");
        if (attemptedAt.isBefore(firstAttemptAt)) {
            throw new IllegalArgumentException("Attempt precedes the first attempt");
        }
        Duration elapsed = Duration.between(firstAttemptAt, attemptedAt);
        return attemptedAt.plus(elapsed.compareTo(INITIAL_PHASE) < 0 ? INITIAL_INTERVAL : LATER_INTERVAL);
    }
}