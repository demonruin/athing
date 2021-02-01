package com.github.ompc.athing.aliyun.framework.component.meta;

import com.github.ompc.athing.standard.component.Identifier;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.component.annotation.ThCom;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.emptyMap;

/**
 * 设备组件元数据
 * <p>
 * 设备组件元数据由设备组件注解解析得来
 * </p>
 */
public class ThComMeta {

    private final static AtomicInteger sequencer = new AtomicInteger(1000);
    private final String thingComId;
    private final ThCom anThCom;
    private final Class<?> thingComType;
    private final Map<Identifier, ThEventMeta> identityThEventMetaMap;
    private final Map<Identifier, ThPropertyMeta> identityThPropertyMetaMap;
    private final Map<Identifier, ThServiceMeta> identityThServiceMetaMap;

    /**
     * 匿名设备组件
     *
     * @param thingComType 设备组件接口类型
     */
    public ThComMeta(final Class<?> thingComType) {
        this(
                String.format("%s#%d", thingComType.getName(), sequencer.getAndIncrement()),
                null,
                thingComType,
                emptyMap(),
                emptyMap(),
                emptyMap()
        );
    }

    /**
     * 命名设备组件
     *
     * @param anThCom                   设备组件注解
     * @param thingComType              设备组件接口类型
     * @param identityThEventMetaMap    事件元数据集合
     * @param identityThPropertyMetaMap 属性元数据集合
     * @param identityThServiceMetaMap  服务元数据集合
     */
    public ThComMeta(final ThCom anThCom,
                     final Class<?> thingComType,
                     final Map<Identifier, ThEventMeta> identityThEventMetaMap,
                     final Map<Identifier, ThPropertyMeta> identityThPropertyMetaMap,
                     final Map<Identifier, ThServiceMeta> identityThServiceMetaMap) {
        this(
                anThCom.id(),
                anThCom,
                thingComType,
                identityThEventMetaMap,
                identityThPropertyMetaMap,
                identityThServiceMetaMap
        );
    }


    private ThComMeta(final String thingComId,
                      final ThCom anThCom,
                      final Class<?> thingComType,
                      final Map<Identifier, ThEventMeta> identityThEventMetaMap,
                      final Map<Identifier, ThPropertyMeta> identityThPropertyMetaMap,
                      final Map<Identifier, ThServiceMeta> identityThServiceMetaMap) {
        this.thingComId = thingComId;
        this.anThCom = anThCom;
        this.thingComType = thingComType;
        this.identityThEventMetaMap = identityThEventMetaMap;
        this.identityThPropertyMetaMap = identityThPropertyMetaMap;
        this.identityThServiceMetaMap = identityThServiceMetaMap;
    }

    /**
     * 是否匿名组件
     * <p>
     * 匿名组件不会参与到物模型中暴露自己的属性、方法、事件，只提供接口本地调用。
     * 适用于功能型组件
     * </p>
     *
     * @return TRUE | FALSE
     */
    public boolean isAnonymous() {
        return anThCom == null;
    }

    /**
     * 获取设备组件ID
     *
     * @return 设备组件ID
     */
    public String getThingComId() {
        return thingComId;
    }

    /**
     * 获取设备组件名称
     *
     * @return 设备组件名称
     */
    public String getThingComName() {
        return isAnonymous() ? null : anThCom.name();
    }

    /**
     * 获取设备组件描述
     *
     * @return 设备组件描述
     */
    public String getThingComDesc() {
        return isAnonymous() ? null : anThCom.desc();
    }

    /**
     * 获取设备组件类型
     * <p>
     * 设备组件类型必须是一个接口，且继承于{@link ThingCom}
     * </p>
     *
     * @return 设备组件类型
     */
    public Class<?> getThingComType() {
        return thingComType;
    }


    /**
     * 获取标识事件元数据集合
     *
     * @return 标识事件元数据集合
     */
    public Map<Identifier, ThEventMeta> getIdentityThEventMetaMap() {
        return identityThEventMetaMap;
    }

    /**
     * 获取标识属性元数据集合
     *
     * @return 标识属性元数据集合
     */
    public Map<Identifier, ThPropertyMeta> getIdentityThPropertyMetaMap() {
        return identityThPropertyMetaMap;
    }

    /**
     * 获取标识服务元数据集合
     *
     * @return 标识服务元数据集合
     */
    public Map<Identifier, ThServiceMeta> getIdentityThServiceMetaMap() {
        return identityThServiceMetaMap;
    }

}
