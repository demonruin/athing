package com.github.ompc.athing.component.dmgr.api.domain.usage;

/**
 * 存储使用率
 */
public class StoreUsage {

    /**
     * 挂载点
     */
    private String mount;

    /**
     * 可用空间(字节)
     */
    private long available;

    /**
     * 可用率(%)
     */
    private float availableRate;

    public String getMount() {
        return mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }

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
