package service.mapper;

import domain.LogFailureIntermediateResult;
import domain.LogFailureResult;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static java.time.OffsetTime.parse;

public class DefaultLogFailureResultMapper implements LogFailureResultMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss Z");

    @Override
    public LogFailureResult toResult(LogFailureIntermediateResult logFailureIntermediateResult) {
        OffsetTime offsetLogTime = toOffsetLogTime(logFailureIntermediateResult);
        return new LogFailureResult(offsetLogTime, logFailureIntermediateResult.completeMs());
    }

    private OffsetTime toOffsetLogTime(LogFailureIntermediateResult logFailureIntermediateResult) {
        return Optional.of(logFailureIntermediateResult)
                .map(LogFailureIntermediateResult::dateTimeStr)
                .map(dateTimeStr -> parse(dateTimeStr, FORMATTER))
                .orElseThrow(() -> new IllegalArgumentException(logFailureIntermediateResult.dateTimeStr()));
    }
}
