package hei.school;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class SimulatedSmsSenderTest {

    private final PrintStream originalOut = System.out;

    @AfterEach
    void restoreStandardOutput() {
        System.setOut(originalOut);
    }

    @Test
    void simulatedProviderLogsTheExpectedSmsFormat() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        new SimulatedSmsSender().send(
                "+261 34 00 000 00",
                "Votre note pour « Tri à bulles » est disponible : 14,4/20.");

        assertEquals(
                "[SMS] +261 34 00 000 00 : Votre note pour « Tri à bulles » est disponible : 14,4/20.\n",
                output.toString());
    }
}
