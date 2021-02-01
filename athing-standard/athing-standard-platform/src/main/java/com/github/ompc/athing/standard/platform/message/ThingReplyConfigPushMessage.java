package com.github.ompc.athing.standard.platform.message;

import static com.github.ompc.athing.standard.platform.message.ThingMessage.Type.THING_REPLY_PUSH_CONFIG;

/**
 * 设备配置应答消息
 */
public class ThingReplyConfigPushMessage extends ThingReplyMessage {

    private final String scope;
    private final String version;

    /**
     * 设备配置应答消息
     *
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param timestamp 消息时间戳
     * @param reqId     请求ID
     * @param code      应答码
     * @param desc      应答描述
     * @param scope     配置范围
     * @param version   配置版本
     */
    public ThingReplyConfigPushMessage(
            String productId, String thingId, long timestamp,
            String reqId, int code, String desc,
            String scope, String version
    ) {
        super(THING_REPLY_PUSH_CONFIG, productId, thingId, timestamp, reqId, code, desc);
        this.scope = scope;
        this.version = version;
    }

    /**
     * 获取配置范围
     *
     * @return 配置范围
     */
    public String getScope() {
        return scope;
    }

    /**
     * 获取配置版本
     *
     * @return 配置版本
     */
    public String getVersion() {
        return version;
    }

}
