package com.github.ompc.athing.aliyun.thing.kernel;

import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.boot.ThingComBoot;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 设备组件库文件加载器
 */
public class ThingComJarLoader {

    private final File comJarFile;

    /**
     * 设备组件库文件加载器
     *
     * @param comJarFile 设备组件库文件
     */
    public ThingComJarLoader(File comJarFile) {
        this.comJarFile = comJarFile;
    }

    /**
     * 寻找被删除组件的classloader是否在存活组件中存在，如果不存在则释放classloader
     *
     * @param survivals 存活的组件
     * @param remove    被删除的组件
     */
    private static void closeClassLoaderIfNecessary(Collection<ThingCom> survivals, ThingCom remove) {

        // 如果被删除的组件不是动态加载进来的，则忽略
        if (!(remove.getClass().getClassLoader() instanceof ThingComJarClassLoader)) {
            return;
        }

        final ThingComJarClassLoader removeLoader = (ThingComJarClassLoader) remove.getClass().getClassLoader();

        // 如果没有找到，则关闭
        if (survivals.stream().noneMatch(com -> com.getClass().getClassLoader() == removeLoader)) {
            removeLoader.close();
        }

    }

    /**
     * 清理组件集合
     * <p>
     * 如有必要关闭对应组件的ClassLoader
     * </p>
     *
     * @param thingComCollection 组件集合
     */
    public static void cleanUpThingComCollection(Collection<ThingCom> thingComCollection) {
        final Set<ThingCom> thingComSet = new LinkedHashSet<>(thingComCollection);
        final Iterator<ThingCom> thingComIt = thingComSet.iterator();
        while (thingComIt.hasNext()) {
            final ThingCom remove = thingComIt.next();
            thingComIt.remove();
            closeClassLoaderIfNecessary(thingComSet, remove);
        }
    }

    /**
     * 加载设备组件引导程序
     *
     * @return 设备组件引导程序
     * @throws IOException 加载失败
     */
    public Collection<ThingComBoot> load() throws IOException {
        final Collection<ThingComBoot> boots = new ArrayList<>();
        final ThingComJarClassLoader loader = new ThingComJarClassLoader(comJarFile, getClass().getClassLoader());
        try {
            for (final ThingComBoot boot : ServiceLoader.load(ThingComBoot.class, loader)) {
                boots.add(boot);
            }
            return boots;
        } catch (Exception cause) {
            loader.close();
            throw new IOException(String.format("failure to load: %s", comJarFile), cause);
        }
    }

}
