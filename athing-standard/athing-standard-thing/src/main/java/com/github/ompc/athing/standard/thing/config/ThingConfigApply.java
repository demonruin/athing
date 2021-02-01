package com.github.ompc.athing.standard.thing.config;

import com.github.ompc.athing.standard.thing.ThingException;

/**
 * 设备配置应用
 */
public interface ThingConfigApply {

    /**
     * 获取最新的配置
     *
     * @return 设备配置
     */
    ThingConfig getThingConfig();

    /**
     * 应用设备更新
     *
     * @throws ThingException 应用失败
     */
    void apply() throws ThingException;

}
