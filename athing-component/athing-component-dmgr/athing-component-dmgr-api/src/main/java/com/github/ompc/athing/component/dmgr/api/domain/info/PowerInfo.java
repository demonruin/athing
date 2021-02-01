package com.github.ompc.athing.component.dmgr.api.domain.info;

/**
 * 电源信息
 */
public class PowerInfo {

    /**
     * 电源名称
     */
    private String name;

    /**
     * 电源容量
     */
    private int cap;

    /**
     * 容量单位
     */
    private String unit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCap() {
        return cap;
    }

    public void setCap(int cap) {
        this.cap = cap;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
