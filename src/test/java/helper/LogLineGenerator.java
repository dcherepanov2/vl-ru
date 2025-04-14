package helper;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Getter
public class LogLineGenerator {

    private static final DateTimeFormatter LOG_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss Z");

    private OffsetDateTime lastTimestamp;

    public LogLineGenerator() {
        lastTimestamp = createInitialTimestamp();
    }

    public String generate(int responseStatusCode, int completeMs) {
        lastTimestamp = lastTimestamp.plusSeconds(1);

        String method = "GET";
        String resource = "/rest/v1.4/documents?zone=default&_rid=6076537c";

        return String.format("192.168.32.181 - - [%s] \"%s %s HTTP/1.1\" %d 2 %d.0 \"-\" \"@list-item-updater\" prio:0",
                LOG_TIMESTAMP_FORMATTER.format(lastTimestamp),
                method,
                resource,
                responseStatusCode,
                completeMs
        );
    }

    private OffsetDateTime createInitialTimestamp() {
        return OffsetDateTime.of(
                2017,
                6,
                14,
                16,
                47,
                2,
                0,
                ZoneOffset.ofHours(10)
        );
    }
}
