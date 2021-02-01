package com.github.ompc.athing.component.dmgr.api.domain.usage;

/**
 * 内存使用率
 */
public class MemoryUsage {

    /**
     * 可用空间(字节)
     */
    private long available;

    /**
     * 可用率(%)
     */
    private float availableRate;

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public float getAvailableRate() {
        return availableRate;
    }

    public void setAvailableRate(float availableRate) {
        this.availableRate = availableRate;
    }
}
