package com.github.ompc.athing.standard.platform.message;

/**
 * 设备应答消息
 */
public class ThingReplyMessage extends ThingMessage {

    private final String reqId;
    private final int code;
    private final String desc;

    /**
     * 设备应答消息
     *
     * @param type      消息类型
     * @param productId 产品ID
     * @param thingId   设备ID
     * @param timestamp 消息时间戳
     * @param reqId     请求ID
     * @param code      应答码
     * @param desc      应答描述
     */
    protected ThingReplyMessage(
            Type type, String productId, String thingId, long timestamp,
            String reqId, int code, String desc
    ) {
        super(type, productId, thingId, timestamp);
        this.reqId = reqId;
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取请求ID
     *
     * @return 请求ID
     */
    public String getReqId() {
        return reqId;
    }

    /**
     * 获取应答码
     *
     * @return 应答码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取应答描述
     *
     * @return 应答描述
     */
    public String getDesc() {
        return desc;
    }

}
