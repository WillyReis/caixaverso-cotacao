package br.gov.caixa.caixaverso.cotacao.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("CA-003")
@Tag("CA-004")
class RetryPolicyTest {
    private final Instant startedAt = Instant.parse("2026-11-12T12:00:00Z");

    @Test
    void retriesAfterFiveMinutesDuringInitialPhase() {
        Instant attemptedAt = startedAt.plus(Duration.ofHours(2));
        assertEquals(attemptedAt.plus(Duration.ofMinutes(5)),
                RetryPolicy.nextAttempt(startedAt, attemptedAt));
    }

    @Test
    void stillUsesFiveMinutesImmediatelyBeforeThreeHours() {
        Instant attemptedAt = startedAt.plus(Duration.ofHours(3)).minusSeconds(1);
        assertEquals(attemptedAt.plus(Duration.ofMinutes(5)),
                RetryPolicy.nextAttempt(startedAt, attemptedAt));
    }

    @Test
    void usesOneHourExactlyAtTheThreeHourBoundary() {
        Instant attemptedAt = startedAt.plus(Duration.ofHours(3));
        assertEquals(attemptedAt.plus(Duration.ofHours(1)),
                RetryPolicy.nextAttempt(startedAt, attemptedAt));
    }

    @Test
    void retainsHourlyRetriesForPreviousDays() {
        Instant attemptedAt = startedAt.plus(Duration.ofDays(2));
        assertEquals(attemptedAt.plus(Duration.ofHours(1)),
                RetryPolicy.nextAttempt(startedAt, attemptedAt));
    }

    @Test
    void rejectsAnAttemptBeforeTheFirstAttempt() {
        assertThrows(IllegalArgumentException.class,
                () -> RetryPolicy.nextAttempt(startedAt, startedAt.minusSeconds(1)));
    }
}