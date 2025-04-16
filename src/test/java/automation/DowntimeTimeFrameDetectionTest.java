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
        int okCount = 5;
        int failCount = 5;
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

    @Test
    void shouldDetectSingleFailureAsDowntime() throws IOException {
        int okCount = 9;
        int failCount = 1;
        generateFileFailEnd(okCount, failCount);
        process = startConsoleApp(99.9, 45.2);

        OffsetTime baseTime = LOG_FILE_GENERATOR.getBaseOffsetTime();
        OffsetTime expectedStart = baseTime.plusSeconds(okCount + 1);
        List<String> strings = readDowntimeFromConsole(process, 1, TIME_OUT);
        DowntimeResult actualDownTime = extractDownTimeConsoleLog(strings.getFirst());
        OffsetTime expectedEnd = baseTime.plusSeconds(okCount + failCount);

        assertEquals(expectedStart, actualDownTime.start(), "Single failure should be detected as downtime start");
        assertEquals(expectedEnd, actualDownTime.end(), "Single failure should be detected as downtime end");
    }

    @Test
    void shouldDetectTwoDowntimePeriodsInAlternatingLog() throws IOException {
        int totalLines = 20;
        generateMixStringFile(totalLines);
        process = startConsoleApp(99.9, 45.2);

        OffsetTime baseTime = LOG_FILE_GENERATOR.getBaseOffsetTime();

        List<String> strings = readDowntimeFromConsole(process, 2, TIME_OUT);

        DowntimeResult firstDowntime = extractDownTimeConsoleLog(strings.getFirst());
        OffsetTime expectedFirstStart = baseTime.plusSeconds(2);
        OffsetTime expectedFirstEnd = baseTime.plusSeconds(10);

        DowntimeResult secondDowntime = extractDownTimeConsoleLog(strings.get(1));
        OffsetTime expectedSecondStart = baseTime.plusSeconds(12);
        OffsetTime expectedSecondEnd = baseTime.plusSeconds(20);

        assertEquals(expectedFirstStart, firstDowntime.start(), "Wrong start of first downtime");
        assertEquals(expectedFirstEnd, firstDowntime.end(), "Wrong end of first downtime");

        assertEquals(expectedSecondStart, secondDowntime.start(), "Wrong start of second downtime");
        assertEquals(expectedSecondEnd, secondDowntime.end(), "Wrong end of second downtime");
    }

    @Test
    void shouldDetectDowntimeAtBeginningOfLog() throws IOException {
        int okCount = 0;
        int failCount = 10;
        generateFileFailEnd(okCount, failCount);
        process = startConsoleApp(99.9, 45.2);

        OffsetTime baseTime = LOG_FILE_GENERATOR.getBaseOffsetTime();
        OffsetTime expectedStart = baseTime.plusSeconds(1);
        List<String> strings = readDowntimeFromConsole(process, 1, TIME_OUT);
        DowntimeResult actualDownTime = extractDownTimeConsoleLog(strings.getFirst());
        OffsetTime expectedEnd = baseTime.plusSeconds(failCount);

        assertEquals(expectedStart, actualDownTime.start(), "Wrong start of beginning downtime");
        assertEquals(expectedEnd, actualDownTime.end(), "Wrong end of beginning downtime");
    }

    @Test
    void shouldDetectDowntimeAtEndOfLog() throws IOException {
        int okCount = 7;
        int failCount = 3;
        generateFileFailEnd(okCount, failCount);
        process = startConsoleApp(99.9, 45.2);

        OffsetTime baseTime = LOG_FILE_GENERATOR.getBaseOffsetTime();
        OffsetTime expectedStart = baseTime.plusSeconds(okCount + 1);
        List<String> strings = readDowntimeFromConsole(process, 1, TIME_OUT);
        DowntimeResult actualDownTime = extractDownTimeConsoleLog(strings.getFirst());
        OffsetTime expectedEnd = baseTime.plusSeconds(okCount + failCount);

        assertEquals(expectedStart, actualDownTime.start(), "Wrong start of ending downtime");
        assertEquals(expectedEnd, actualDownTime.end(), "Wrong end of ending downtime");
    }
}
