package automation;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseTimeThresholdTest extends AbstractDowntimeAnalyzerBaseTest {

    @Test
    void shouldDetectDowntimeWhenResponseTimeExceedsLimit() throws IOException {
        int totalLines = 20;
        generateFileNotAllowedCompleteMs(totalLines, 100);
        process = startConsoleApp(99.9, 100);

        List<String> strings = readDowntimeFromConsole(process, 2, Duration.ofSeconds(5));

        assertEquals(2, strings.size(), "Should detect downtime when response time exceeds limit");
    }
}