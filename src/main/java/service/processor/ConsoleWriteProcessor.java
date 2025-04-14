package service.processor;

import domain.DowntimeResult;
import domain.LogBatchProcessData;
import lombok.extern.log4j.Log4j2;
import service.definer.DefaultDownTimeNotAllowedDefiner;
import service.definer.DownTimeNotAllowedDefiner;
import service.mapper.DefaultDownTimeResultMapper;
import service.mapper.DownTimeResultMapper;

import java.util.concurrent.BlockingQueue;

@Log4j2
public class ConsoleWriteProcessor implements Runnable {

    private final BlockingQueue<LogBatchProcessData> logBatchStatisticsQueue;
    private final DownTimeNotAllowedDefiner downTimeNotAllowedDefiner;
    private final double allowedAvailabilityLevel;

    public ConsoleWriteProcessor(
            BlockingQueue<LogBatchProcessData> logBatchStatisticsQueue,
            double allowedAvailabilityLevel
    ) {
        DownTimeResultMapper downTimeResultMapper = new DefaultDownTimeResultMapper();
        this.allowedAvailabilityLevel = allowedAvailabilityLevel;
        this.logBatchStatisticsQueue = logBatchStatisticsQueue;
        this.downTimeNotAllowedDefiner = new DefaultDownTimeNotAllowedDefiner(downTimeResultMapper);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                LogBatchProcessData logBatchProcessData = logBatchStatisticsQueue.take();
                downTimeNotAllowedDefiner.define(logBatchProcessData, allowedAvailabilityLevel)
                        .ifPresent(this::log);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("InputWriteProcessor was interrupted", e);
        }
    }

    private void log(DowntimeResult downtimeResult){
        System.out.println(downtimeResult);
    }
}
