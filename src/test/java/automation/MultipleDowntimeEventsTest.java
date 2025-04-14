package automation;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultipleDowntimeEventsTest extends AbstractDowntimeAnalyzerBaseTest {

    @Test
    void shouldReportAllDowntimeEventsWhenThresholdExceededMultipleTimes() throws IOException {
        int okCount = 10;
        int failCount = 20;
        generateFileFailEnd(okCount, failCount);
        process = startConsoleApp(99.9, 45.2);

        List<String> strings = readDowntimeFromConsole(process, 3, Duration.ofSeconds(5));

        assertEquals(2, strings.size(), "Incorrect number of reported downtime events");
    }
}