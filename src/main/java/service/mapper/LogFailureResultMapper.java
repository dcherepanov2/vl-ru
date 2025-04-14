package service.mapper;

import domain.LogFailureIntermediateResult;
import domain.LogFailureResult;

public interface LogFailureResultMapper {

    LogFailureResult toResult(LogFailureIntermediateResult logFailureIntermediateResult);
}
