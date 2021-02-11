package com.github.ompc.athing.standard.thing;

/**
 * 设备
 */
public interface Thing extends ThingComContainer {

    /**
     * 获取设备产品ID
     *
     * @return 设备产品ID
     */
    String getProductId();

    /**
     * 获取设备ID
     *
     * @return 设备ID
     */
    String getThingId();

    /**
     * 获取设备操作
     *
     * @return 设备操作
     */
    ThingOp getThingOp();

    /**
     * 销毁设备
     *
     * @throws Exception 销毁失败
     */
    void destroy() throws Exception;

}
