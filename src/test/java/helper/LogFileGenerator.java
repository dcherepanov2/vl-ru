package helper;

import java.io.IOException;
import java.nio.file.*;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;

public class LogFileGenerator {

    private final LogLineGenerator generator = new LogLineGenerator();

    public List<String> createLineWithNotAllowedCompleteMs(int totalCount, int completeMs) {
        List<String> lines = new ArrayList<>();
        for (int numberLine = 0; numberLine < totalCount; numberLine++) {
            lines.add(generator.generate(200, completeMs + 1));
        }
        return lines;
    }

    public List<String> createLinesFailEnd(int okCount, int failCount) {
        List<String> lines = new ArrayList<>();

        for (int numberLine = 0; numberLine < okCount; numberLine++) {
            lines.add(generator.generate(200, 0));
        }
        for (int numberLine = 0; numberLine < failCount; numberLine++) {
            lines.add(generator.generate(500, 1000000));
        }

        return lines;
    }

    public List<String> createMixLines(int totalCount) {
        List<String> lines = new ArrayList<>();

        for (int numberLine = 0; numberLine < totalCount; numberLine++) {
            if (numberLine % 2 == 0) {
                lines.add(generator.generate(200, 0));
            } else {
                lines.add(generator.generate(500, 1000000));
            }
        }

        return lines;
    }

    public void createFile(Path outputPath, List<String> lines) throws IOException {
        Files.write(outputPath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public OffsetTime getBaseOffsetTime() {
        return generator.getLastTimestamp().toOffsetTime();
    }
}

