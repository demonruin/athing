package com.github.ompc.athing.aliyun.thing;

/**
 * 设备启动选项
 */
public class ThingConnectOptions {

    /**
     * 设备连接超时(毫秒)
     */
    private long connectTimeoutMs = 1000 * 30L;

    /**
     * 设备心跳维持间隔(毫秒)
     */
    private long keepAliveIntervalMs = 1000 * 90L;

    /**
     * 获取配置超时(毫秒)
     */
    private long configTimeoutMs = 1000 * 60L;

    /**
     * 获取升级超时(毫秒)
     */
    private long upgradeTimeoutMs = 1000 * 60L * 3;

    /**
     * 获取连接区域
     */
    private String connectRegion = "cn-shanghai";

    /**
     * 工作线程数
     */
    private int threads = 20;

    public long getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(long connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public long getConfigTimeoutMs() {
        return configTimeoutMs;
    }

    public void setConfigTimeoutMs(long configTimeoutMs) {
        this.configTimeoutMs = configTimeoutMs;
    }

    public long getUpgradeTimeoutMs() {
        return upgradeTimeoutMs;
    }

    public void setUpgradeTimeoutMs(long upgradeTimeoutMs) {
        this.upgradeTimeoutMs = upgradeTimeoutMs;
    }

    public long getKeepAliveIntervalMs() {
        return keepAliveIntervalMs;
    }

    public void setKeepAliveIntervalMs(long keepAliveIntervalMs) {
        this.keepAliveIntervalMs = keepAliveIntervalMs;
    }

    public String getConnectRegion() {
        return connectRegion;
    }

    public void setConnectRegion(String connectRegion) {
        this.connectRegion = connectRegion;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
}
