package service.definer;

import domain.DowntimeResult;
import domain.LogBatchProcessData;
import service.mapper.DownTimeResultMapper;

import java.util.Optional;

public class DefaultDownTimeNotAllowedDefiner implements DownTimeNotAllowedDefiner {

    private final DownTimeResultMapper downTimeResultMapper;

    public DefaultDownTimeNotAllowedDefiner(DownTimeResultMapper downTimeResultMapper) {
        this.downTimeResultMapper = downTimeResultMapper;
    }

    @Override
    public Optional<DowntimeResult> define(LogBatchProcessData logBatchStatistic, double allowAvailabilityLevel) {
        double realAvailabilityLevel = calculateBatchAvailabilityLevel(logBatchStatistic);
        return Optional.of(logBatchStatistic)
                .filter(logBatchStatisticLocal -> isNotAllowedAvailabilityLevel(logBatchStatisticLocal, allowAvailabilityLevel))
                .map(logBatchStatisticLocal -> downTimeResultMapper.toDownTimeResult(logBatchStatisticLocal, realAvailabilityLevel));
    }

    private boolean isNotAllowedAvailabilityLevel(LogBatchProcessData logBatchProcessData, double allowedAvailabilityLevel) {
        double realAvailabilityLevel = calculateBatchAvailabilityLevel(logBatchProcessData);
        return realAvailabilityLevel < allowedAvailabilityLevel;
    }

    private double calculateBatchAvailabilityLevel(LogBatchProcessData logBatchProcessData) {
        int countLines = logBatchProcessData.getCountLines();
        int failureLogCountLines = logBatchProcessData.getBufferSize();
        return ((countLines - failureLogCountLines) / (double) countLines) * 100;
    }
}
