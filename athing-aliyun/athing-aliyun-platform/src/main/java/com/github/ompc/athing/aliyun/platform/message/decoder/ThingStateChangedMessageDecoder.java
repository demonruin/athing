package com.github.ompc.athing.aliyun.platform.message.decoder;

import com.github.ompc.athing.aliyun.framework.util.GsonFactory;
import com.github.ompc.athing.aliyun.platform.product.ThProductMeta;
import com.github.ompc.athing.standard.platform.message.ThingMessage;
import com.github.ompc.athing.standard.platform.message.ThingStateChangedMessage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

/**
 * 设备状态变更消息编解码器
 *
 * @see <a href="https://help.aliyun.com/document_detail/73736.html#title-2ll-4j3-1wx">设备上下线状态</a>
 */
public class ThingStateChangedMessageDecoder extends MessageDecoder<JmsMessageDecoder.Header, JmsMessageDecoder.Header> {

    private final Gson gson = GsonFactory.getGson();
    private final Map<String, ThProductMeta> productMetaMap;

    public ThingStateChangedMessageDecoder(Map<String, ThProductMeta> productMetaMap) {
        this.productMetaMap = productMetaMap;
    }

    @Override
    protected boolean matches(JmsMessageDecoder.Header preHeader) {
        return preHeader.getMessageTopic().matches("/as/mqtt/status/[^/]+/[^/]+");
    }

    @Override
    protected JmsMessageDecoder.Header decodeHeader(JmsMessageDecoder.Header preHeader, JsonObject payloadJsonObject) {
        return preHeader;
    }

    private ThingStateChangedMessage.State parseThingStateChangedEventState(ThingStateChanged changed) {
        switch (changed.status.toUpperCase()) {
            case "ONLINE":
                return ThingStateChangedMessage.State.ONLINE;
            case "OFFLINE":
                return ThingStateChangedMessage.State.OFFLINE;
            default:
                return ThingStateChangedMessage.State.UN_KNOW;
        }
    }

    @Override
    protected ThingMessage decodePayload(JmsMessageDecoder.Header header, JsonObject payloadJsonObject) throws DecodeException {
        final ThingStateChanged thingStateChanged = gson.fromJson(payloadJsonObject, ThingStateChanged.class);
        if (!productMetaMap.containsKey(thingStateChanged.productKey)) {
            throw new DecodeException(String.format("product not defined, product=%s", thingStateChanged.productKey));
        }

        final SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        utcDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            final long utcOccurTimestamp = utcDateFormat.parse(thingStateChanged.utcTime).getTime();
            final long utcLastTimestamp = utcDateFormat.parse(thingStateChanged.utcLastTime).getTime();
            return new ThingStateChangedMessage(
                    thingStateChanged.productKey,
                    thingStateChanged.deviceName,
                    utcOccurTimestamp,
                    parseThingStateChangedEventState(thingStateChanged),
                    utcLastTimestamp,
                    thingStateChanged.clientIp
            );
        } catch (ParseException cause) {
            throw new DecodeException(String.format("illegal utc format, occur=%s;last=%s;", thingStateChanged.utcTime, thingStateChanged.utcLastTime), cause);
        }
    }

    private static class ThingStateChanged {

        @SerializedName("status")
        String status;

        @SerializedName("productKey")
        String productKey;

        @SerializedName("deviceName")
        String deviceName;

        @SerializedName("time")
        String time;

        @SerializedName("utcTime")
        String utcTime;

        @SerializedName("lastTime")
        String lastTime;

        @SerializedName("utcLastTime")
        String utcLastTime;

        @SerializedName("clientIp")
        String clientIp;

    }

}
