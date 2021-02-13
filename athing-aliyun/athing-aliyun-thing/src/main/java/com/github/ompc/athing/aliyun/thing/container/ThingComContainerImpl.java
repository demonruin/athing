package com.github.ompc.athing.aliyun.thing.container;

import com.github.ompc.athing.aliyun.framework.component.ThComMetaHelper;
import com.github.ompc.athing.aliyun.framework.component.meta.ThComMeta;
import com.github.ompc.athing.aliyun.thing.container.loader.ThingComJarClassLoader;
import com.github.ompc.athing.aliyun.thing.container.loader.ThingComLoader;
import com.github.ompc.athing.aliyun.thing.util.DependentTree;
import com.github.ompc.athing.aliyun.thing.util.ThingComInjectUtils;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.ThingComContainer;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.boot.Disposable;
import com.github.ompc.athing.standard.thing.boot.Initializing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 设备组件容器实现
 */
public class ThingComContainerImpl implements ThingComContainer {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String productId;
    private final String thingId;
    private final Map<String, ThingComStub> thingComStubMap = new HashMap<>();
    private final DependentTree<String> dependentTree = new DependentTree<>();

    protected ThingComContainerImpl(String productId, String thingId, Set<ThingComLoader> loaders) throws ThingException {
        this.productId = productId;
        this.thingId = thingId;
        loading(loaders);
    }

    // 容器加载组件
    private void loading(Set<ThingComLoader> thingComLoaders) throws ThingException {

        try {

            // 遍历整个组件加载器
            for (final ThingComLoader thingComLoader : thingComLoaders) {

                // 遍历组件加载器加载上来的组件实例
                for (final ThingCom thingCom : thingComLoader.onLoad(productId, thingId)) {

                    // 遍历组件实例对应的组件接口
                    for (final ThComMeta meta : ThComMetaHelper.getThComMetaMap(thingCom.getClass()).values()) {

                        // 检查设备组件ID是否冲突 & 注册到容器中
                        final ThingComStub exist;
                        if ((exist = thingComStubMap.putIfAbsent(meta.getThingComId(), new ThingComStub(meta, thingCom))) != null) {
                            throw new ThingException(productId, thingId, String.format(
                                    "duplicate component: %s, conflict: [ %s, %s ]",
                                    meta.getThingComId(),
                                    meta.getThingComType().getName(),
                                    exist.getThComMeta().getThingComType().getName()
                            ));
                        }

                    }// for: meta
                }// for: com
            }// for: loader

            // 注入依赖
            final ThDependInjector injector = new ThDependInjector(productId, thingId, thingComStubMap, dependentTree);
            for (final ThingComStub stub : thingComStubMap.values()) {
                for (final Field field : ThingComInjectUtils.getThDependFields(stub.getThingCom().getClass())) {
                    injector.inject(stub, field);
                }
            }

        } catch (Exception cause) {

            // 任何一个环节出错都必须导致容器被销毁，释放初始化过程中所申请的所有资源
            destroyContainer();

            // 继续对外抛出
            throw new ThingException(productId, thingId, "container loading components occur error", cause);

        }

    }

    /**
     * 初始化容器
     *
     * @param thing 设备
     * @throws ThingException 初始化失败
     */
    protected void initContainer(Thing thing) throws ThingException {
        for (final String thingComId : dependentTree) {
            final ThingCom thingCom = requireThingCom(thingComId);
            if (thingCom instanceof Initializing) {
                try {
                    ((Initializing) thingCom).initialized(thing);
                } catch (Exception cause) {
                    throw new ThingException(
                            thing,
                            String.format("initializing component: %s occur error!", thingComId),
                            cause
                    );
                }
            }
        }
    }

    /**
     * 销毁容器
     */
    protected void destroyContainer() {

        // 销毁容器中所有可销毁的组件
        thingComStubMap.values().stream()
                .filter(stub -> stub.getThingCom() instanceof Disposable)
                .forEach(stub -> {
                    try {
                        ((Disposable) stub.getThingCom()).destroy();
                    } catch (Exception cause) {
                        logger.warn("thing:/{}/{} destroy container occur an negligible error when destroy component: {};",
                                productId, thingId, stub.getThingComId(), cause);
                    }
                });

        // 关闭容器中所有组件库加载器
        thingComStubMap.values().stream()
                .map(stub -> stub.getClass().getClassLoader())
                .filter(loader -> loader instanceof ThingComJarClassLoader)
                .collect(Collectors.toSet())
                .forEach(loader -> {
                    try {
                        ((ThingComJarClassLoader) loader).close();
                    } catch (Exception cause) {
                        logger.warn("thing:/{}/{} destroy container occur an negligible error when closing loader: {};",
                                productId, thingId, loader, cause);
                    }
                });
    }


    /**
     * 获取设备组件存根集合
     *
     * @return 设备组件存根集合
     */
    public Map<String, ThingComStub> getThingComStubMap() {
        return thingComStubMap;
    }

    @Override
    public Set<String> getThingComIds() {
        return thingComStubMap.keySet();
    }

    @Override
    public ThingCom getThingCom(String thingComId) {
        if (thingComStubMap.containsKey(thingComId)) {
            return thingComStubMap.get(thingComId).getThingCom();
        }
        return null;
    }

    @Override
    public ThingCom requireThingCom(String thingComId) throws ThingException {
        if (!thingComStubMap.containsKey(thingComId)) {
            throw new ThingException(productId, thingId, String.format("require component: %s, but not found!",
                    thingComId
            ));
        }
        return thingComStubMap.get(thingComId).getThingCom();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ThingCom> T getThingCom(String thingComId, Class<T> expectType) {
        final ThingCom thingCom = getThingCom(thingComId);
        return expectType.isInstance(thingCom)
                ? (T) thingCom
                : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ThingCom> T requireThingCom(String thingComId, Class<T> expectType) throws ThingException {
        final ThingCom thingCom = requireThingCom(thingComId);
        if (!expectType.isInstance(thingCom)) {
            throw new ThingException(productId, thingId, String.format("require component: %s, type not match, expect: %s, actual: %s",
                    thingComId,
                    expectType.getName(),
                    thingCom.getClass().getName()
            ));
        }
        return (T) thingCom;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ThingCom> Map<String, T> getThingComMapOfType(Class<T> expectType) {
        return thingComStubMap.values().stream()
                .filter(stub -> expectType.isInstance(stub.getThingCom()))
                .collect(Collectors.toMap(
                        ThingComStub::getThingComId,
                        stub -> (T) stub.getThingCom()
                ));
    }

    @Override
    public <T extends ThingCom> T getUniqueThingComOfType(Class<T> expectType) throws ThingException {

        final Set<T> founds = new HashSet<>(getThingComMapOfType(expectType).values());
        if (founds.size() > 1) {
            throw new ThingException(productId, thingId, String.format("component type: %s is not unique, expect: 1, actual: %s, found: %s",
                    expectType.getName(),
                    founds.size(),
                    founds
            ));
        }
        return founds.isEmpty()
                ? null
                : founds.iterator().next();
    }

    @Override
    public <T extends ThingCom> T requireUniqueThingComOfType(Class<T> expectType) throws ThingException {
        final T found = getUniqueThingComOfType(expectType);
        if (null == found) {
            throw new ThingException(productId, thingId, String.format("component type: %s is require, but not found!",
                    expectType.getName()
            ));
        }
        return found;
    }

}
