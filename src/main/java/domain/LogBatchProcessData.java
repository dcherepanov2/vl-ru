package domain;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LogBatchProcessData implements Cloneable {

    private OffsetTime timeStart;
    private OffsetTime timeEnd;
    private List<LogFailureIntermediateResult> buffer;
    private int countLines;

    public LogBatchProcessData() {
        this.timeStart = OffsetTime.MAX;
        this.timeEnd = OffsetTime.MIN;
        this.buffer = new ArrayList<>();
    }

    public boolean isBufferCanBeCleared(int bufferSize) {
        return countLines >= bufferSize;
    }

    public void incrementCountLines() {
        countLines++;
    }

    public void addFailureLog(LogFailureIntermediateResult logFailureIntermediateResult) {
        buffer.add(logFailureIntermediateResult);
    }

    public void updateTime(OffsetTime time) {
        boolean isAfterCurrentTimeStart = timeStart.isAfter(time);
        boolean isBeforeCurrentTimeEnd = timeEnd.isBefore(time);
        if (isAfterCurrentTimeStart) {
            timeStart = time;
        }
        if (isBeforeCurrentTimeEnd) {
            timeEnd = time;
        }
    }

    public void clear() {
        countLines = 0;
        buffer.clear();
        timeEnd = OffsetTime.MIN;
        timeStart = OffsetTime.MAX;
    }

    public int getBufferSize() {
        return buffer.size();
    }

    @Override
    public LogBatchProcessData clone() {
        try {
            LogBatchProcessData logBatchProcessData = (LogBatchProcessData) super.clone();
            logBatchProcessData.setBuffer(new ArrayList<>(buffer));
            logBatchProcessData.setCountLines(countLines);
            logBatchProcessData.setTimeStart(timeStart);
            logBatchProcessData.setTimeEnd(timeEnd);
            return logBatchProcessData;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String toString() {
        return "LogBatchProcessData{" +
                "timeStart=" + timeStart +
                ", timeEnd=" + timeEnd +
                ", buffer=" + buffer +
                ", countLines=" + countLines +
                '}';
    }
}
