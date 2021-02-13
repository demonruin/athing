package com.github.ompc.athing.aliyun.qatest.puppet.component.impl;

import com.github.ompc.athing.aliyun.qatest.puppet.component.ResourceThingCom;
import org.junit.Assert;

import java.util.concurrent.atomic.AtomicReference;

public class ResourceThingComImpl implements ResourceThingCom {

    public final static String DEFAULT_VERSION = "1.0.0";
    private final AtomicReference<String> versionRef = new AtomicReference<>(DEFAULT_VERSION);

    @Override
    public void reset() {
        versionRef.set(DEFAULT_VERSION);
    }

    @Override
    public String getModuleId() {
        return "resource";
    }

    @Override
    public String getModuleVersion() {
        return versionRef.get();
    }

    @Override
    public void upgrade(Upgrade upgrade, Commit commit) throws Exception {
        Assert.assertNotNull(upgrade.getUpgradeFile());
        Assert.assertNotNull(upgrade.getUpgradeFile());
        versionRef.set(upgrade.getUpgradeVersion());
        commit.commit();
    }

}
