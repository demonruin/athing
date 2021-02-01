package com.github.ompc.athing.component.dmgr.api.domain.info;

/**
 * 处理器信息
 */
public class CpuInfo {

    /**
     * CPU ID
     */
    private String id;

    /**
     * CPU 标识信息
     */
    private String identity;

    /**
     * 逻辑核数
     */
    private int logicCnt;

    /**
     * 物理核数
     */
    private int phyCnt;

    /**
     * 物理芯片数
     */
    private int phyPkgCnt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public int getLogicCnt() {
        return logicCnt;
    }

    public void setLogicCnt(int logicCnt) {
        this.logicCnt = logicCnt;
    }

    public int getPhyCnt() {
        return phyCnt;
    }

    public void setPhyCnt(int phyCnt) {
        this.phyCnt = phyCnt;
    }

    public int getPhyPkgCnt() {
        return phyPkgCnt;
    }

    public void setPhyPkgCnt(int phyPkgCnt) {
        this.phyPkgCnt = phyPkgCnt;
    }

}
