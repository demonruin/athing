package com.github.ompc.athing.component.dmgr.core.impl;

import com.github.ompc.athing.component.dmgr.api.domain.usage.*;
import com.github.ompc.athing.component.dmgr.core.util.FormatUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.CentralProcessor.TickType;
import oshi.hardware.GlobalMemory;
import oshi.hardware.NetworkIF;
import oshi.hardware.PowerSource;
import oshi.software.os.OSFileStore;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 设备使用率计算器
 */
class DeviceUsageCalculator {

    private final CentralProcessor processor;
    private final GlobalMemory memory;
    private final Collection<NetworkIF> networkIFs;
    private final Collection<PowerSource> powers;
    private final Collection<OSFileStore> stores;
    private final AtomicReference<long[]> ticksRef;

    public DeviceUsageCalculator(SystemInfo info) {
        this.processor = info.getHardware().getProcessor();
        this.memory = info.getHardware().getMemory();
        this.networkIFs = info.getHardware().getNetworkIFs();
        this.powers = info.getHardware().getPowerSources();
        this.stores = info.getOperatingSystem().getFileSystem().getFileStores();
        this.ticksRef = new AtomicReference<>(processor.getSystemCpuLoadTicks());
    }

    // 计算CPU使用率
    public CpuUsage calculateCpuUsage() {

        // 计算ticks
        final long[] bTicks = ticksRef.get();
        final long[] cTicks = processor.getSystemCpuLoadTicks();
        final long[] dTicks = new long[cTicks.length];
        long temp = 0;
        for (final TickType type : TickType.values()) {
            final int index = type.getIndex();
            dTicks[index] = bTicks[index] - cTicks[index];
            temp += dTicks[index];
        }
        final long dSum = temp;

        // 计算load
        final double[] loads = processor.getSystemLoadAverage(3);

        final CpuUsage cpuUsage = new CpuUsage();
        cpuUsage.setContextSwitches(processor.getContextSwitches());
        cpuUsage.setInterrupts(processor.getInterrupts());
        cpuUsage.setLoad01(FormatUtils.rate(loads[0]));
        cpuUsage.setLoad05(FormatUtils.rate(loads[1]));
        cpuUsage.setLoad15(FormatUtils.rate(loads[2]));
        cpuUsage.setIdle(FormatUtils.rate(1.0f * dTicks[TickType.IDLE.getIndex()] / dSum));
        cpuUsage.setSteal(FormatUtils.rate(1.0f * dTicks[TickType.STEAL.getIndex()] / dSum));
        cpuUsage.setSoftIrq(FormatUtils.rate(1.0f * dTicks[TickType.SOFTIRQ.getIndex()] / dSum));
        cpuUsage.setIrq(FormatUtils.rate(1.0f * dTicks[TickType.IRQ.getIndex()] / dSum));
        cpuUsage.setUser(FormatUtils.rate(1.0f * dTicks[TickType.USER.getIndex()] / dSum));
        cpuUsage.setSystem(FormatUtils.rate(1.0f * dTicks[TickType.SYSTEM.getIndex()] / dSum));
        cpuUsage.setIowait(FormatUtils.rate(1.0f * dTicks[TickType.IOWAIT.getIndex()] / dSum));
        return cpuUsage;
    }

    public MemoryUsage calculateMemoryUsage() {
        final long available = memory.getAvailable();
        final MemoryUsage memoryUsage = new MemoryUsage();
        memoryUsage.setAvailable(available);
        memoryUsage.setAvailableRate(FormatUtils.rate(1.0f * available / memory.getTotal()));
        return memoryUsage;
    }

    public NetworkUsage[] calculateNetworkUsage() {
        return networkIFs.stream()
                .map(networkIF -> {
                    networkIF.updateAttributes();
                    final NetworkUsage networkUsage = new NetworkUsage();
                    networkUsage.setName(networkIF.getName());
                    networkUsage.setRecv(networkIF.getBytesRecv());
                    networkUsage.setSent(networkIF.getBytesSent());
                    networkUsage.setRecvPkt(networkIF.getPacketsRecv());
                    networkUsage.setSentPkt(networkIF.getPacketsSent());
                    networkUsage.setDropPkt(networkIF.getInDrops());
                    networkUsage.setErrorPkt(networkIF.getInErrors() + networkIF.getOutErrors());
                    return networkUsage;
                })
                .toArray(NetworkUsage[]::new);
    }

    public PowerUsage[] calculatePowerUsage() {
        return powers.stream()
                .map(power -> {
                    power.updateAttributes();
                    final float remainingRate = FormatUtils.rate(power.getRemainingCapacityPercent());
                    final PowerUsage powerUsage = new PowerUsage();
                    powerUsage.setName(power.getName());
                    powerUsage.setRemainingRate(remainingRate);
                    powerUsage.setRemaining(FormatUtils.toInt(power.getMaxCapacity() * remainingRate));
                    return powerUsage;
                })
                .toArray(PowerUsage[]::new);
    }

    public StoreUsage[] calculateStoreUsage() {
        return stores.stream()
                .map(store -> {
                    store.updateAttributes();
                    final long available = store.getUsableSpace();
                    final StoreUsage storeUsage = new StoreUsage();
                    storeUsage.setMount(store.getMount());
                    storeUsage.setAvailable(available);
                    storeUsage.setAvailableRate(FormatUtils.rate(1.0f * available / store.getTotalSpace()));
                    return storeUsage;
                })
                .toArray(StoreUsage[]::new);
    }

}
