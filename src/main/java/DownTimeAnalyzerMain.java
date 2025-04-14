import config.LineProcessorConfig;
import config.PropertiesConfig;
import service.processor.DefaultLogProcessor;
import service.processor.LogProcessor;

public class DownTimeAnalyzerMain {

    public static void main(String[] args) {
        PropertiesConfig propertiesConfig = new PropertiesConfig();
        LineProcessorConfig lineProcessor = propertiesConfig.getConfig().getLineProcessor();
        LogProcessor logProcessor = new DefaultLogProcessor(lineProcessor);
        logProcessor.start(args);
    }
}
