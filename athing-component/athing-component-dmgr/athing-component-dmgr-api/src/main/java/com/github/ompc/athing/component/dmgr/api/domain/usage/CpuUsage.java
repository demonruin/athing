package com.github.ompc.athing.component.dmgr.api.domain.usage;

/**
 * CPU使用率
 */
public class CpuUsage {

    private float user;
    private float nice;
    private float system;
    private float idle;
    private float iowait;
    private float irq;
    private float softIrq;
    private float steal;
    private float load01;
    private float load05;
    private float load15;

    /**
     * 上下文切换次数
     */
    private long contextSwitches;

    /**
     * 中断发生次数
     */
    private long interrupts;

    public float getUser() {
        return user;
    }

    public void setUser(float user) {
        this.user = user;
    }

    public float getNice() {
        return nice;
    }

    public void setNice(float nice) {
        this.nice = nice;
    }

    public float getSystem() {
        return system;
    }

    public void setSystem(float system) {
        this.system = system;
    }

    public float getIdle() {
        return idle;
    }

    public void setIdle(float idle) {
        this.idle = idle;
    }

    public float getIowait() {
        return iowait;
    }

    public void setIowait(float iowait) {
        this.iowait = iowait;
    }

    public float getIrq() {
        return irq;
    }

    public void setIrq(float irq) {
        this.irq = irq;
    }

    public float getSoftIrq() {
        return softIrq;
    }

    public void setSoftIrq(float softIrq) {
        this.softIrq = softIrq;
    }

    public float getSteal() {
        return steal;
    }

    public void setSteal(float steal) {
        this.steal = steal;
    }

    public float getLoad01() {
        return load01;
    }

    public void setLoad01(float load01) {
        this.load01 = load01;
    }

    public float getLoad05() {
        return load05;
    }

    public void setLoad05(float load05) {
        this.load05 = load05;
    }

    public float getLoad15() {
        return load15;
    }

    public void setLoad15(float load15) {
        this.load15 = load15;
    }

    public long getContextSwitches() {
        return contextSwitches;
    }

    public void setContextSwitches(long contextSwitches) {
        this.contextSwitches = contextSwitches;
    }

    public long getInterrupts() {
        return interrupts;
    }

    public void setInterrupts(long interrupts) {
        this.interrupts = interrupts;
    }
}
