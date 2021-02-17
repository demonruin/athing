package com.github.ompc.athing.aliyun.thing.executor.impl.modular;

import com.github.ompc.athing.aliyun.framework.util.MapObject;
import com.github.ompc.athing.aliyun.thing.ThingImpl;
import com.github.ompc.athing.aliyun.thing.executor.MqttExecutor;
import com.github.ompc.athing.aliyun.thing.executor.MqttPoster;
import com.github.ompc.athing.aliyun.thing.executor.impl.AlinkReplyImpl;
import com.github.ompc.athing.aliyun.thing.executor.impl.ThingOpReplyImpl;
import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.ThingOpCb;
import com.github.ompc.athing.standard.thing.boot.Modular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.ompc.athing.aliyun.thing.util.StringUtils.generateSequenceId;
import static java.lang.String.format;

/**
 * 设备上报模块信息MQTT执行器
 */
public class ThingModularReportPostMqttExecutor implements MqttExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Thing thing;
    private final MqttPoster poster;

    public ThingModularReportPostMqttExecutor(ThingImpl thing, MqttPoster poster) {
        this.thing = thing;
        this.poster = poster;
    }

    @Override
    public void init(MqttSubscriber subscriber) throws ThingException {

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
