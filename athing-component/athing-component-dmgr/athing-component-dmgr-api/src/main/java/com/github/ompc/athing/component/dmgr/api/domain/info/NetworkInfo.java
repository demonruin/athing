package com.github.ompc.athing.component.dmgr.api.domain.info;

/**
 * 网络信息
 */
public class NetworkInfo {

    /**
     * 网卡名
     */
    private String name;

    /**
     * Mac地址
     */
    private String mac;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

}
