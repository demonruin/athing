package com.github.ompc.athing.component.dmgr.api.domain.info;

/**
 * 内存信息
 */
public class MemoryInfo {

    /**
     * 物理内存容量
     */
    private long phyCap;

    /**
     * 虚拟内存容量
     */
    private long virCap;

    public long getPhyCap() {
        return phyCap;
    }

    public void setPhyCap(long phyCap) {
        this.phyCap = phyCap;
    }

    public long getVirCap() {
        return virCap;
    }

    public void setVirCap(long virCap) {
        this.virCap = virCap;
    }

}
