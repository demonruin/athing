package com.github.ompc.athing.component.dmgr.api;

import com.github.ompc.athing.component.dmgr.api.domain.info.*;
import com.github.ompc.athing.component.dmgr.api.domain.usage.*;
import com.github.ompc.athing.standard.component.ThingCom;
import com.github.ompc.athing.standard.component.annotation.ThCom;
import com.github.ompc.athing.standard.component.annotation.ThProperty;

import static com.github.ompc.athing.component.dmgr.api.DmgrThingCom.THING_COM_ID;

/**
 * athing设备管理模块
 */
@ThCom(id = THING_COM_ID, name = "athing device manager", desc = "device manager for athing")
public interface DmgrThingCom extends ThingCom {

    String THING_COM_ID = "athing_dmgr";

    /**
     * 获取CPU信息
     *
     * @return CPU信息
     */
    @ThProperty
    CpuInfo getCpuInfo();

    /**
     * 获取内存信息
     *
     * @return 内存信息
     */
    @ThProperty
    MemoryInfo getMemoryInfo();

    /**
     * 获取网络信息
     *
     * @return 网络信息
     */
    @ThProperty
    NetworkInfo[] getNetworkInfo();

    /**
     * 获取电源信息
     *
     * @return 电源信息
     */
    @ThProperty
    PowerInfo[] getPowerInfo();

    /**
     * 获取存储信息
     *
     * @return 存储信息
     */
    @ThProperty
    StoreInfo[] getStoreInfo();

    /**
     * 获取CPU使用率
     *
     * @return CPU使用率
     */
    @ThProperty
    CpuUsage getCpuUsage();

    /**
     * 获取内存使用率
     *
     * @return 内存使用率
     */
    @ThProperty
    MemoryUsage getMemoryUsage();

    /**
     * 获取网络使用率
     *
     * @return 网络使用率
     */
    @ThProperty
    NetworkUsage[] getNetworkUsage();

    /**
     * 获取电源使用率
     *
     * @return 电源使用率
     */
    @ThProperty
    PowerUsage[] getPowerUsage();

    /**
     * 获取存储使用率
     *
     * @return 存储使用率
     */
    @ThProperty
    StoreUsage[] getStoreUsage();

}
