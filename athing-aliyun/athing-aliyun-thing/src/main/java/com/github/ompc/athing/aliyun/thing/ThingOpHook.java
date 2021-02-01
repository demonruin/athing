package com.github.ompc.athing.aliyun.thing;

import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.ThingException;

/**
 * 设备操作钩子
 * <p>
 * 不同的设备操作需要根据具体的设备组织结构而来，所以这里需要钩子反调具体实现
 * </p>
 */
public interface ThingOpHook {

    /**
     * 设备重启
     *
     * @param thing 设备
     * @throws ThingException 设备重启失败
     */
    void reboot(Thing thing) throws ThingException;

}
