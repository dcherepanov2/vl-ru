package automation;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MultipleDowntimeEventsTest extends AbstractDowntimeAnalyzerBaseTest {

    @Test
    void shouldReportAllDowntimeEventsWhenThresholdExceededMultipleTimes() throws IOException {
        int okCount = 5;
        int failCount = 10;
        generateFileFailEnd(okCount, failCount);
        process = startConsoleApp(99.9, 45.2);

        List<String> strings = readDowntimeFromConsole(process, 1, Duration.ofSeconds(5));

        assertEquals(1, strings.size(), "Console not should be empty");
    }
}