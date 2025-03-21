package com.github.tvbox.osc.bean;

import java.util.Objects;

public class DataSourceBean {

    private String name;
    private String url;

    private boolean isCurrent = false;


    public DataSourceBean(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public DataSourceBean(String name, String url, boolean isCurrent) {
        this.name = name;
        this.url = url;
        this.isCurrent = isCurrent;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DataSourceBean that = (DataSourceBean) o;
        return isCurrent == that.isCurrent && Objects.equals(name, that.name) && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, isCurrent);
    }
}
