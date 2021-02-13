package com.github.ompc.athing.standard.thing;

import com.github.ompc.athing.standard.component.ThingCom;

import java.util.Map;
import java.util.Set;

/**
 * 设备组件容器
 */
public interface ThingComContainer {

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
     * @return 设备组件，若没有找到匹配组件，返回null
     */
    ThingCom getThingCom(String thingComId);

    /**
     * 获取设备组件
     *
     * @param thingComId 组件ID
     * @return 设备组件
     * @throws ThingException 若找不到匹配的设备组件则抛出异常
     */
    ThingCom requireThingCom(String thingComId) throws ThingException;

    /**
     * 获取设备组件
     *
     * @param thingComId 组件ID
     * @param expectType 期待组件类型
     * @param <T>        组件类型
     * @return 设备组件，若没有找到匹配组件，返回null
     */
    <T extends ThingCom> T getThingCom(String thingComId, Class<T> expectType);

    /**
     * 获取设备组件
     *
     * @param thingComId 组件ID
     * @param expectType 期待组件类型
     * @param <T>        组件类型
     * @return 设备组件
     * @throws ThingException 若找不到匹配的设备组件则抛出异常
     */
    <T extends ThingCom> T requireThingCom(String thingComId, Class<T> expectType) throws ThingException;

    /**
     * 根据组件类型获取设备组件
     *
     * @param expectType 期待组件类型
     * @param <T>        组件类型
     * @return 设备组件集合
     */
    <T extends ThingCom> Map<String, T> getThingComMapOfType(Class<T> expectType);

    /**
     * 根据设备组件类型获取唯一的设备组件
     *
     * @param expectType 期待组件类型
     * @param <T>        组件类型
     * @return 设备组件，若没有找到匹配组件，返回null
     * @throws ThingException 若找不到匹配到多个设备组件，则抛出异常
     */
    <T extends ThingCom> T getUniqueThingComOfType(Class<T> expectType) throws ThingException;

    /**
     * 根据设备组件类型获取唯一的设备组件
     *
     * @param expectType 期待组件类型
     * @param <T>        组件类型
     * @return 设备组件
     * @throws ThingException 若找不到匹配的设备组件，或找到多个，则抛出异常
     */
    <T extends ThingCom> T requireUniqueThingComOfType(Class<T> expectType) throws ThingException;

}
