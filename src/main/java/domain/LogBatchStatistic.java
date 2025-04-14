package domain;

import java.util.List;

public record LogBatchStatistic(
        int sizeAllLines,
        List<LogFailureIntermediateResult> logFailureIntermediateResults,
        String dateStart,
        String dateEnd
) {
}
