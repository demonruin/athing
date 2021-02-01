package com.github.ompc.athing.aliyun.thing;

import com.github.ompc.athing.aliyun.thing.executor.MqttExecutor;
import com.github.ompc.athing.aliyun.thing.executor.MqttPoster;
import com.github.ompc.athing.aliyun.thing.executor.ThingOpPingPong;
import com.github.ompc.athing.aliyun.thing.executor.impl.*;
import com.github.ompc.athing.standard.component.Identifier;
import com.github.ompc.athing.standard.component.ThingEvent;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.ThingOp;
import com.github.ompc.athing.standard.thing.ThingOpCb;
import com.github.ompc.athing.standard.thing.boot.Modular;
import com.github.ompc.athing.standard.thing.config.ThingConfigApply;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 设备操作实现
 */
class ThingOpImpl implements ThingOp {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ThingImpl thing;
    private final ThingConfigPullMqttExecutor thingConfigPullMqttExecutor;
    private final ThingPostMqttExecutor thingPostMqttExecutor;

    ThingOpImpl(ThingImpl thing) throws ThingException {
        this.thing = thing;
        final MqttPoster poster = new MqttPoster(thing, thing.getMqttClient());
        final ThingOpPingPong pingPong = new ThingOpPingPong();
        initMqttExecutors(
                thingConfigPullMqttExecutor = new ThingConfigPullMqttExecutor(thing, poster, pingPong),
                thingPostMqttExecutor = new ThingPostMqttExecutor(thing, poster, pingPong),
                new ThingModularUpgradePushMqttExecutor(thing, poster),
                new ThingConfigPushMqttExecutor(thing, poster),
                new ThPropertySetMqttExecutor(thing, poster),
                new ThServiceInvokeMqttExecutor(thing, poster)
        );
    }

    /**
     * 初始化Mqtt执行器
     *
     * @param mqttExecutors Mqtt执行器集合
     * @throws ThingException 初始化失败
     */
    private void initMqttExecutors(MqttExecutor... mqttExecutors) throws ThingException {
        for (final MqttExecutor mqttExecutor : mqttExecutors) {
            for (final String topicExpress : mqttExecutor.getMqttTopicExpress()) {
                try {
                    thing.getMqttClient().subscribe(topicExpress, (topic, message) -> thing.getExecutor().submit(() -> {
                        try {
                            logger.debug("{}/mqtt received mqtt-message: {} -> {}", thing, topic, message);
                            mqttExecutor.onMqttMessage(topic, message);
                        } catch (Throwable cause) {
                            logger.warn("{}/mqtt consume message failure, topic={};message={};", thing, topic, message, cause);
                        }
                    }));
                    logger.debug("{}/mqtt subscribe: {};", thing, topicExpress);
                } catch (MqttException cause) {
                    throw new ThingException(
                            thing,
                            String.format("subscribe mqtt-topic: %s error", topicExpress),
                            cause
                    );
                }
            }
        }
    }

    @Override
    public String postThingEvent(ThingEvent<?> event, ThingOpCb<Void> thingOpCb) throws ThingException {
        return thingPostMqttExecutor.postThingEvent(event, thingOpCb);
    }

    @Override
    public String postThingProperties(Identifier[] identifiers, ThingOpCb<Void> thingOpCb) throws ThingException {
        return thingPostMqttExecutor.postThingProperties(identifiers, thingOpCb);
    }

    @Override
    public String reportModule(Modular module, ThingOpCb<Void> thingOpCb) throws ThingException {
        return thingPostMqttExecutor.reportModule(module, thingOpCb);
    }

    @Override
    public String updateThingConfig(ThingOpCb<ThingConfigApply> thingOpCb) throws ThingException {
        return thingConfigPullMqttExecutor.updateThingConfig(thingOpCb);
    }

    @Override
    public void reboot() throws ThingException {
        thing.getThingOpHook().reboot(thing);
    }

}