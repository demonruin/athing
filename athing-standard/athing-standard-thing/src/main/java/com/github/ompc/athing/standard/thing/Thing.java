package com.github.ompc.athing.standard.thing;

import com.github.ompc.athing.standard.component.ThingCom;

import java.util.Set;

/**
 * 设备
 */
public interface Thing {

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
     * 获取设备组件ID集合
     *
     * @return 设备组件ID集合
     */
    Set<String> getThingComIds();

    /**
     * 获取设备组件
     *
     * @param thingComId 组件ID
     * @param <T>        组件类型
     * @return 设备组件
     */
    <T extends ThingCom> T getThingCom(String thingComId);

    /**
     * 获取设备组件
     *
     * @param thingComId 组件ID
     * @param <T>        组件类型
     * @return 设备组件
     * @throws ThingException 若设备组件不存在则抛出异常
     */
    <T extends ThingCom> T requireThingCom(String thingComId) throws ThingException;

    /**
     * 获取指定类型的设备组件集合
     *
     * @param type 指定设备组件类型
     * @param <T>  设备组件类型
     * @return 设备组件集合
     */
    <T extends ThingCom> Set<T> getThingComSet(Class<T> type);

    /**
     * 获取指定类型的设备组件
     *
     * @param type 指定设备组件类型
     * @param <T>  设备组件类型
     * @return 设备组件
     * @throws ThingException 找到多个则报错
     */
    <T extends ThingCom> T getThingCom(Class<T> type) throws ThingException;

    /**
     * 获取指定类型的设备组件
     *
     * @param type 设备组件类型
     * @param <T>  设备组件类型
     * @return 设备组件
     * @throws ThingException 若设备组件不存在则抛出异常
     */
    <T extends ThingCom> T requireThingCom(Class<T> type) throws ThingException;

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
