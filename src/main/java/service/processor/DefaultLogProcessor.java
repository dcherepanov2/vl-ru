package service.processor;

import config.LineProcessorConfig;
import domain.InputUserData;
import domain.LogBatchProcessData;
import service.helper.DefaultResourceCalculator;
import service.helper.ResourceCalculator;
import service.parser.DefaultInputUserParser;
import service.parser.InputUserParser;
import java.util.concurrent.*;

public class DefaultLogProcessor implements LogProcessor {

    private final LineProcessorConfig lineProcessorConfig;
    private final BlockingQueue<String> lineConsoleBlockingQueue;
    private final BlockingQueue<LogBatchProcessData> logBatchProcessDataBlockingQueue;
    private final InputUserParser inputUserParser;

    public DefaultLogProcessor(LineProcessorConfig lineProcessorConfig) {
        this.lineProcessorConfig = lineProcessorConfig;
        this.logBatchProcessDataBlockingQueue = new LinkedBlockingQueue<>();
        this.lineConsoleBlockingQueue = new LinkedBlockingQueue<>();
        this.inputUserParser = new DefaultInputUserParser();
    }

    @Override
    public void start(String[] args) {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        InputUserData inputUserData = inputUserParser.parse(args);

        CompletableFuture<Void> workers = createWorkers(executorService, inputUserData);
        workers.join();

        executorService.shutdownNow();
    }

    private CompletableFuture<Void> createWorkers(ExecutorService executorService, InputUserData inputUserData) {
        return CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> configureConsoleWriteProcessor(inputUserData).run(), executorService),
                CompletableFuture.runAsync(() -> configureConsoleInputReadProcessor().run(), executorService),
                CompletableFuture.runAsync(() -> configureLineParseProcessor(inputUserData).run(), executorService)
        );
    }

    private ConsoleInputReadProcessor configureConsoleInputReadProcessor() {
        return new ConsoleInputReadProcessor(lineConsoleBlockingQueue);
    }

    private ConsoleWriteProcessor configureConsoleWriteProcessor(InputUserData inputUserData) {
        return new ConsoleWriteProcessor(logBatchProcessDataBlockingQueue, inputUserData.getAllowedAvailabilityLevel());
    }

    private LineBatchProcessor configureLineParseProcessor(InputUserData inputUserData) {
        ResourceCalculator resourceCalculator = new DefaultResourceCalculator(lineProcessorConfig);
        int optimalBufferSize = resourceCalculator.calculateOptimalBufferSize();
        return new LineBatchProcessor(
                lineConsoleBlockingQueue,
                logBatchProcessDataBlockingQueue,
                inputUserData.getAllowedCompleteSecond(),
                optimalBufferSize
        );
    }
}
