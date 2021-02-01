package com.github.ompc.athing.aliyun.thing.kernel;

import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.Thing;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.boot.Disposable;
import com.github.ompc.athing.standard.thing.boot.ThingComBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 设备引导程序
 */
public class ThingBoot {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Set<ThingComBootUp> thingComBootUpSet = new LinkedHashSet<>();

    /**
     * 装入设备组件库文件，从库文件中加载设备组件引导程序
     *
     * @param comJarFile 设备组件库文件
     * @return this
     * @throws IOException 加载库文件失败
     */
    public ThingBoot booting(File comJarFile) throws IOException {
        return booting(comJarFile, boot -> null);
    }

    /**
     * 装入设备组件库文件，从库文件中加载设备组件引导程序
     *
     * @param comJarFile   设备组件库文件
     * @param getArguments 获取引导参数
     * @return this
     * @throws IOException 加载库文件失败
     */
    public ThingBoot booting(File comJarFile, GetArguments getArguments) throws IOException {
        for (final ThingComBoot thingComBoot : new ThingComJarLoader(comJarFile).load()) {
            booting(thingComBoot, getArguments.getArguments(thingComBoot));
        }
        return this;
    }

    /**
     * 装入设备组件引导程序
     *
     * @param boot 设备组件引导程序
     * @return this
     */
    public ThingBoot booting(ThingComBoot boot) {
        return booting(boot, null);
    }

    /**
     * 装入设备组件引导程序
     *
     * @param boot      设备组件引导程序
     * @param arguments 引导参数
     * @return this
     */
    public ThingBoot booting(ThingComBoot boot, String arguments) {
        thingComBootUpSet.add(thing -> boot.bootUp(thing, arguments));
        return this;
    }

    // 引导设备组件
    private Set<ThingCom> bootUpThingCom(Thing thing, Set<Disposable> disposables) throws Exception {
        final Set<ThingCom> thingComSet = new HashSet<>();
        for (final ThingComBootUp bootUp : thingComBootUpSet) {
            final ThingCom thingCom = bootUp.bootUp(thing);
            thingComSet.add(thingCom);
            if (thingCom instanceof Disposable) {
                disposables.add((Disposable) thingCom);
            }
        }
        return thingComSet;
    }

    // 释放已分配资源
    private void destroyDisposableThingCom(Thing thing, Set<Disposable> disposables) {
        disposables.forEach(thingCom -> {
            try {
                thingCom.destroy();
            } catch (Exception cause) {
                logger.warn("{}/boot destroy component failure!", thing, cause);
            }
        });
    }

    /**
     * 引导设备内核启动
     *
     * @param thing 设备
     * @return 设备内核
     * @throws ThingException 引导设备内核启动失败
     */
    public ThingKernel bootUp(Thing thing) throws ThingException {
        final Set<Disposable> disposables = new LinkedHashSet<>();
        final Set<ThingCom> thingComSet = new LinkedHashSet<>();
        try {
            thingComSet.addAll(bootUpThingCom(thing, disposables));
            return new ThingKernel(thingComSet);
        } catch (Throwable cause) {
            logger.warn("{}/boot boot-up failure!", thing, cause);

            // 启动失败需要主动销毁组件资源
            destroyDisposableThingCom(thing, disposables);

            // 清理已加载的组件集合
            ThingComJarLoader.cleanUpThingComCollection(thingComSet);

            // 继续向外转抛异常
            throw new ThingException(thing, "boot-up failure!", cause);
        }

    }

    /**
     * 获取引导参数
     */
    public interface GetArguments {

        /**
         * 获取引导参数
         *
         * @param boot 引导程序
         * @return 引导参数
         */
        String getArguments(ThingComBoot boot);

    }

    /**
     * 设备组件启动
     */
    private interface ThingComBootUp {

        /**
         * 启动设备组件
         *
         * @param thing 启动设备
         * @return 设备组件
         * @throws Exception 启动失败
         */
        ThingCom bootUp(Thing thing) throws Exception;

    }

}
