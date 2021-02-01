package com.github.ompc.athing.aliyun.platform.message;

import com.github.ompc.athing.aliyun.platform.message.decoder.*;
import com.github.ompc.athing.aliyun.platform.product.ThProductMeta;
import com.github.ompc.athing.standard.platform.message.ThingMessage;
import com.github.ompc.athing.standard.platform.message.ThingMessageListener;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 设备JMS消息监听器实现
 */
public class ThingJmsMessageListenerImpl implements MessageListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ThingMessageListener listener;
    private final JsonParser parser = new JsonParser();
    private final MessageDecoder<Message, ?> decoder;
    private final String toString = String.format("thing-message:/listener/%s", hashCode());


    /**
     * 设备JMS消息监听器实现
     *
     * @param productMetaMap 产品元数据集合
     * @param listener       设备消息监听器
     */
    public ThingJmsMessageListenerImpl(Map<String, ThProductMeta> productMetaMap, ThingMessageListener listener) {
        this.decoder = new JmsMessageDecoder()
                .next(new ThingStateChangedMessageDecoder(productMetaMap))
                .next(new ThingModularUpgradeRetMessageDecoder(productMetaMap))
                .next(new ThingMessageDecoder(productMetaMap)
                        .next(new ThingReplyMessageDecoder()
                                .next(new ThingReplyPropertySetMessageDecoder())
                                .next(new ThingReplyConfigPushMessageDecoder())
                                .next(new ThingReplyServiceReturnMessageDecoder())
                        )
                        .next(new ThingPostEventMessageDecoder())
                        .next(new ThingPostPropertyMessageDecoder())
                );
        this.listener = listener;
    }

    @Override
    public String toString() {
        return toString;
    }

    /**
     * 解码 (JMS-MESSAGE > THING-MESSAGE)
     *
     * @param jmsMessage JMS消息
     * @return 设备消息
     * @throws DecodeException 解码失败
     */
    private ThingMessage decode(Message jmsMessage) throws DecodeException {

        final String jmsMessageID;
        final String jmsMessagePayloadJson;
        try {
            jmsMessageID = jmsMessage.getJMSMessageID();
            jmsMessagePayloadJson = new String(jmsMessage.getBody(byte[].class), UTF_8);
            logger.debug("{} received jms-message-id={} -> {};", this, jmsMessageID, jmsMessagePayloadJson);
        } catch (Exception cause) {
            throw new DecodeException("decode jms-message failure!", cause);
        }

        final ThingMessage thingMessage = decoder.decodeThingMessage(jmsMessage, parser.parse(jmsMessagePayloadJson));
        if (null == thingMessage) {
            throw new DecodeException(String.format("none thing-message decoded! jms-message=%s", jmsMessageID));
        }

        logger.info("{} received thing-message, type={};product={};thing={};",
                this,
                thingMessage.getType(),
                thingMessage.getProductId(),
                thingMessage.getThingId()
        );
        return thingMessage;

    }

    @Override
    public void onMessage(Message jmsMessage) {

        try {

            // 处理设备消息
            listener.onMessage(decode(jmsMessage));

            // 一切阿弥陀佛，提交消费成功
            jmsMessage.acknowledge();

        }

        // 处理消息失败，回滚本次消息
        catch (Exception cause) {
            logger.warn("{} handle jms-message occur error!", this, cause);
            throw new RuntimeException(cause);
        }

    }

}
