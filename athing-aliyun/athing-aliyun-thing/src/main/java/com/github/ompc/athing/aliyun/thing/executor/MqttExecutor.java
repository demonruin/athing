package com.github.ompc.athing.aliyun.thing.executor;

import com.github.ompc.athing.standard.thing.ThingException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * MQTT执行器
 */
public interface MqttExecutor {

    /**
     * 初始化
     *
     * @param subscriber 订阅器
     * @throws ThingException 订阅失败
     */
    void init(MqttSubscriber subscriber) throws ThingException;

    /**
     * 订阅器
     */
    interface MqttSubscriber {

        /**
         * 订阅MQTT主题
         *
         * @param mqttTopicExpress   主题表达式
         * @param mqttMessageHandler 消息处理器
         * @throws ThingException 处理失败
         */
        void subscribe(String mqttTopicExpress, MqttMessageHandler mqttMessageHandler) throws ThingException;

    }

    /**
     * MQTT消息处理器
     */
    interface MqttMessageHandler {

        /**
         * 处理MQTT消息
         *
         * @param mqttTopic   消息主题
         * @param mqttMessage 消息
         * @throws Exception 处理失败
         */
        void handle(String mqttTopic, MqttMessage mqttMessage) throws Exception;

    }

}
