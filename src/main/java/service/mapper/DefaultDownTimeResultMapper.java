package service.mapper;

import domain.DowntimeResult;
import domain.LogBatchProcessData;
import java.time.OffsetTime;

public class DefaultDownTimeResultMapper implements DownTimeResultMapper {

    @Override
    public DowntimeResult toDownTimeResult(LogBatchProcessData logBatchProcessData, double realAvailabilityLevel) {
        OffsetTime timeStart = logBatchProcessData.getTimeStart();
        OffsetTime timeEnd = logBatchProcessData.getTimeEnd();
        return new DowntimeResult(timeStart, timeEnd, realAvailabilityLevel);
    }
}
