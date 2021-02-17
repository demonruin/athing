package com.github.ompc.athing.aliyun.thing.executor.impl;

import com.github.ompc.athing.aliyun.framework.util.GsonFactory;
import com.github.ompc.athing.aliyun.framework.util.MapObject;
import com.github.ompc.athing.aliyun.thing.ThingImpl;
import com.github.ompc.athing.aliyun.thing.executor.MqttExecutor;
import com.github.ompc.athing.aliyun.thing.executor.MqttPoster;
import com.github.ompc.athing.aliyun.thing.executor.ThingOpPingPong;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.ThingOpCb;
import com.github.ompc.athing.standard.thing.config.ThingConfig;
import com.github.ompc.athing.standard.thing.config.ThingConfigApply;
import com.github.ompc.athing.standard.thing.config.ThingConfigListener;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.ompc.athing.aliyun.thing.executor.MqttPoster.MQTT_QOS_AT_LEAST_ONCE;
import static com.github.ompc.athing.aliyun.thing.util.StringUtils.generateSequenceId;
import static com.github.ompc.athing.standard.thing.config.ThingConfig.ConfigScope.PRODUCT;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 设备配置主动请求执行器
 */
public class ThingConfigPullMqttExecutor implements MqttExecutor, MqttExecutor.MqttMessageHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ThingImpl thing;
    private final MqttPoster poster;
    private final ThingOpPingPong pingPong;

    private final Gson gson = GsonFactory.getGson();

    public ThingConfigPullMqttExecutor(ThingImpl thing, MqttPoster poster, ThingOpPingPong pingPong) {
        this.thing = thing;
        this.poster = poster;
        this.pingPong = pingPong;
    }

    @Override
    public void init(MqttSubscriber subscriber) throws ThingException {
        subscriber.subscribe(
                format("/sys/%s/%s/thing/config/get_reply", thing.getProductId(), thing.getThingId()),
                this
        );
    }

    @Override
    public void handle(String mqttTopic, MqttMessage mqttMessage) {

        final AlinkReplyImpl<ThingConfigPullData> reply = gson.fromJson(
                new String(mqttMessage.getPayload(), UTF_8),
                new TypeToken<AlinkReplyImpl<ThingConfigPullData>>() {
                }.getType()
        );
        final String reqId = reply.getReqId();
        final ThingOpCb<ThingConfig> thingOpCb = pingPong.pong(reqId);

        logger.debug("{}/config/pull receive reply, req={};code={};message={};",
                thing, reqId, reply.getCode(), reply.getMessage());

        // 回调不存在，说明已经提前被移除
        if (null == thingOpCb) {
            logger.warn("{}/config/pull receive reply, but callback is not found, req={}", thing, reqId);
            return;
        }

        // 通知失败
        if (!reply.isOk()) {
            thingOpCb.callback(reqId, ThingOpReplyImpl.failure(reply));
            return;
        }

        // 通知成功
        thingOpCb.callback(reqId, ThingOpReplyImpl.success(
                reply,
                new ThingConfigImpl(PRODUCT, thing, thing.getThingConnOpt(),
                        reply.getData().configId,
                        reply.getData().url,
                        reply.getData().sign
                )
        ));
    }

    private String pullThingConfig(ThingOpCb<ThingConfig> thingOpCb) throws ThingException {
        final String reqId = generateSequenceId();
        final String topic = format("/sys/%s/%s/thing/config/get", thing.getProductId(), thing.getThingId());

        try {
            pingPong.pingInBlock(reqId, thingOpCb, () ->
                    poster.post(topic, MQTT_QOS_AT_LEAST_ONCE,
                            new MapObject()
                                    .putProperty("id", reqId)
                                    .putProperty("version", "1.0")
                                    .putProperty("method", "thing.config.get")
                                    .enterProperty("params")
                                    /**/.putProperty("configScope", PRODUCT)
                                    /**/.putProperty("getType", "file")
                                    .exitProperty()));
            logger.info("{}/config/pull posted, req={};", thing, reqId);
        } catch (Exception cause) {
            throw new ThingException(thing, "pull config error!", cause);
        }
        return reqId;
    }

    /**
     * 更新设备配置
     *
     * @param thingOpCb 回调
     * @throws ThingException 操作失败
     */
    public String updateThingConfig(ThingOpCb<ThingConfigApply> thingOpCb) throws ThingException {

        final ThingConfigListener listener = thing.getThingConfigListener();

        // 如果没有设置配置监听器，则不需要更新
        if (null == listener) {
            throw new ThingException(thing, "thing is not configurable!");
        }

        return pullThingConfig((id, reply) ->
                thingOpCb.callback(id, new ThingOpReplyImpl<>(reply.isSuccess(), reply.getCode(), reply.getMessage(),
                        !reply.isSuccess() ? null : new ThingConfigApply() {
                            @Override
                            public ThingConfig getThingConfig() {
                                return reply.getData();
                            }

                            @Override
                            public void apply() throws ThingException {
                                try {
                                    listener.configThing(thing, getThingConfig());
                                } catch (Exception cause) {
                                    throw new ThingException(thing, "apply config failure!", cause);
                                }
                            }
                        }
                )));

    }


    /**
     * 从平台拉取配置数据
     */
    static private class ThingConfigPullData {

        @SerializedName("configId")
        String configId;

        @SerializedName("sign")
        String sign;

        @SerializedName("url")
        String url;

    }

}
