package service.processor;

import com.google.common.base.Stopwatch;
import config.LineProcessorConfig;
import domain.InputUserData;
import domain.LogBatchProcessData;
import service.helper.DefaultResourceCalculator;
import service.helper.ResourceCalculator;
import service.parser.DefaultInputUserParser;
import service.parser.InputUserParser;

import java.util.List;
import java.util.concurrent.*;

public class DefaultLogProcessor implements LogProcessor {

    private final LineProcessorConfig lineProcessorConfig;
    private final BlockingQueue<String> lineConsoleBlockingQueue;
    private final BlockingQueue<LogBatchProcessData> logBatchProcessDataBlockingQueue;
    private final InputUserParser inputUserParser;
    private final Stopwatch stopwatch;

    public DefaultLogProcessor(LineProcessorConfig lineProcessorConfig) {
        this.lineProcessorConfig = lineProcessorConfig;
        this.logBatchProcessDataBlockingQueue = new LinkedBlockingQueue<>();
        this.lineConsoleBlockingQueue = new LinkedBlockingQueue<>();
        this.inputUserParser = new DefaultInputUserParser();
        this.stopwatch = Stopwatch.createUnstarted();
    }

    @Override
    public void start(String[] args) {
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        InputUserData inputUserData = inputUserParser.parse(args);
        stopwatch.start();

        List<CompletableFuture<?>> workers = createWorkers(executorService, inputUserData);
        boolean shouldContinue = true;

        while (shouldContinue && !executorService.isShutdown()) {
            shouldContinue = checkWorkersStatus(workers);
        }

        executorService.shutdownNow();
    }

    private boolean checkWorkersStatus(List<CompletableFuture<?>> workers) {
        if (!isTimeForWorkersChecker()) {
            return true;
        }

        boolean hasExceptions = workers.stream()
                .anyMatch(CompletableFuture::isCompletedExceptionally);

        return !hasExceptions;
    }

    private boolean isTimeForWorkersChecker(){
        long elapsed = stopwatch.elapsed(TimeUnit.SECONDS);
        if(elapsed > 5){
            stopwatch.reset().start();
            return true;
        }
        return false;
    }

    private List<CompletableFuture<?>> createWorkers(ExecutorService executorService, InputUserData inputUserData) {
        CompletableFuture<Void> consoleWriteProcessor =
                CompletableFuture.runAsync(() -> configureConsoleWriteProcessor(inputUserData).run(), executorService);
        CompletableFuture<Void> lineBatchProcessor =
                CompletableFuture.runAsync(() -> configureLineParseProcessor(inputUserData).run(), executorService);
        CompletableFuture<Void> consoleInputReadProcessor =
                CompletableFuture.runAsync(() -> configureConsoleInputReadProcessor().run(), executorService);
        return List.of(
                consoleWriteProcessor,
                consoleInputReadProcessor,
                lineBatchProcessor
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
