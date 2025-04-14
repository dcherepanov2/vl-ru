package service.parser;

import domain.InputUserData;
import picocli.CommandLine;

public class DefaultInputUserParser implements InputUserParser {

    @Override
    public InputUserData parse(String[] args) {
        InputUserData inputUserData = new InputUserData();
        CommandLine commandLine = new CommandLine(inputUserData);
        commandLine.parseArgs(args);
        return inputUserData;
    }
}
