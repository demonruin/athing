package com.github.ompc.athing.standard.platform.message;

/**
 * 设备上报消息
 */
public class ThingPostMessage extends ThingMessage {

    private final String reqId;

    /**
     * 设备上报消息
     *
     * @param type      消息类型
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param timestamp 消息时间戳
     * @param reqId     请求ID
     */
    protected ThingPostMessage(
            Type type, String productId, String thingId, long timestamp,
            String reqId
    ) {
        super(type, productId, thingId, timestamp);
        this.reqId = reqId;
    }

    /**
     * 获取请求ID
     *
     * @return 请求ID
     */
    public String getReqId() {
        return reqId;
    }

}
