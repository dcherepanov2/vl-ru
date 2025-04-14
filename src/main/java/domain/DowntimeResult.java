package domain;

import java.time.OffsetTime;

public record DowntimeResult(OffsetTime start, OffsetTime end, double realAvailabilityLevel) {

    @Override
    public String toString() {
        return String.format("%s %s %s", start, end, realAvailabilityLevel);
    }
}
