package automation;

import domain.DowntimeResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.OffsetTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DowntimeTimeFrameDetectionTest extends AbstractDowntimeAnalyzerBaseTest {

    @Test
    void shouldCorrectlyDetectDowntimeStartAndEndTimes() throws IOException {
        int okCount = 10;
        int failCount = 10;
        generateFileFailEnd(okCount, failCount);
        process = startConsoleApp(99.9, 45.2);

        OffsetTime baseTime = LOG_FILE_GENERATOR.getBaseOffsetTime();
        OffsetTime expectedStart = baseTime.plusSeconds(okCount + 1);
        List<String> strings = readDowntimeFromConsole(process, 1, TIME_OUT);
        DowntimeResult actualDownTime = extractDownTimeConsoleLog(strings.getFirst());
        OffsetTime expectedEnd = baseTime.plusSeconds(okCount + failCount);

        assertEquals(expectedStart, actualDownTime.start(), "Wrong start of downtime");
        assertEquals(expectedEnd, actualDownTime.end(), "Wrong ending of downtime");
    }
}
