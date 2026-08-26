package dev.aftermath.sdk.model;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class SystemSnapshot {
    private long freeMemoryBytes;
    private long totalMemoryBytes;
    private long maxMemoryBytes;
    private int availableProcessors;
    private int activeThreadCount;
    private double systemCpuLoad;

    public SystemSnapshot() {
        Runtime runtime = Runtime.getRuntime();
        this.freeMemoryBytes = runtime.freeMemory();
        this.totalMemoryBytes = runtime.totalMemory();
        this.maxMemoryBytes = runtime.maxMemory();
        this.availableProcessors = runtime.availableProcessors();
        this.activeThreadCount = Thread.activeCount();

        try {
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            if (osBean instanceof com.sun.management.OperatingSystemMXBean sunBean) {
                this.systemCpuLoad = Math.round(sunBean.getCpuLoad() * 100.0) / 100.0;
            } else {
                this.systemCpuLoad = osBean.getSystemLoadAverage();
            }
        } catch (Exception ignored) {
            this.systemCpuLoad = -1.0;
        }
    }

    public long getFreeMemoryBytes() {
        return freeMemoryBytes;
    }

    public void setFreeMemoryBytes(long freeMemoryBytes) {
        this.freeMemoryBytes = freeMemoryBytes;
    }

    public long getTotalMemoryBytes() {
        return totalMemoryBytes;
    }

    public void setTotalMemoryBytes(long totalMemoryBytes) {
        this.totalMemoryBytes = totalMemoryBytes;
    }

    public long getMaxMemoryBytes() {
        return maxMemoryBytes;
    }

    public void setMaxMemoryBytes(long maxMemoryBytes) {
        this.maxMemoryBytes = maxMemoryBytes;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public int getActiveThreadCount() {
        return activeThreadCount;
    }

    public void setActiveThreadCount(int activeThreadCount) {
        this.activeThreadCount = activeThreadCount;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }
}
