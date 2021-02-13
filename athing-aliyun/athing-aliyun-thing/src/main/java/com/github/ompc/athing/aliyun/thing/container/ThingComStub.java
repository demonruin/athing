package com.github.ompc.athing.aliyun.thing.container;

import com.github.ompc.athing.aliyun.framework.component.meta.ThComMeta;
import com.github.ompc.athing.standard.component.ThingCom;

/**
 * 设备组件存根
 */
public class ThingComStub {

    private final ThComMeta thComMeta;
    private final ThingCom thingCom;

    public ThingComStub(ThComMeta thComMeta, ThingCom thingCom) {
        this.thComMeta = thComMeta;
        this.thingCom = thingCom;
    }

    /**
     * 获取组件ID
     *
     * @return 组件ID
     */
    public String getThingComId() {
        return thComMeta.getThingComId();
    }

    /**
     * 获取组件元数据
     *
     * @return 组件元数据
     */
    public ThComMeta getThComMeta() {
        return thComMeta;
    }

    /**
     * 获取组件实例
     *
     * @return 组件实例
     */
    public ThingCom getThingCom() {
        return thingCom;
    }
}
