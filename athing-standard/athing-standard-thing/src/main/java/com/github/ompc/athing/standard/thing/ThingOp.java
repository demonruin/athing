package com.github.ompc.athing.standard.thing;

import com.github.ompc.athing.standard.component.Identifier;
import com.github.ompc.athing.standard.component.ThingEvent;
import com.github.ompc.athing.standard.thing.boot.Modular;
import com.github.ompc.athing.standard.thing.config.ThingConfigApply;

/**
 * 设备操作
 */
public interface ThingOp {

    /**
     * 报告设备事件
     *
     * @param event     事件
     * @param thingOpCb 回调
     * @return 请求ID
     * @throws ThingException 操作失败
     */
    String postThingEvent(ThingEvent<?> event, ThingOpCb<Void> thingOpCb) throws ThingException;

    /**
     * 报告设备属性
     *
     * @param identifiers 设备属性标识
     * @param thingOpCb   回调
     * @return 请求ID
     * @throws ThingException 操作失败
     */
    String postThingProperties(Identifier[] identifiers, ThingOpCb<Void> thingOpCb) throws ThingException;

    /**
     * 报告模块信息
     *
     * @param module    模块
     * @param thingOpCb 回调
     * @return 请求ID
     * @throws ThingException 操作失败
     */
    String reportModule(Modular module, ThingOpCb<Void> thingOpCb) throws ThingException;

    /**
     * 更新设备配置
     *
     * @param thingOpCb 回调
     * @throws ThingException 操作失败
     */
    String updateThingConfig(ThingOpCb<ThingConfigApply> thingOpCb) throws ThingException;

    /**
     * 重启设备
     *
     * @throws ThingException 操作失败
     */
    void reboot() throws ThingException;

}
