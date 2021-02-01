package com.github.ompc.athing.aliyun.thing.executor;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MQTT执行器
 */
public interface MqttExecutor {

    /**
     * 获订阅的MQTT主题表达式
     *
     * @return MQTT主题表达式
     */
    String[] getMqttTopicExpress();

    /**
     * 处理MQTT消息
     *
     * @param mqttTopic   MQTT主题
     * @param mqttMessage MQTT消息
     * @throws Exception 处理失败
     */
    void onMqttMessage(String mqttTopic, MqttMessage mqttMessage) throws Exception;

}
