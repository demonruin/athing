package com.github.ompc.athing.aliyun.thing.container.loader;

import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.boot.ThingComBoot;

/**
 * 设备组件引导加载器
 */
abstract public class ThingComBootLoader implements ThingComLoader {

    private final OnBoot onBoot;

    /**
     * 设备组件引导加载器
     *
     * @param onBoot 组件引导
     */
    public ThingComBootLoader(OnBoot onBoot) {
        this.onBoot = onBoot;
    }

    /**
     * 获取组件引导
     *
     * @return 组件引导
     */
    protected OnBoot getOnBoot() {
        return onBoot;
    }

    /**
     * 组件引导
     */
    public interface OnBoot {

        /**
         * 引导
         *
         * @param boot 设备组件引导
         * @return 设备组件
         * @throws Exception 引导失败
         */
        ThingCom onBoot(ThingComBoot boot) throws Exception;

    }

}
