package hei.school;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LatePenaltyTest {

    @ParameterizedTest(name = "{index}: {1} late should result in {2}/20")
    @MethodSource("latePenaltyCases")
    void appliesTenPercentPenaltyPerLateDayWithFiftyPercentCap(
            BigDecimal rawGrade,
            Duration lateness,
            BigDecimal expectedGrade) {

        BigDecimal actualGrade = LatePenalty.apply(rawGrade, lateness);

        assertEquals(0, expectedGrade.compareTo(actualGrade));
    }

    private static Stream<Arguments> latePenaltyCases() {
        BigDecimal rawGrade = new BigDecimal("16");

        return Stream.of(
                // Submitted on time: no penalty.
                Arguments.of(rawGrade, Duration.ZERO, new BigDecimal("16")),

                // Submitted before the deadline: no penalty.
                Arguments.of(rawGrade, Duration.ofHours(-2), new BigDecimal("16")),

                // Two hours late counts as one full late day: -10%.
                Arguments.of(rawGrade, Duration.ofHours(2), new BigDecimal("14.4")),

                // Three days late: -30%.
                Arguments.of(rawGrade, Duration.ofDays(3), new BigDecimal("11.2")),

                // Eight days late would be -80%, but the penalty is capped at -50%.
                Arguments.of(rawGrade, Duration.ofDays(8), new BigDecimal("8"))
        );
    }
}
