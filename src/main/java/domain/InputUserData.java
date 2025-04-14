package domain;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Option;

@Getter
@Setter
public class InputUserData {

    @Option(names = {"-t"}, required = true)
    private double allowedCompleteSecond;

    @Option(names = {"-u"}, required = true)
    double allowedAvailabilityLevel;
}
