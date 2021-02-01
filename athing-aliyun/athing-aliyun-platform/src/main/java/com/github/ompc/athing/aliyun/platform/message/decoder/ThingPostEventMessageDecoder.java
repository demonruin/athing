package com.github.ompc.athing.aliyun.platform.message.decoder;

import com.github.ompc.athing.aliyun.framework.component.meta.ThEventMeta;
import com.github.ompc.athing.aliyun.framework.util.GsonFactory;
import com.github.ompc.athing.aliyun.platform.product.ThProductMeta;
import com.github.ompc.athing.standard.platform.message.ThingMessage;
import com.github.ompc.athing.standard.platform.message.ThingPostEventMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * 设备上报事件消息解码器
 *
 * @see <a href="https://help.aliyun.com/document_detail/73736.html#title-ary-z3g-ftr">设备事件上报</a>
 */
public class ThingPostEventMessageDecoder extends MessageDecoder<ThingMessageDecoder.Header, ThingMessageDecoder.Header> {

    private final Gson gson = GsonFactory.getGson();

    @Override
    protected boolean matches(ThingMessageDecoder.Header preHeader) {
        final String topic = preHeader.getParent().getMessageTopic();
        return topic.matches("/[^/]+/[^/]+/thing/event/[^/]+/post")
                && !topic.endsWith("/thing/event/property/post");
    }

    @Override
    protected ThingMessageDecoder.Header decodeHeader(ThingMessageDecoder.Header preHeader, JsonObject payloadJsonObject) {
        return preHeader;
    }

    @Override
    protected ThingMessage decodePayload(ThingMessageDecoder.Header header, JsonObject payloadJsonObject) throws DecodeException {

        final ThProductMeta productMeta = header.getProductMeta();
        final String identity = required(payloadJsonObject, "identifier").getAsString();
        final ThEventMeta eventMeta = productMeta.getThEventMeta(identity);
        if (null == eventMeta) {
            throw new DecodeException(String.format("event is not provided in product=%s;identity=%s",
                    header.getProductId(),
                    identity
            ));
        }

        return new ThingPostEventMessage(
                header.getProductId(),
                header.getThingId(),
                header.getTimestamp(),
                header.getReqId(),
                eventMeta.getIdentifier(),
                gson.fromJson(required(payloadJsonObject, "value"), eventMeta.getType()),
                getMemberAsLong(payloadJsonObject, "time", header.getTimestamp())
        );

    }

}
