package automation;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorRateThresholdTest extends AbstractDowntimeAnalyzerBaseTest {

    @Test
    void shouldNotTriggerDowntimeWhenErrorRateBelowThreshold() throws IOException {
        int totalLines = 20;
        generateMixStringFile(totalLines);
        process = startConsoleApp(49.9, 1000000000);

        List<String> strings = readDowntimeFromConsole(process, 2, Duration.ofSeconds(5));

        assertEquals(0, strings.size(), "Should not report downtime when error rate is low");
    }
}
