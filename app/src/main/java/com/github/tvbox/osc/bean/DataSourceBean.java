package com.github.tvbox.osc.bean;
public class DataSourceBean {

    private String name;
    private String url;

    private boolean isCurrent = false;
    private boolean isDefault = false;


    public DataSourceBean(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public DataSourceBean(String name, String url, boolean isCurrent, boolean isDefault) {
        this.name = name;
        this.url = url;
        this.isCurrent = isCurrent;
        this.isDefault = isDefault;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
