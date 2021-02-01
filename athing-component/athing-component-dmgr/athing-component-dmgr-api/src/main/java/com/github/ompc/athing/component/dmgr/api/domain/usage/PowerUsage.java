package com.github.ompc.athing.component.dmgr.api.domain.usage;

/**
 * 电源使用率
 */
public class PowerUsage {

    /**
     * 电源名
     */
    private String name;

    /**
     * 剩余电量
     */
    private int remaining;

    /**
     * 剩余电量比率(%)
     */
    private float remainingRate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public float getRemainingRate() {
        return remainingRate;
    }

    public void setRemainingRate(float remainingRate) {
        this.remainingRate = remainingRate;
    }
}
