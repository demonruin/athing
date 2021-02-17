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
import org.eclipse.paho.client.mqttv3.IMqttClient;

/**
 * 设备操作实现
 */
class ThingOpImpl implements ThingOp {

    private final ThingImpl thing;
    private final MqttExecutor[] mqttExecutors;
    private final ThingConfigPullMqttExecutor thingConfigPullMqttExecutor;
    private final ThingPostMqttExecutor thingPostMqttExecutor;

    ThingOpImpl(ThingImpl thing, IMqttClient client) {
        this.thing = thing;
        final MqttPoster poster = new MqttPoster(thing, client);
        final ThingOpPingPong pingPong = new ThingOpPingPong();
        mqttExecutors = new MqttExecutor[]{
                thingConfigPullMqttExecutor = new ThingConfigPullMqttExecutor(thing, poster, pingPong),
                thingPostMqttExecutor = new ThingPostMqttExecutor(thing, poster, pingPong),
                new ThingModularUpgradePushMqttExecutor(thing, poster),
                new ThingConfigPushMqttExecutor(thing, poster),
                new ThPropertySetMqttExecutor(thing, poster),
                new ThServiceInvokeMqttExecutor(thing, poster),
        };
    }

    public MqttExecutor[] getMqttExecutors() {
        return mqttExecutors;
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
