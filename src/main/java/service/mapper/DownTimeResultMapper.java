package service.mapper;

import domain.DowntimeResult;
import domain.LogBatchProcessData;
import domain.LogBatchStatistic;

public interface DownTimeResultMapper {

    DowntimeResult toDownTimeResult(LogBatchProcessData logBatchProcessData, double realAvailabilityLevel);
}
