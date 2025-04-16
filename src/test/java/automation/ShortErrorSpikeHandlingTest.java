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
        process = startConsoleApp(94.9, 45.2);

        List<String> logs = readDowntimeFromConsole(process, 1, Duration.ofSeconds(5));

        assertTrue(logs.isEmpty(), "Should console 1 downtime log");
    }
}
