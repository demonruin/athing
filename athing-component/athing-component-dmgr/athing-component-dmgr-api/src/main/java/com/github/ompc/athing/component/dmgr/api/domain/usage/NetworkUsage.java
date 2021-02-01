package com.github.ompc.athing.component.dmgr.api.domain.usage;

/**
 * 网络使用率
 */
public class NetworkUsage {

    /**
     * 网络名称
     */
    private String name;

    /**
     * 已接收(字节)
     */
    private long recv;

    /**
     * 已发送(字节)
     */
    private long sent;

    /**
     * 已接收包
     */
    private long recvPkt;

    /**
     * 已发送包
     */
    private long sentPkt;

    /**
     * 错包
     */
    private long errorPkt;

    /**
     * 丢包
     */
    private long dropPkt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRecv() {
        return recv;
    }

    public void setRecv(long recv) {
        this.recv = recv;
    }

    public long getSent() {
        return sent;
    }

    public void setSent(long sent) {
        this.sent = sent;
    }

    public long getRecvPkt() {
        return recvPkt;
    }

    public void setRecvPkt(long recvPkt) {
        this.recvPkt = recvPkt;
    }

    public long getSentPkt() {
        return sentPkt;
    }

    public void setSentPkt(long sentPkt) {
        this.sentPkt = sentPkt;
    }

    public long getErrorPkt() {
        return errorPkt;
    }

    public void setErrorPkt(long errorPkt) {
        this.errorPkt = errorPkt;
    }

    public long getDropPkt() {
        return dropPkt;
    }

    public void setDropPkt(long dropPkt) {
        this.dropPkt = dropPkt;
    }
}
