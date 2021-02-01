package com.github.ompc.athing.standard.thing.config;

import com.github.ompc.athing.standard.thing.ThingException;

/**
 * 设备配置
 */
public interface ThingConfig {

    /**
     * 获取配置范围
     *
     * @return 配置范围
     */
    ConfigScope getScope();

    /**
     * 获取配置版本
     *
     * @return 配置版本
     */
    String getVersion();

    /**
     * 获取配置内容
     *
     * @return 配置内容
     * @throws ThingException 获取配置内容失败
     */
    String getConfig() throws ThingException;

    /**
     * 配置范围
     */
    enum ConfigScope {

        /**
         * 产品
         */
        PRODUCT

    }
}
