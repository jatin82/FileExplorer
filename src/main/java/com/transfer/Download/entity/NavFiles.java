package com.transfer.Download.entity;

public class NavFiles {
    private String entityName;
    private String entityUrl;
    private Boolean isDir;

    public NavFiles(String entityName, String entityUrl, Boolean isDir) {
        this.entityName = entityName;
        this.entityUrl = entityUrl;
        this.isDir = isDir;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityUrl() {
        return entityUrl;
    }

    public Boolean getIsDir() {
        return isDir;
    }

    @Override
    public String toString() {
        return "NavFiles{" +
                "entityName='" + entityName + '\'' +
                ", entityUrl='" + entityUrl + '\'' +
                ", isDir=" + isDir +
                '}';
    }
}
