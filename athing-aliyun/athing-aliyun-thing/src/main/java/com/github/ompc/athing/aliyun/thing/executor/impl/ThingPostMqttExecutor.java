package com.github.ompc.athing.aliyun.thing.executor.impl;

import com.github.ompc.athing.aliyun.framework.component.meta.ThPropertyMeta;
import com.github.ompc.athing.aliyun.framework.util.GsonFactory;
import com.github.ompc.athing.aliyun.framework.util.MapObject;
import com.github.ompc.athing.aliyun.thing.ThingImpl;
import com.github.ompc.athing.aliyun.thing.executor.MqttExecutor;
import com.github.ompc.athing.aliyun.thing.executor.MqttPoster;
import com.github.ompc.athing.aliyun.thing.executor.ThingOpPingPong;
import com.github.ompc.athing.aliyun.thing.kernel.ThingComStub;
import com.github.ompc.athing.aliyun.thing.kernel.ThingKernel;
import com.github.ompc.athing.standard.component.Identifier;
import com.github.ompc.athing.standard.component.ThingEvent;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.ThingOpCb;
import com.github.ompc.athing.standard.thing.boot.Modular;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Map;

import static com.github.ompc.athing.aliyun.thing.executor.MqttPoster.MQTT_QOS_AT_LEAST_ONCE;
import static com.github.ompc.athing.aliyun.thing.util.StringUtils.generateSequenceId;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 设备上报平台执行器
 */
public class ThingPostMqttExecutor implements MqttExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ThingImpl thing;
    private final MqttPoster poster;
    private final ThingOpPingPong pingPong;
    private final Gson gson = GsonFactory.getGson();
    private final Type tokenType = new TypeToken<AlinkReplyImpl<Map<String, String>>>() {
    }.getType();

    public ThingPostMqttExecutor(ThingImpl thing, MqttPoster poster, ThingOpPingPong pingPong) {
        this.thing = thing;
        this.poster = poster;
        this.pingPong = pingPong;
    }

    @Override
    public String[] getMqttTopicExpress() {
        return new String[]{
                format("/sys/%s/%s/thing/event/+/post_reply", thing.getProductId(), thing.getThingId())
        };
    }

    @Override
    public void onMqttMessage(String mqttTopic, MqttMessage mqttMessage) {
        // 解析alink应答数据
        final AlinkReplyImpl<Map<String, String>> reply = gson.fromJson(
                new String(mqttMessage.getPayload(), UTF_8),
                tokenType
        );

        // 请求ID
        final String reqId = reply.getReqId();
        logger.debug("{}/post reply received, req={};topic={};", thing, reqId, mqttTopic);

        // 拿到执行回调
        final ThingOpCb<?> thingOpCb = pingPong.pong(reqId);
        if (null == thingOpCb) {
            logger.warn("{}/post reply received, but callback is not found, req={};topic={};",
                    thing,
                    reqId,
                    mqttTopic
            );
            return;
        }

        // 属性上报的应答需要做特殊日志处理
        if (mqttTopic.endsWith("/thing/event/property/post_reply")
                && null != reply.getData()
                && !reply.getData().isEmpty()) {
            logger.warn("{}/property/post reply, but some properties failure, req={};properties={};",
                    thing,
                    reqId,
                    reply.getData()
            );
        }

        // 应答
        thingOpCb.callback(reqId, ThingOpReplyImpl.empty(reply));

    }

    /**
     * 报告设备事件
     *
     * @param thingOpCb 回调
     * @return 请求ID
     */
    public String postThingEvent(ThingEvent<?> event, ThingOpCb<Void> thingOpCb) throws ThingException {

        final String identity = event.getIdentifier().getIdentity();
        final String reqId = generateSequenceId();
        final String topic = format("/sys/%s/%s/thing/event/%s/post",
                thing.getProductId(),
                thing.getThingId(),
                identity
        );

        try {
            pingPong.pingInBlock(reqId, thingOpCb, () ->
                    poster.post(topic,
                            new MapObject()
                                    .putProperty("id", reqId)
                                    .putProperty("version", "1.0")
                                    .putProperty("method", format("thing.event.%s.post", identity))
                                    .enterProperty("params")
                                    /**/.putProperty("time", new Date(event.getOccurTimestampMs()))
                                    /**/.putProperty("value", event.getData())
                                    .exitProperty()));
            logger.info("{}/event posting, req={};identity={};", thing, reqId, event.getIdentifier());
        } catch (Exception cause) {
            throw new ThingException(
                    thing,
                    format("post event error, identity=%s;", identity),
                    cause
            );
        }

        return reqId;
    }

    // 构造报告数据：属性
    private MapObject buildingPostDataForThingComProperties(Identifier[] identifiers) throws ThingException {
        final ThingKernel kernel = thing.getThingKernel();
        final MapObject parameterMap = new MapObject();
        for (final Identifier identifier : identifiers) {
            // 模块不存在
            final ThingComStub thingComStub = kernel.getThingComStubMap().get(identifier.getComponentId());
            if (null == thingComStub) {
                throw new ThingException(thing, String.format("component: %s not existed, identity=%s;",
                        identifier.getComponentId(),
                        identifier
                ));
            }

            // 属性元数据不存在
            final ThPropertyMeta thPropertyMeta = thingComStub
                    .getThComMeta()
                    .getIdentityThPropertyMetaMap()
                    .get(identifier);
            if (null == thPropertyMeta) {
                throw new ThingException(thing, String.format("property: %s not existed!",
                        identifier
                ));
            }

            // 获取属性值
            try {
                final Object propertyValue = thPropertyMeta.getPropertyValue(thingComStub.getThingCom());
                parameterMap.enterProperty(identifier.getIdentity())
                        .putProperty("value", propertyValue)
                        .putProperty("time", new Date());
            }

            // 获取设备属性失败
            catch (Throwable cause) {
                throw new ThingException(
                        thing,
                        String.format("property: %s get value error!", identifier),
                        cause
                );
            }

        }

        return parameterMap;
    }

    /**
     * 投递属性
     *
     * @param identifiers 属性标识集合
     * @param thingOpCb   投递回调
     * @return 请求ID
     * @throws ThingException 投递属性失败
     */
    public String postThingProperties(Identifier[] identifiers, ThingOpCb<Void> thingOpCb) throws ThingException {

        final String reqId = generateSequenceId();
        final String topic = format("/sys/%s/%s/thing/event/property/post",
                thing.getProductId(),
                thing.getThingId()
        );

        try {
            pingPong.pingInBlock(reqId, thingOpCb, () ->
                    poster.post(topic, MQTT_QOS_AT_LEAST_ONCE,
                            // 组装事件集合
                            new MapObject()
                                    .putProperty("id", reqId)
                                    .putProperty("version", "1.0")
                                    .putProperty("method", "thing.event.property.post")
                                    .putProperty("params", buildingPostDataForThingComProperties(identifiers))
                    ));
            logger.info("{}/property posting, req={};identities={};", thing, reqId, identifiers);
        } catch (Exception cause) {
            throw new ThingException(thing, "post properties failure", cause);
        }

        return reqId;

    }

    /**
     * 报告设备模块信息
     * ThingComModular
     *
     * @param module    模块
     * @param thingOpCb 回调
     * @return 请求ID
     */
    public String reportModule(Modular module, ThingOpCb<Void> thingOpCb) throws ThingException {

        final String reqId = generateSequenceId();
        final String topic = format("/ota/device/inform/%s/%s", thing.getProductId(), thing.getThingId());

        try {
            poster.post(topic,
                    new MapObject()
                            .putProperty("id", reqId)
                            .enterProperty("params")
                            /**/.putProperty("module", module.getModuleId())
                            /**/.putProperty("version", module.getModuleVersion())
                            .exitProperty());
            logger.info("{}/module report version, req={};module={};version={};",
                    thing,
                    reqId,
                    module.getModuleId(),
                    module.getModuleVersion()
            );
        } catch (Throwable cause) {
            throw new ThingException(thing, String.format("module: %s post version failure", module.getModuleId()));
        }

        // 因为阿里云的实现中，上报版本平台不会给回馈
        // 所以这里只能自己构造一个阿里云的成功回馈回来
        thingOpCb.callback(reqId,
                ThingOpReplyImpl.empty(AlinkReplyImpl.success(reqId, "success")));

        return reqId;
    }


}
