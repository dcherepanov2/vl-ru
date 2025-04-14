package service.parser;

import domain.LogFailureIntermediateResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static constant.RegexConstant.REGEX_LOG_LINE;

public class DefaultLogFailureIntermediateParser implements LogFailureIntermediateParser {

    @Override
    public LogFailureIntermediateResult parse(String logLine) {
        Pattern pattern = Pattern.compile(REGEX_LOG_LINE);
        Matcher matcher = pattern.matcher(logLine);

        if (matcher.find()) {
            String dateTimeStr = matcher.group(1);
            String responseStatus = matcher.group(2);
            double executionTime = Double.parseDouble(matcher.group(3));
            return new LogFailureIntermediateResult(dateTimeStr, responseStatus, executionTime);
        }

        throw new IllegalStateException("Log file contains string not allowed format.");
    }
}
