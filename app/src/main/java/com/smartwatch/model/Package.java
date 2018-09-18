package com.smartwatch.model;

/**
 * Created by Robert Zagórski on 2016-12-14.
 */

public class Package implements Comparable<Package> {
    private String name;
    private String version;
    private String packageName;
    private long mobileData;
    private long wifiData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getWifiData() {
        return wifiData;
    }

    public void setWifiData(long wifiData) {
        this.wifiData = wifiData;
    }

    public long getMobileData() {
        return mobileData;
    }

    public void setMobileData(long mobileData) {
        this.mobileData = mobileData;
    }

    @Override
    public int compareTo(Package u) {
        return (int) (getMobileData() - u.getMobileData());
    }

}
