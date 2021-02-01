package com.github.ompc.athing.aliyun.platform;

/**
 * 设备平台连接密钥
 */
public class ThingPlatformAccessKey {

    private final String accessKeyId;
    private final String accessKeySecret;

    /**
     * 设备平台连接密钥
     *
     * @param accessKeyId     ACCESS_KEY_ID
     * @param accessKeySecret ACCESS_KEY_SECRET
     */
    public ThingPlatformAccessKey(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }
}
