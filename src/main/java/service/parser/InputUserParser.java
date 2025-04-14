package service.parser;

import domain.InputUserData;

public interface InputUserParser {

    InputUserData parse(String[] args);
}
