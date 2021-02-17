package com.github.ompc.athing.aliyun.thing.executor;

import com.github.ompc.athing.aliyun.framework.util.GsonFactory;
import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.ThingException;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Mqtt消息投递器
 */
public class MqttPoster {

    public static final int MQTT_QOS_AT_MOST_ONCE = 0;
    public static final int MQTT_QOS_AT_LEAST_ONCE = 1;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Gson gson = GsonFactory.getGson();
    private final Thing thing;
    private final IMqttClient client;

    public MqttPoster(Thing thing, IMqttClient client) {
        this.thing = thing;
        this.client = client;
    }

    /**
     * 投递消息
     *
     * @param topic   消息主题
     * @param message 消息
     * @throws ThingException 投递失败
     */
    public void post(String topic, Object message) throws ThingException {
        post(topic, MQTT_QOS_AT_LEAST_ONCE, message);
    }

    /**
     * 投递消息
     *
     * @param topic   消息主题
     * @param qos     消息QOS
     * @param message 消息
     * @throws ThingException 投递失败
     */
    public void post(String topic, int qos, Object message) throws ThingException {
        try {
            final String payload = gson.toJson(message);
            final MqttMessage mqttMessage = new MqttMessage(payload.getBytes(UTF_8));
            mqttMessage.setRetained(false);
            mqttMessage.setQos(qos);
            client.publish(topic, mqttMessage);
            logger.debug("{}/mqtt post topic={};qos={}; message -> {}", thing, topic, qos, payload);
        } catch (Throwable cause) {
            throw new ThingException(
                    thing,
                    format("post message: [qos=%s;topic=%s;] error!", qos, topic),
                    cause
            );
        }
    }


}
