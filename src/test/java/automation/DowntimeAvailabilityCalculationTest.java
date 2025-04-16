package automation;
import domain.DowntimeResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class DowntimeAvailabilityCalculationTest extends AbstractDowntimeAnalyzerBaseTest {

    @Test
    void shouldCalculateCorrectAvailabilityDuringDowntime() throws IOException {
        int okCount = 3;
        int failCount = 7;
        double expectedAvailability = 30d;

        generateFileFailEnd(okCount, failCount);
        process = startConsoleApp(99.9, 45.2);

        List<String> strings = readDowntimeFromConsole(process, 1, TIME_OUT);
        DowntimeResult actualDownTime = extractDownTimeConsoleLog(strings.getFirst());

        assertEquals(expectedAvailability, actualDownTime.realAvailabilityLevel(), "Calculated availability is incorrect");
    }

    @Test
    void shouldCalculateZeroAvailabilityWhenAllRequestsFail() throws IOException {
        int okCount = 0;
        int failCount = 10;
        double expectedAvailability = 0.0;

        generateFileFailEnd(okCount, failCount);
        process = startConsoleApp(99.9, 45.2);

        List<String> strings = readDowntimeFromConsole(process, 1, TIME_OUT);
        DowntimeResult actualDownTime = extractDownTimeConsoleLog(strings.getFirst());

        assertEquals(expectedAvailability, actualDownTime.realAvailabilityLevel(), "Availability should be 0 when all requests fail");
    }

    @Test
    void shouldCalculateFullAvailabilityWhenNoFailures() throws IOException {
        int okCount = 10;
        int failCount = 0;

        generateFileFailEnd(okCount, failCount);
        process = startConsoleApp(99.9, 45.2);

        List<String> strings = readDowntimeFromConsole(process, 1, Duration.ofSeconds(5));

        assertTrue(strings.isEmpty(), "Availability should be 100 when no failures occur");
    }
}
