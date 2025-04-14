package automation;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShortErrorSpikeHandlingTest extends AbstractDowntimeAnalyzerBaseTest {

    @Test
    void shouldNotReportShortErrorBurstAsDowntime() throws IOException {
        generateFileFailEnd(19, 1);
        process = startConsoleApp(99.9, 45.2);

        List<String> logs = readDowntimeFromConsole(process, 1, Duration.ofSeconds(5));

        assertTrue(logs.isEmpty(), "A short burst of errors should not be considered downtime");
    }
}
