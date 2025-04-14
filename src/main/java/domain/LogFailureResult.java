package domain;


import java.time.OffsetTime;

public record LogFailureResult(OffsetTime logTime, double completeMs) {
}
