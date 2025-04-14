package config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LineProcessorConfig {

    private int bufferSize;

    private int workerThread;
}