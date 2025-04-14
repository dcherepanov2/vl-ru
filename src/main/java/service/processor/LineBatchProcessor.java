package service.processor;

import com.google.common.base.Stopwatch;
import domain.LogBatchProcessData;
import domain.LogFailureIntermediateResult;
import domain.LogFailureResult;
import service.mapper.DefaultLogFailureResultMapper;
import service.mapper.LogFailureResultMapper;
import service.parser.DefaultLogFailureIntermediateParser;
import service.parser.LogFailureIntermediateParser;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class LineBatchProcessor implements Runnable {

    private final BlockingQueue<String> inputLogQueue;
    private final BlockingQueue<LogBatchProcessData> logBatchProcessDataQueue;
    private final double allowedRequestCompleteMs;
    private final Stopwatch timeWatcher;
    private final LogBatchProcessData logBatchProcessData;
    private final LogFailureIntermediateParser logFailureIntermediateParser;
    private final LogFailureResultMapper logFailureResultMapper;
    private final int bufferSize;

    public LineBatchProcessor(
            BlockingQueue<String> queue,
            BlockingQueue<LogBatchProcessData> logBatchProcessDataQueue,
            double allowedRequestCompleteMs,
            int bufferSize
    ) {
        this.logFailureResultMapper = new DefaultLogFailureResultMapper();
        this.allowedRequestCompleteMs = allowedRequestCompleteMs;
        this.bufferSize = bufferSize;
        this.inputLogQueue = queue;
        this.logBatchProcessData = new LogBatchProcessData();
        this.logBatchProcessDataQueue = logBatchProcessDataQueue;
        this.logFailureIntermediateParser = new DefaultLogFailureIntermediateParser();
        this.timeWatcher = Stopwatch.createUnstarted();
    }

    @Override
    public void run() {
        timeWatcher.start();
        try {
            while (!Thread.currentThread().isInterrupted()) {
                process();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    private void process() throws InterruptedException {
        String line = inputLogQueue.take();
        Optional<LogFailureIntermediateResult> logFailureOptional = processLine(line);
        logBatchProcessData.incrementCountLines();
        if (logFailureOptional.isPresent()) {
            LogFailureIntermediateResult logFailureIntermediateResult = logFailureOptional.get();
            LogFailureResult logFailureResult = logFailureResultMapper.toResult(logFailureIntermediateResult);
            logBatchProcessData.addFailureLog(logFailureIntermediateResult);
            logBatchProcessData.updateTime(logFailureResult.logTime());

            if (isFlush()) {
                logBatchProcessData.incrementCountLines();
                logBatchProcessDataQueue.add(logBatchProcessData.clone());
                logBatchProcessData.clear();
                timeWatcher.reset();
                timeWatcher.start();
            }
        }
    }


    private boolean isFlush() {
        long elapsed = timeWatcher.elapsed(TimeUnit.MINUTES);
        return elapsed == 5 || logBatchProcessData.isBufferCanBeCleared(bufferSize);
    }

    private Optional<LogFailureIntermediateResult> processLine(String line) {
        return Optional.ofNullable(line)
                .filter(lineLocal -> !lineLocal.isEmpty())
                .map(logFailureIntermediateParser::parse)
                .filter(this::isFailureLog);
    }

    private boolean isFailureLog(LogFailureIntermediateResult result) {
        return isAllowedCompleteMs(result) || isAllowedResponseStatus(result);
    }

    private boolean isAllowedCompleteMs(LogFailureIntermediateResult result) {
        return result != null && result.completeMs() > allowedRequestCompleteMs;
    }

    private boolean isAllowedResponseStatus(LogFailureIntermediateResult result) {
        return result != null && Integer.parseInt(result.responseStatusCode()) >= 500;
    }
}