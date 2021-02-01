package com.github.ompc.athing.aliyun.platform.message.decoder;

import com.github.ompc.athing.aliyun.framework.component.meta.ThServiceMeta;
import com.github.ompc.athing.aliyun.framework.util.GsonFactory;
import com.github.ompc.athing.aliyun.platform.product.ThProductMeta;
import com.github.ompc.athing.standard.platform.message.ThingMessage;
import com.github.ompc.athing.standard.platform.message.ThingReplyServiceReturnMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 异步服务返回应答消息解码器
 *
 * @see <a href="https://help.aliyun.com/document_detail/73736.html#title-9p8-2jl-sv4">设备下行指令结果</a>
 */
public class ThingReplyServiceReturnMessageDecoder extends MessageDecoder<ThingReplyMessageDecoder.Header, ThingReplyMessageDecoder.Header> {

    private final Gson gson = GsonFactory.getGson();

    @Override
    protected boolean matches(ThingReplyMessageDecoder.Header preHeader) {
        return preHeader.getReplyTopic().matches("^/sys/[^/]+/[^/]+/thing/service/[^/]+_reply$");
    }

    // 从消息中解析服务ID
    private String parseServiceIdentity(ThingReplyMessageDecoder.Header preHeader) throws DecodeException {
        final String topic = preHeader.getReplyTopic();
        try {
            return topic.substring(
                    topic.lastIndexOf("/") + 1,
                    topic.lastIndexOf("_reply")
            );
        } catch (Exception cause) {
            throw new DecodeException(String.format("illegal service in topic=%s", topic), cause);
        }
    }

    @Override
    protected ThingReplyMessageDecoder.Header decodeHeader(ThingReplyMessageDecoder.Header preHeader, JsonObject payloadJsonObject) {
        return preHeader;
    }

    @Override
    protected ThingMessage decodePayload(ThingReplyMessageDecoder.Header header, JsonObject payloadJsonObject) throws DecodeException {

        // 检查服务是否存在
        final ThProductMeta productMeta = header.getParent().getProductMeta();
        final String identity = parseServiceIdentity(header);
        final ThServiceMeta serviceMeta = productMeta.getThServiceMeta(identity);
        if (null == serviceMeta) {
            throw new DecodeException(String.format("service is not defined in product=%s;identity=%s;",
                    header.getParent().getProductId(),
                    identity
            ));
        }

        return new ThingReplyServiceReturnMessage(
                header.getParent().getProductId(),
                header.getParent().getThingId(),
                header.getParent().getTimestamp(),
                header.getParent().getReqId(),
                header.getReplyCode(),
                header.getReplyMessage(),
                serviceMeta.getIdentifier(),
                gson.fromJson(required(payloadJsonObject, "data"), serviceMeta.getReturnType())
        );

    }

}
