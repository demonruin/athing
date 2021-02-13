package com.github.ompc.athing.aliyun.thing;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.UUID;

import static com.github.ompc.athing.aliyun.thing.util.StringUtils.bytesToHexString;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 阿里云MQTT客户端
 */
class MqttClientExt extends MqttClient {

    // 启动信息
    private final Boot boot;

    /**
     * 构造MQTT客户端
     *
     * @param thingServerUrl 服务地址
     * @param access         设备密钥
     * @throws MqttException 构建MQTT客户端失败
     */
    public MqttClientExt(final String thingServerUrl,
                         final ThingAccess access) throws MqttException {
        this(thingServerUrl, new Boot(access), new MemoryPersistence());
    }

    /**
     * 构造MQTT客户端
     *
     * @param serverUrl   服务地址
     * @param access      设备密钥
     * @param persistence 数据持久化方案
     * @throws MqttException 构建MQTT客户端失败
     */
    public MqttClientExt(final String serverUrl,
                         final ThingAccess access,
                         final MqttClientPersistence persistence) throws MqttException {
        this(serverUrl, new Boot(access), persistence);
    }

    private MqttClientExt(final String serverUrl,
                          final Boot boot,
                          final MqttClientPersistence persistence) throws MqttException {
        super(serverUrl, boot.getClientId(), persistence);
        this.boot = boot;
    }

    /**
     * 注入连接参数
     *
     * @param options 连接参数
     * @return options
     */
    private MqttConnectOptions injectMqttConnectOptions(MqttConnectOptions options) {
        options.setUserName(boot.getUsername());
        options.setPassword(boot.getPassword());
        return options;
    }

    @Override
    public void connect() throws MqttException {
        connect(injectMqttConnectOptions(new MqttConnectOptions()));
    }

    @Override
    public void connect(MqttConnectOptions options) throws MqttException {
        super.connect(injectMqttConnectOptions(options));
    }

    @Override
    public IMqttToken connectWithResult(MqttConnectOptions options) throws MqttException {
        return super.connectWithResult(injectMqttConnectOptions(options));
    }

    /**
     * 启动信息
     */
    private static class Boot {

        final String uniqueId = UUID.randomUUID().toString();
        final long timestamp = System.currentTimeMillis();
        final ThingAccess access;

        /**
         * 构建启动信息
         *
         * @param access 设备密钥
         */
        Boot(ThingAccess access) {
            this.access = access;
        }

        /**
         * 获取MQTT帐号
         *
         * @return MQTT帐号
         */
        String getUsername() {
            return String.format("%s&%s", access.getThingId(), access.getProductId());
        }

        /**
         * 获取MQTT密码
         *
         * @return MQTT密码
         */
        char[] getPassword() {
            final String content = String.format("clientId%sdeviceName%sproductKey%stimestamp%s",
                    uniqueId,
                    access.getThingId(),
                    access.getProductId(),
                    timestamp
            );
            try {
                final Mac mac = Mac.getInstance("HMACSHA1");
                mac.init(new SecretKeySpec(access.getSecret().getBytes(UTF_8), mac.getAlgorithm()));
                return bytesToHexString(mac.doFinal(content.getBytes(UTF_8))).toCharArray();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 获取MQTT客户端ID
         *
         * @return 客户端ID
         */
        String getClientId() {
            return String.format("%s|securemode=3,signmethod=hmacsha1,timestamp=%s,ext=1|", uniqueId, timestamp);
        }

    }


}