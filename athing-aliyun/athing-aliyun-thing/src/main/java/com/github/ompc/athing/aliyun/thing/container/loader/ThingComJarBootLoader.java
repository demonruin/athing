package com.github.ompc.athing.aliyun.thing.container.loader;

import com.github.ompc.athing.aliyun.framework.util.IOUtils;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.thing.ThingException;
import com.github.ompc.athing.standard.thing.boot.ThingComBoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.ServiceLoader;

/**
 * 设备组件库文件加载器
 */
public class ThingComJarBootLoader extends ThingComBootLoader {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final File comJarFile;

    /**
     * 设备组件库文件加载器
     *
     * @param comJarFile 设备组件库文件
     */
    public ThingComJarBootLoader(File comJarFile, OnBoot onBoot) {
        super(onBoot);
        this.comJarFile = comJarFile;
    }

    @Override
    public ThingCom[] onLoad(String productId, String thingId) throws Exception {
        final ThingComJarClassLoader loader = new ThingComJarClassLoader(comJarFile, getClass().getClassLoader());
        return new LinkedHashSet<ThingCom>() {{
            try {
                for (final ThingComBoot boot : ServiceLoader.load(ThingComBoot.class, loader)) {
                    add(getOnBoot().onBoot(boot));
                }
            } catch (Exception cause) {
                logger.warn("thing:/{}/{} booting jar failure, {} will be closed!", productId, thingId, loader);
                IOUtils.closeQuietly(loader);
                throw new ThingException(
                        productId,
                        thingId,
                        String.format("%s booting occur error, jar: %s;", loader, comJarFile),
                        cause
                );
            }
        }}.toArray(new ThingCom[0]);
    }
}
