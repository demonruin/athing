package com.github.ompc.athing.aliyun.platform.message.decoder;

import com.github.ompc.athing.aliyun.framework.component.meta.ThPropertyMeta;
import com.github.ompc.athing.aliyun.framework.util.GsonFactory;
import com.github.ompc.athing.aliyun.platform.product.ThProductMeta;
import com.github.ompc.athing.standard.component.Identifier;
import com.github.ompc.athing.standard.platform.domain.ThingPropertySnapshot;
import com.github.ompc.athing.standard.platform.message.ThingMessage;
import com.github.ompc.athing.standard.platform.message.ThingPostPropertyMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备上报属性消息解码器
 *
 * @see <a href="https://help.aliyun.com/document_detail/73736.html#title-7r8-lbe-2m1">设备属性上报</a>
 */
public class ThingPostPropertyMessageDecoder extends MessageDecoder<ThingMessageDecoder.Header, ThingMessageDecoder.Header> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Gson gson = GsonFactory.getGson();

    @Override
    protected boolean matches(ThingMessageDecoder.Header preHeader) {
        return preHeader.getParent().getMessageTopic().matches("/[^/]+/[^/]+/thing/event/property/post");
    }

    @Override
    protected ThingMessageDecoder.Header decodeHeader(ThingMessageDecoder.Header preHeader, JsonObject payloadJsonObject) {
        return preHeader;
    }

    // 从消息体中解析属性集合
    private Map<String, ThingPropertySnapshot> parsePropertyMap(ThingMessageDecoder.Header header, JsonObject itemsJsonObject) {
        final ThProductMeta productMeta = header.getProductMeta();
        final Map<String, ThingPropertySnapshot> propertyValueMap = new HashMap<>();
        for (final Map.Entry<String, JsonElement> entry : itemsJsonObject.entrySet()) {
            final String identity = entry.getKey();
            final ThPropertyMeta propertyMeta = productMeta.getThPropertyMeta(identity);
            // 属性尚未提供
            if (null == propertyMeta) {
                logger.warn("thing-product:/{}/message/property/post receive message, but property not provided, thing={};identity={};",
                        header.getProductId(), header.getThingId(), identity);
                continue;
            }

            try {
                final JsonObject itemJsonObject = entry.getValue().getAsJsonObject();
                final long timestamp = getMemberAsLong(itemJsonObject, "time", header.getTimestamp());
                final Object value = gson.fromJson(required(itemJsonObject, "value"), propertyMeta.getPropertyType());
                propertyValueMap.put(
                        identity,
                        new ThingPropertySnapshot(Identifier.parseIdentity(identity), value, timestamp)
                );
            }

            // 这里捕获异常的原因是，单个属性的失败不影响整条消息的处理失败
            catch (Throwable cause) {
                logger.warn("thing-product:/{}/message/property/post parse properties message failure, ignore this property. thing={};identity={};",
                        header.getProductId(), header.getThingId(), identity, cause);
            }

        }
        return propertyValueMap;
    }

    @Override
    protected ThingMessage decodePayload(ThingMessageDecoder.Header header, JsonObject payloadJsonObject) throws DecodeException {

        final JsonObject itemsJsonObject = required(payloadJsonObject, "items").getAsJsonObject();

        return new ThingPostPropertyMessage(
                header.getProductId(),
                header.getThingId(),
                header.getTimestamp(),
                header.getReqId(),
                parsePropertyMap(header, itemsJsonObject)
        );

    }

}
