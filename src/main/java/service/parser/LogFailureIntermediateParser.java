package service.parser;

import domain.LogFailureIntermediateResult;

public interface LogFailureIntermediateParser {

    LogFailureIntermediateResult parse(String logLine);
}
