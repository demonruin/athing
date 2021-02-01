package com.github.ompc.athing.aliyun.platform.message.decoder;

import com.github.ompc.athing.standard.platform.message.ThingMessage;
import com.github.ompc.athing.standard.platform.message.ThingReplyPropertySetMessage;
import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.ompc.athing.aliyun.framework.Constants.FEATURE_KEY_PROPERTY_SET_REPLY_SUCCESS_IDS;

/**
 * 属性设置应答消息解码器
 *
 * @see <a href="https://help.aliyun.com/document_detail/73736.html#title-9p8-2jl-sv4">设备下行指令结果</a>
 */
public class ThingReplyPropertySetMessageDecoder extends MessageDecoder<ThingReplyMessageDecoder.Header, ThingReplyMessageDecoder.Header> {

    @Override
    protected boolean matches(ThingReplyMessageDecoder.Header preHeader) {
        return preHeader.getReplyTopic().matches("^/sys/[^/]+/[^/]+/thing/service/property/set_reply$");
    }

    @Override
    protected ThingReplyMessageDecoder.Header decodeHeader(ThingReplyMessageDecoder.Header preHeader, JsonObject payloadJsonObject) {
        return preHeader;
    }

    // 从消息中解析出设置成功的属性ID集合
    private Set<String> parsePropertyIdentities(ThingReplyMessageDecoder.Header header) {
        final String successIdsStr = header.getReplyFeatureMap().get(FEATURE_KEY_PROPERTY_SET_REPLY_SUCCESS_IDS);
        return (null == successIdsStr || successIdsStr.isEmpty())
                ? Collections.emptySet()
                : Stream.of(successIdsStr.split(",")).collect(Collectors.toSet());
    }

    @Override
    protected ThingMessage decodePayload(ThingReplyMessageDecoder.Header header, JsonObject payloadJsonObject) {
        return new ThingReplyPropertySetMessage(
                header.getParent().getProductId(),
                header.getParent().getThingId(),
                header.getParent().getTimestamp(),
                header.getParent().getReqId(),
                header.getReplyCode(),
                header.getReplyMessage(),
                parsePropertyIdentities(header)
        );
    }

}
