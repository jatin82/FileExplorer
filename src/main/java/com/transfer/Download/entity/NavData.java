package com.transfer.Download.entity;

import java.util.List;

public class NavData {
    private String parentDir;
    private List<NavFiles> navFiles;

    public NavData(String parentDir, List<NavFiles> navFiles) {
        this.parentDir = parentDir;
        this.navFiles = navFiles;
    }

    public String getParentDir() {
        return parentDir;
    }

    public List<NavFiles> getNavFiles() {
        return navFiles;
    }

    @Override
    public String toString() {
        return "NavData{" +
                "parentDir='" + parentDir + '\'' +
                ", navFiles=" + navFiles +
                '}';
    }
}
