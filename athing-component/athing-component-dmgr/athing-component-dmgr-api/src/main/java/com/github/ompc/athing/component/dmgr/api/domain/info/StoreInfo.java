package com.github.ompc.athing.component.dmgr.api.domain.info;

/**
 * 存储信息
 */
public class StoreInfo {

    /**
     * 挂载点
     */
    private String mount;

    /**
     * 格式
     */
    private String format;

    /**
     * 存储容量(字节)
     */
    private long capacity;

    public String getMount() {
        return mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }
}
