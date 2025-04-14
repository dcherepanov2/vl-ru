package constant;

public class RegexConstant {

    public static final String REGEX_LOG_LINE = "^.*\\[\\d{2}/\\d{2}/\\d{4}:(\\d{2}:\\d{2}:\\d{2} [+-]\\d{4})].+\"(?:GET|POST|PUT|DELETE).+?\" (\\d{3}) .+?(\\d+\\.\\d+).*$";
    public static final String REGEX_DOWNTIME = ".*\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}.*";
}
