package com.github.ompc.athing.standard.platform.message;

import java.util.Collections;
import java.util.Set;

import static com.github.ompc.athing.standard.platform.message.ThingMessage.Type.THING_REPLY_PROPERTIES_SET;

/**
 * 设备组件属性设置应答消息
 */
public class ThingReplyPropertySetMessage extends ThingReplyMessage {

    /**
     * 设置成功属性标识值集合
     */
    private final Set<String> successIdentities;

    /**
     * 设备属性设置应答消息
     *
     * @param productId         产品ID
     * @param thingId           设备ID
     * @param timestamp         消息时间戳
     * @param reqId             请求ID
     * @param code              应答码
     * @param desc              应答描述
     * @param successIdentities 成功属性标识集合
     */
    public ThingReplyPropertySetMessage(
            String productId, String thingId, long timestamp,
            String reqId, int code, String desc,
            Set<String> successIdentities
    ) {
        super(THING_REPLY_PROPERTIES_SET, productId, thingId, timestamp, reqId, code, desc);
        this.successIdentities = Collections.unmodifiableSet(successIdentities);
    }

    /**
     * 获取设置成功属性标识值集合
     *
     * @return 设置成功属性标识值集合
     */
    public Set<String> getSuccessIdentities() {
        return successIdentities;
    }

}
