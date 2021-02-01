package com.github.ompc.athing.aliyun.thing;

import com.github.ompc.athing.aliyun.thing.kernel.ThingComStub;
import com.github.ompc.athing.aliyun.thing.kernel.ThingKernel;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.ThingOp;
import com.github.ompc.athing.standard.thing.config.ThingConfigListener;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 阿里云设备实现
 */
public class ThingImpl implements Thing {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ThingAccessKey thingAccessKey;

    private final ThingConfigListener thingConfigListener;
    private final ThingOpHook thingOpHook;
    private final ThingConnectOptions thingConnOpts;

    private ThingKernel kernel;
    private IMqttClient mqttClient;
    private ExecutorService executor;
    private ThingOp thingOp;

    /**
     * 设备实现
     *
     * @param thingAccessKey      连接密钥
     * @param thingConfigListener 设备配置监听器
     * @param thingOpHook         设备操作钩子
     * @param thingConnOpts       连接选项
     */
    ThingImpl(final ThingAccessKey thingAccessKey,
              final ThingConfigListener thingConfigListener,
              final ThingOpHook thingOpHook,
              final ThingConnectOptions thingConnOpts) {
        this.thingAccessKey = thingAccessKey;
        this.thingConfigListener = thingConfigListener;
        this.thingOpHook = thingOpHook;
        this.thingConnOpts = thingConnOpts;
    }

    /**
     * 初始化设备
     * <p>
     * 这里将初始化设备方法暴露出来主要是考虑到初始化时候的报错，
     * 当初始化失败时可直接销毁设备
     * </p>
     *
     * @param kernel   设备内核
     * @param client   Mqtt客户端
     * @param executor 工作线程池
     * @throws ThingException 初始化失败
     */
    protected void init(ThingKernel kernel,
                        IMqttClient client,
                        ExecutorService executor) throws ThingException {
        this.kernel = kernel;
        this.executor = executor;
        this.mqttClient = client;
        this.thingOp = new ThingOpImpl(this);
        kernel.initialized(this);
    }

    public ThingKernel getThingKernel() {
        return kernel;
    }

    public ThingConfigListener getThingConfigListener() {
        return thingConfigListener;
    }

    public ThingOpHook getThingOpHook() {
        return thingOpHook;
    }

    public ThingConnectOptions getThingConnOpts() {
        return thingConnOpts;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public IMqttClient getMqttClient() {
        return mqttClient;
    }

    @Override
    public String getProductId() {
        return thingAccessKey.getProductId();
    }

    @Override
    public String getThingId() {
        return thingAccessKey.getThingId();
    }

    @Override
    public Set<String> getThingComIds() {
        return kernel.getThingComStubMap().keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ThingCom> T getThingCom(String thingComId) {
        final ThingComStub thingComStub = kernel.getThingComStubMap().get(thingComId);
        return null != thingComStub
                ? (T) thingComStub.getThingCom()
                : null;
    }

    @Override
    public <T extends ThingCom> T requireThingCom(String thingComId) throws ThingException {
        final T thingCom = getThingCom(thingComId);
        if (null == thingCom) {
            throw new ThingException(this, String.format(
                    "component: id=%s is required, but not found!",
                    thingComId)
            );
        }
        return thingCom;
    }

    @Override
    public <T extends ThingCom> T getThingCom(Class<T> type) throws ThingException {
        final Set<T> thingComSet = getThingComSet(type);

        // 一个都没找到，返回null
        if (null == thingComSet || thingComSet.isEmpty()) {
            return null;
        }

        // 多于1个，报错
        if (thingComSet.size() > 1) {
            throw new ThingException(this, String.format(
                    "component: type=%s except: 1, but found: %d",
                    type.getName(),
                    thingComSet.size())
            );
        }

        // 正好1个，直接返回
        return thingComSet.iterator().next();
    }

    @Override
    public <T extends ThingCom> T requireThingCom(Class<T> type) throws ThingException {
        final T thingCom = getThingCom(type);
        if (null == thingCom) {
            throw new ThingException(this, String.format(
                    "component: type=%s is required, but not found!",
                    type.getName())
            );
        }
        return thingCom;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ThingCom> Set<T> getThingComSet(Class<T> type) {
        return kernel.getThingComSet().stream()
                .filter(type::isInstance)
                .map(thingCom -> (T) thingCom)
                .collect(Collectors.toSet());
    }


    @Override
    public ThingOp getThingOp() {
        return thingOp;
    }

    @Override
    public void destroy() {

        /*
         * 设备关闭的严格流程
         * 1. 断开设备与平台的连接
         * 2. 关闭工作线程池
         * 3. 销毁设备组件
         */

        // 断开连接
        if (null != mqttClient) {
            try {
                mqttClient.disconnect();
            } catch (MqttException cause) {
                logger.warn("{} mqtt-client disconnect failure!", this, cause);
            }
        }

        // 关闭线程池
        if (null != executor) {
            executor.shutdown();
        }

        // 销毁内核
        if (null != kernel) {
            kernel.destroy(this);
        }

        logger.info("{} destroy completed!", this);

    }

    @Override
    public String toString() {
        return String.format("thing:/%s/%s", getProductId(), getThingId());
    }

}
