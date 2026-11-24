package hei.school;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class GradingTest {

    private static final LocalDate DEADLINE = LocalDate.of(2026, 11, 10);
    private static final BigDecimal RAW_POINTS = new BigDecimal("16");
    private static final BigDecimal MAX_POINTS = new BigDecimal("20");

    @Test
    void submissionBeforeDeadlineHasNoPenalty() {
        assertGrade("16", LocalDateTime.of(2026, 11, 10, 10, 0));
    }

    @Test
    void submissionTwoHoursAfterDeadlineDayCountsAsOneLateDay() {
        assertGrade("14.4", LocalDateTime.of(2026, 11, 11, 2, 0));
    }

    @Test
    void submissionThreeDaysLateLosesThirtyPercent() {
        assertGrade("11.2", LocalDateTime.of(2026, 11, 14, 0, 0));
    }

    @Test
    void submissionEightDaysLateIsCappedAtFiftyPercentPenalty() {
        assertGrade("8", LocalDateTime.of(2026, 11, 19, 0, 0));
    }

    private void assertGrade(String expected, LocalDateTime submittedAt) {
        BigDecimal actual = Grading.finalGrade(
                RAW_POINTS,
                MAX_POINTS,
                DEADLINE,
                submittedAt);

        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}
