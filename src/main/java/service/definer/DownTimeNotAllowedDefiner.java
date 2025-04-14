package service.definer;

import domain.DowntimeResult;
import domain.LogBatchProcessData;
import domain.LogBatchStatistic;

import java.util.List;
import java.util.Optional;

public interface DownTimeNotAllowedDefiner {

    Optional<DowntimeResult> define(LogBatchProcessData logBatchProcessData, double allowAvailabilityLevel);
}
