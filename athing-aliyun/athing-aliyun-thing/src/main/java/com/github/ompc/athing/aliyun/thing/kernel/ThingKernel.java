package com.github.ompc.athing.aliyun.thing.kernel;

import com.github.ompc.athing.aliyun.framework.component.ThComMetaHelper;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.boot.Disposable;
import com.github.ompc.athing.standard.thing.boot.Initializing;
import com.github.ompc.athing.standard.thing.boot.Modular;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 设备内核
 */
public class ThingKernel {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AtomicInteger anonymousIdSequencer = new AtomicInteger(1000);
    private final Set<ThingCom> thingComSet;
    private final Map<String, ThingComStub> thingComStubMap;

    /**
     * 设备内核
     *
     * @param thingComSet 设备组件集合
     */
    ThingKernel(Set<ThingCom> thingComSet) {
        this.thingComSet = Collections.unmodifiableSet(verifyThingCom(thingComSet));
        this.thingComStubMap = Collections.unmodifiableMap(toThingComStubMap(thingComSet));
    }

    // 校验设备组件
    private Set<ThingCom> verifyThingCom(Set<ThingCom> thingComSet) {

        // 校验模块化设备组件的模块ID是否重复
        final Map<String, Modular> uniqueModuleMap = new HashMap<>();
        thingComSet.stream()
                .filter(thingCom -> thingCom instanceof Modular)
                .map(thingCom -> (Modular) thingCom)
                .forEach(modular -> {
                    final Modular exist;
                    if ((exist = uniqueModuleMap.putIfAbsent(modular.getModuleId(), modular)) != null) {
                        throw new IllegalArgumentException(String.format(
                                "duplicate module: %s, conflict: [ %s, %s ]",
                                modular.getModuleId(),
                                modular.getClass().getName(),
                                exist.getClass().getName()
                        ));
                    }
                });
        return thingComSet;
    }

    // 组件集合转换为组件存根集合
    private Map<String, ThingComStub> toThingComStubMap(Set<ThingCom> thingComSet) {
        final Map<String, ThingComStub> thingComStubMap = new HashMap<>();
        thingComSet.forEach(thingCom -> ThComMetaHelper.getThComMetaMap(thingCom.getClass()).forEach((thingComId, meta) -> {
            // 检查设备组件ID是否冲突
            final ThingComStub exist;
            if ((exist = thingComStubMap.putIfAbsent(thingComId, new ThingComStub(meta, thingCom))) != null) {
                throw new IllegalArgumentException(String.format(
                        "duplicate component: %s, conflict: [ %s, %s ]",
                        thingComId,
                        meta.getThingComType().getName(),
                        exist.getThComMeta().getThingComType().getName()
                ));
            }
        }));


        return thingComStubMap;
    }


    /**
     * 获取加载的所有设备组件集合
     *
     * @return 设备组件集合
     */
    public Set<ThingCom> getThingComSet() {
        return thingComSet;
    }

    /**
     * 根据模块ID寻找模块
     *
     * @param moduleId 模块ID
     * @return 模块化组件
     */
    public Modular getModule(String moduleId) {
        return thingComSet.stream()
                .filter(thingCom -> thingCom instanceof Modular)
                .map(thingCom -> (Modular) thingCom)
                .filter(modular -> Objects.equals(modular.getModuleId(), moduleId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取设备组件存根集合
     *
     * @return 设备组件存根集合
     */
    public Map<String, ThingComStub> getThingComStubMap() {
        return thingComStubMap;
    }

    /**
     * 初始化内核
     *
     * @param thing 初始化设备
     * @throws ThingException 初始化失败
     */
    public void initialized(Thing thing) throws ThingException {
        for (final ThingCom thingCom : thingComSet) {
            if (thingCom instanceof Initializing) {
                final Set<String> thingComIds = getThingComIds(thingCom);
                try {
                    ((Initializing) thingCom).initialized();
                    logger.debug("{}/kernel component init completed, components={};",
                            thing,
                            thingComIds
                    );
                } catch (Exception cause) {
                    throw new ThingException(
                            thing,
                            String.format("component init error, components: %s", thingComIds),
                            cause
                    );
                }
            }
        }
    }

    private Set<String> getThingComIds(ThingCom thingCom) {
        return thingComStubMap.entrySet().stream()
                .filter(entry -> entry.getValue().getThingCom() == thingCom)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * 销毁设备内核
     *
     * @param thing 销毁设备
     */
    public void destroy(Thing thing) {
        for (final ThingCom thingCom : thingComSet) {
            if (thingCom instanceof Disposable) {
                final Set<String> thingComIds = getThingComIds(thingCom);
                try {
                    ((Disposable) thingCom).destroy();
                    logger.debug("{}/kernel destroy component completed, components={};", thing, thingComIds);
                } catch (Exception cause) {
                    logger.warn("{}/kernel destroy component failure, components={};",
                            thing,
                            thingComIds,
                            cause
                    );
                }
            }
        }// for

        // 清理已加载的组件集合
        ThingComJarLoader.cleanUpThingComCollection(thingComSet);

    }

}
