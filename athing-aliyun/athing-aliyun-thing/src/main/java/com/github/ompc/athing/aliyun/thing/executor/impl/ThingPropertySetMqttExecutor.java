package com.github.ompc.athing.aliyun.thing.executor.impl;

import com.github.ompc.athing.aliyun.framework.component.meta.ThPropertyMeta;
import com.github.ompc.athing.aliyun.framework.util.GsonFactory;
import com.github.ompc.athing.aliyun.thing.ThingImpl;
import com.github.ompc.athing.aliyun.thing.container.ThComStub;
import com.github.ompc.athing.aliyun.thing.executor.MqttExecutor;
import com.github.ompc.athing.aliyun.thing.executor.MqttPoster;
import com.github.ompc.athing.standard.component.Identifier;
import com.github.ompc.athing.standard.thing.ThingException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.github.ompc.athing.aliyun.framework.Constants.FEATURE_KEY_PROPERTY_SET_REPLY_SUCCESS_IDS;
import static com.github.ompc.athing.aliyun.framework.util.FeatureCodec.encode;
import static com.github.ompc.athing.aliyun.thing.executor.MqttPoster.MQTT_QOS_AT_LEAST_ONCE;
import static com.github.ompc.athing.aliyun.thing.executor.impl.AlinkReplyImpl.success;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 设备属性赋值执行器
 */
public class ThingPropertySetMqttExecutor implements MqttExecutor, MqttExecutor.MqttMessageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Gson gson = GsonFactory.getGson();
    private final JsonParser parser = new JsonParser();

    private final ThingImpl thing;
    private final MqttPoster poster;

    public ThingPropertySetMqttExecutor(ThingImpl thing, MqttPoster poster) {
        this.thing = thing;
        this.poster = poster;
    }

    @Override
    public void init(MqttSubscriber subscriber) throws ThingException {
        subscriber.subscribe(
                format("/sys/%s/%s/thing/service/property/set", thing.getProductId(), thing.getThingId()),
                this
        );
    }

    @Override
    public void handle(String mqttTopic, MqttMessage mqttMessage) throws Exception {

        final JsonObject requestJsonObject = parser.parse(new String(mqttMessage.getPayload(), UTF_8)).getAsJsonObject();
        final String reqId = requestJsonObject.get("id").getAsString();
        final Set<String> successIds = batchPropertySet(reqId, requestJsonObject.getAsJsonObject("params"));

        // 回馈云平台属性设置结果
        poster.post(mqttTopic + "_reply", MQTT_QOS_AT_LEAST_ONCE,
                success(
                        reqId,
                        encode(new HashMap<String, String>() {{
                            put(FEATURE_KEY_PROPERTY_SET_REPLY_SUCCESS_IDS, String.join(",", successIds));
                        }})
                ));

        logger.info("{}/property/set success, req={};identities={};", thing, reqId, successIds);

    }


    // 批量设置属性
    private Set<String> batchPropertySet(String reqId, JsonObject paramsJsonObject) {
        final Set<String> successIds = new LinkedHashSet<>();
        if (null == paramsJsonObject) {
            return successIds;
        }

        // 批量设置属性
        paramsJsonObject.entrySet().forEach(entry -> {

            final String identity = entry.getKey();
            if (!Identifier.test(identity)) {
                logger.warn("{}/property/set ignored, illegal identity, req={};identity={};",
                        thing, reqId, identity);
                return;
            }

            final Identifier identifier = Identifier.parseIdentity(identity);

            // 过滤掉未提供的组件
            final ThComStub thComStub = thing.getThComStubMap().get(identifier.getComponentId());
            if (null == thComStub) {
                logger.warn("{}/property/set ignored, component: {} not provided, req={};identity={};",
                        thing, identifier.getComponentId(), reqId, identity);
                return;
            }

            // 过滤掉未提供的属性
            final ThPropertyMeta thPropertyMeta = thComStub.getThComMeta().getIdentityThPropertyMetaMap().get(identifier);
            if (null == thPropertyMeta) {
                logger.warn("{}/property/set ignored, property not provided, req={};identity={};",
                        thing, reqId, identity);
                return;
            }

            // 过滤掉只读属性
            if (thPropertyMeta.isReadonly()) {
                logger.warn("{}/property/set ignored, property is readonly, req={};identity={};",
                        thing, reqId, identity);
                return;
            }

            // 属性赋值
            try {
                thPropertyMeta.setPropertyValue(
                        thComStub.getThingCom(),
                        gson.fromJson(entry.getValue(), thPropertyMeta.getPropertyType())
                );
                successIds.add(identity);
                logger.debug("{}/property/set success, req={};identity={};", thing, reqId, identity);
            } catch (Throwable cause) {
                logger.warn("{}/property/set ignored, occur error, req={};identity={};",
                        thing, reqId, identity, cause);
            }

        });

        return successIds;
    }

}
