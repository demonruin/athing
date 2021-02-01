package com.github.ompc.athing.standard.thing.boot;

import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.Thing;

import java.util.Properties;

/**
 * 设备组件引导程序
 */
public interface ThingComBoot {

    /**
     * 获取组件规格信息
     *
     * @return 组件规格信息
     */
    default Specifications getSpecifications() {
        return Properties::new;
    }

    /**
     * 启动设备组件
     *
     * @param thing     启动设备
     * @param arguments 启动参数
     * @return 设备组件
     * @throws Exception 启动失败
     */
    ThingCom bootUp(Thing thing, String arguments) throws Exception;

    /**
     * 设备组件厂商信息
     */
    interface Specifications {

        /**
         * 获取设备组件规格信息
         *
         * @return 设备组件规格信息
         */
        Properties getProperties();

    }

}
