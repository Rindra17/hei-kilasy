package hei.school;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Grading {

    private static final BigDecimal TWENTY = new BigDecimal("20");

    private Grading() {
    }

    /** The complete deadline day is on time; lateness starts after midnight of the next day. */
    public static Duration lateness(LocalDate deadline, LocalDateTime submittedAt) {
        Objects.requireNonNull(deadline, "deadline must not be null");
        Objects.requireNonNull(submittedAt, "submittedAt must not be null");

        LocalDateTime limit = deadline.plusDays(1).atStartOfDay();
        return submittedAt.isAfter(limit)
                ? Duration.between(limit, submittedAt)
                : Duration.ZERO;
    }

    public static boolean isLate(LocalDate deadline, LocalDateTime submittedAt) {
        return !lateness(deadline, submittedAt).isZero();
    }

    /** Converts raw points to a displayed grade out of 20 and applies the late penalty. */
    public static BigDecimal finalGrade(
            BigDecimal rawPoints,
            BigDecimal maxPoints,
            LocalDate deadline,
            LocalDateTime submittedAt) {
        Objects.requireNonNull(rawPoints, "rawPoints must not be null");
        Objects.requireNonNull(maxPoints, "maxPoints must not be null");

        if (maxPoints.signum() <= 0) {
            throw new IllegalArgumentException("maxPoints must be positive");
        }

        BigDecimal rawGrade = rawPoints
                .multiply(TWENTY)
                .divide(maxPoints, 12, RoundingMode.HALF_UP);

        return LatePenalty.apply(rawGrade, lateness(deadline, submittedAt))
                .setScale(1, RoundingMode.HALF_UP);
    }
}
