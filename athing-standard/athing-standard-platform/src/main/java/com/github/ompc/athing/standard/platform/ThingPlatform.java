package com.github.ompc.athing.standard.platform;

/**
 * 设备平台
 */
public interface ThingPlatform {

    /**
     * 获取设备模版
     *
     * @param productId 产品ID
     * @param thingId   设备ID
     * @return 设备模版
     */
    ThingTemplate getThingTemplate(String productId, String thingId);

    /**
     * 销毁设备平台
     *
     * @throws Exception 销毁失败
     */
    void destroy() throws Exception;

}
