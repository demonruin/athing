package com.github.ompc.athing.aliyun.thing;

import com.github.ompc.athing.aliyun.framework.util.IOUtils;
import com.github.ompc.athing.aliyun.thing.kernel.ThingBoot;
import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.config.ThingConfigListener;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 设备连接器
 * <p>
 * 负责将设备启动并连接到设备平台
 * </p>
 */
public class ThingConnector {

    private static final Logger logger = LoggerFactory.getLogger(ThingConnector.class);

    static {
        logger.info(IOUtils.getLogo("athing-logo.txt"));
    }

    /**
     * 设备连接平台
     *
     * @param thingServerUrl 设备服务地址
     * @param thingAccessKey 设备连接密钥
     * @return Connecting
     */
    public Connecting connecting(String thingServerUrl, ThingAccessKey thingAccessKey) {
        return new Connecting() {

            private ThingBoot thingBoot;
            private ThingConfigListener thingConfigListener;
            private ThingOpHook thingOpHook;
            private ExecutorService executor;

            @Override
            public Connecting setThingBoot(ThingBoot thingBoot) {
                this.thingBoot = thingBoot;
                return this;
            }

            @Override
            public Connecting setThingConfigListener(ThingConfigListener thingConfigListener) {
                this.thingConfigListener = thingConfigListener;
                return this;
            }

            @Override
            public Connecting setThingOpHook(ThingOpHook thingOpHook) {
                this.thingOpHook = thingOpHook;
                return this;
            }

            @Override
            public Connecting setExecutorService(ExecutorService executor) {
                this.executor = executor;
                return this;
            }

            @Override
            public Thing connect(ThingConnectOptions thingConnOpts) throws ThingException {
                return connect(null, thingConnOpts);
            }

            @Override
            public Thing connect(IMqttClient client, ThingConnectOptions thingConnOpts) throws ThingException {

                // 构建设备
                final ThingImpl thing = new ThingImpl(thingAccessKey, initThingConfigListener(), initThingOpHook(), thingConnOpts) {{

                    try {

                        /*
                         * 设备启动需要严格遵守的顺序
                         * 1. 设备组件引导
                         * 2. 设备连接平台
                         * 3. 启动工作线程
                         * 4. 物模型主题订阅
                         * 5. 通知组件启动成功
                         */
                        init(
                                initThingBoot().bootUp(this),
                                initMqttClient(client, thingConnOpts),
                                initExecutor(thingConnOpts)
                        );

                    } catch (Exception cause) {

                        // 任何一步的构建失败都将会主动销毁设备，释放已申请的资源
                        destroy();

                        throw new ThingException(this, "connect error!", cause);
                    }

                }};

                logger.info("{} connect completed, server={};thread={};components={};",
                        thing,
                        thingServerUrl,
                        thingConnOpts.getThreads(),
                        thing.getThingKernel().getThingComStubMap().keySet()
                );
                return thing;
            }

            private ThingBoot initThingBoot() {
                if (null != thingBoot) {
                    return thingBoot;
                }
                return new ThingBoot();
            }

            private ThingConfigListener initThingConfigListener() {
                if (null != thingConfigListener) {
                    return thingConfigListener;
                }
                return (thing, config) -> {
                    throw new UnsupportedOperationException();
                };
            }

            private IMqttClient initMqttClient(IMqttClient _client, ThingConnectOptions thingConnOpts) throws ThingException {
                try {

                    final IMqttClient client = null == _client
                            ? new MqttClientExt(thingServerUrl, thingAccessKey)
                            : _client;

                    client.connect(new MqttConnectOptions() {{
                        setCleanSession(true);
                        setConnectionTimeout((int) (thingConnOpts.getConnectTimeoutMs() / 1000));
                        setKeepAliveInterval((int) (thingConnOpts.getKeepAliveIntervalMs() / 1000));
                        setAutomaticReconnect(true);
                    }});

                    return client;

                } catch (MqttException cause) {
                    throw new ThingException(
                            thingAccessKey.getProductId(),
                            thingAccessKey.getThingId(),
                            "mqtt connect error",
                            cause
                    );
                }
            }

            private ExecutorService initExecutor(ThingConnectOptions thingConnOpts) {
                if (null != executor) {
                    return executor;
                }
                return executor = Executors.newFixedThreadPool(thingConnOpts.getThreads(), r -> {
                    final Thread thread = new Thread(
                            r,
                            String.format("thing:/%s/%s/worker-daemon",
                                    thingAccessKey.getProductId(),
                                    thingAccessKey.getThingId()
                            )
                    );
                    thread.setDaemon(true);
                    return thread;
                });
            }

            private ThingOpHook initThingOpHook() {
                if (null != thingOpHook) {
                    return thingOpHook;
                }
                return thing -> {
                    throw new UnsupportedOperationException();
                };
            }

        };
    }

    public interface Connecting {

        /**
         * 设置设备启动器
         *
         * @param thingBoot 设备启动器
         * @return this
         */
        Connecting setThingBoot(ThingBoot thingBoot);

        /**
         * 设置设备配置监听器
         *
         * @param thingConfigListener 设备配置监听器
         * @return this
         */
        Connecting setThingConfigListener(ThingConfigListener thingConfigListener);

        /**
         * 设置设备操作钩子
         *
         * @param thingOpHook 操作钩子
         * @return this
         */
        Connecting setThingOpHook(ThingOpHook thingOpHook);

        /**
         * 设置设备工作线程池
         *
         * @param executor 设备工作线程池
         * @return this
         */
        Connecting setExecutorService(ExecutorService executor);

        /**
         * 设备连接
         *
         * @param thingConnOpts 设备连接选项
         * @return 设备
         * @throws ThingException 连接失败
         */
        Thing connect(ThingConnectOptions thingConnOpts) throws ThingException;

        /**
         * 设备连接
         *
         * @param client        Mqtt客户端
         * @param thingConnOpts 设备连接选项
         * @return 设备
         * @throws ThingException 连接失败
         */
        Thing connect(IMqttClient client, ThingConnectOptions thingConnOpts) throws ThingException;

    }

}
