package hei.school;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Objects;

public final class LatePenalty {

    private static final BigDecimal DAILY_PENALTY = new BigDecimal("0.10");
    private static final BigDecimal MAX_PENALTY = new BigDecimal("0.50");

    private LatePenalty() {
    }

    public static BigDecimal apply(BigDecimal rawGrade, Duration lateness) {
        Objects.requireNonNull(rawGrade, "rawGrade must not be null");
        Objects.requireNonNull(lateness, "lateness must not be null");

        if (lateness.isZero() || lateness.isNegative()) {
            return rawGrade;
        }

        long lateDays = lateness.toDays();
        if (!lateness.minusDays(lateDays).isZero()) {
            lateDays++;
        }

        BigDecimal penalty = DAILY_PENALTY
                .multiply(BigDecimal.valueOf(lateDays))
                .min(MAX_PENALTY);

        return rawGrade.multiply(BigDecimal.ONE.subtract(penalty));
    }
}
