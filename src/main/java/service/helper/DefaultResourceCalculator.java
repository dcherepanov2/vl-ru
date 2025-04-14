package service.helper;

import config.LineProcessorConfig;

public class DefaultResourceCalculator implements ResourceCalculator {

    private final LineProcessorConfig lineProcessorConfig;

    private static final int APPROX_BYTES_PER_LINE = 600;

    public DefaultResourceCalculator(LineProcessorConfig lineProcessorConfig) {
        this.lineProcessorConfig = lineProcessorConfig;
    }

    @Override
    public int calculateOptimalBufferSize() {
        long workerThreads = lineProcessorConfig.getWorkerThread();
        long lineProcessorBufferSize = lineProcessorConfig.getBufferSize();
        long availableMemory = Math.round(Runtime.getRuntime().freeMemory() * 0.1);
        long memoryPerThread = availableMemory / workerThreads;
        long optimalBufferSize = memoryPerThread / APPROX_BYTES_PER_LINE;
        return Math.toIntExact(Math.min(optimalBufferSize, lineProcessorBufferSize));
    }
}
