package com.github.tvbox.osc.bean;

import com.github.tvbox.kotlin.ui.utils.SP;

import java.util.LinkedHashMap;

/**
 * @author pj567
 * @date :2021/3/8
 * @description:
 */
public class IJKCode {
    private String name;
    private LinkedHashMap<String, String> option;
    private boolean selected;

    public void selected(boolean selected) {
        this.selected = selected;
        if (selected) {
            SP.INSTANCE.setIjkCodec(name);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String, String> getOption() {
        return option;
    }

    public void setOption(LinkedHashMap<String, String> option) {
        this.option = option;
    }
}