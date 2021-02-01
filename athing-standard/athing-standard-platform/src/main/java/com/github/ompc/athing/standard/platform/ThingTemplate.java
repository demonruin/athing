package com.github.ompc.athing.standard.platform;

import com.github.ompc.athing.standard.component.Identifier;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.platform.domain.ThingPropertySnapshot;

import java.util.Map;
import java.util.Set;

/**
 * 设备模版
 */
public interface ThingTemplate {

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
     * @param thingComId         组件ID
     * @param expectThingComType 期待组件类型
     * @param <T>                组件类型
     * @return 设备组件
     */
    <T extends ThingCom> T getThingCom(String thingComId, Class<T> expectThingComType);

    /**
     * 批量设置设备属性
     *
     * @param propertyValueMap 属性值集合
     * @throws ThingPlatformException 操作失败
     */
    void batchSetProperties(Map<Identifier, Object> propertyValueMap) throws ThingPlatformException;

    /**
     * 批量获取属性快照
     *
     * @param identifiers 属性标识集合
     * @return 属性快照集合
     * @throws ThingPlatformException 操作失败
     */
    Map<Identifier, ThingPropertySnapshot> batchGetProperties(Set<Identifier> identifiers) throws ThingPlatformException;

}
