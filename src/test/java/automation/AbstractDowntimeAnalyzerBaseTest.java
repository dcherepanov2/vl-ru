package automation;

import domain.DowntimeResult;
import helper.LogFileGenerator;
import org.junit.jupiter.api.AfterEach;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import static constant.RegexConstant.REGEX_DOWNTIME;

public abstract class AbstractDowntimeAnalyzerBaseTest {

    protected static final String LOG_FILE_PATH = "src/test/resources/access.log";
    protected static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ssXXX");
    protected static final LogFileGenerator LOG_FILE_GENERATOR = new LogFileGenerator();
    protected static final Duration TIME_OUT = Duration.ofSeconds(30);
    protected Process process;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    protected Process startConsoleApp(double allowedAvailability, double allowedCompleteMs) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "java",
                "-jar",
                "target/vl-ru-1.0-SNAPSHOT-jar-with-dependencies.jar",
                "-u", String.valueOf(allowedAvailability),
                "-t", String.valueOf(allowedCompleteMs)
        );
        builder.redirectInput(new File(LOG_FILE_PATH));
        builder.redirectErrorStream(true);
        return builder.start();
    }


    protected List<String> readDowntimeFromConsole(Process process, int countLines, Duration time) {
        final List<String> lines = new ArrayList<>();
        try {
            Future<?> future = executor.submit(() -> {
                for (int numberLine = 0; numberLine < countLines; numberLine++) {
                    String line = readLineWithTimeout(process);
                    if (line.isEmpty()) {
                        break;
                    }
                    lines.add(line);
                }
            });
            future.get(time.toSeconds(), TimeUnit.SECONDS);
            return lines;
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        } catch (TimeoutException e) {
            return lines;
        } finally {
            executor.shutdownNow();
        }
    }

    protected DowntimeResult extractDownTimeConsoleLog(String outputLine) {
        Optional<String> downtimeLineOpt = Optional.ofNullable(outputLine)
                .filter(line -> line.matches(REGEX_DOWNTIME));

        if (downtimeLineOpt.isPresent()) {
            String downtimeLine = downtimeLineOpt.get();
            String[] parts = downtimeLine.trim().split("\\s+");

            OffsetTime actualStart = OffsetTime.parse(parts[0], TIME_FORMATTER);
            OffsetTime actualEnd = OffsetTime.parse(parts[1], TIME_FORMATTER);
            double actualAvailabilityLevel = Double.parseDouble(parts[2]);

            return new DowntimeResult(actualStart, actualEnd, actualAvailabilityLevel);
        } else {
            throw new NoSuchElementException("Down time not found");
        }
    }

    protected void generateFileFailEnd(int okCount, int failCount) throws IOException {
        Path path = Path.of(LOG_FILE_PATH);
        LogFileGenerator logFileGenerator = new LogFileGenerator();
        List<String> logs = logFileGenerator.createLinesFailEnd(okCount, failCount);
        logFileGenerator.createFile(path, logs);
    }

    protected void generateMixStringFile(int totalLines) throws IOException {
        Path path = Path.of(LOG_FILE_PATH);
        LogFileGenerator logFileGenerator = new LogFileGenerator();
        List<String> logs = logFileGenerator.createMixLines(totalLines);
        logFileGenerator.createFile(path, logs);
    }

    protected void generateFileNotAllowedCompleteMs(int totalLines, int completeMs) throws IOException {
        Path path = Path.of(LOG_FILE_PATH);
        LogFileGenerator logFileGenerator = new LogFileGenerator();
        List<String> logs = logFileGenerator.createLineWithNotAllowedCompleteMs(totalLines, completeMs);
        logFileGenerator.createFile(path, logs);
    }


    public String readLineWithTimeout(Process process) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            System.out.println(line);

            return line;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Paths.get(LOG_FILE_PATH));
        process.destroy();
    }
}
