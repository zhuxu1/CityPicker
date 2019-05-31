package com.zaaach.citypicker.model;

import android.text.TextUtils;

import java.io.Serializable;

public class CityPickerConfig implements Serializable {
    public boolean isUseCustomHotData() {
        return useCustomHotData;
    }

    public void setUseCustomHotData(boolean useCustomHotData) {
        this.useCustomHotData = useCustomHotData;
    }

    public boolean isUseCustomData() {
        return useCustomData;
    }

    public void setUseCustomData(boolean useCustomData) {
        this.useCustomData = useCustomData;
    }

    public boolean isShowLocation() {
        return showLocation;
    }

    public void setShowLocation(boolean showLocation) {
        this.showLocation = showLocation;
    }

    public boolean isShowHotCities() {
        return showHotCities;
    }

    public void setShowHotCities(boolean showHotCities) {
        this.showHotCities = showHotCities;
    }

    public String getStrHotCities() {
        return strHotCities;
    }

    public void setStrHotCities(String strHotCities) {
        this.strHotCities = strHotCities;
    }

    public boolean hasSetStrHotCities() {
        return !TextUtils.isEmpty(strHotCities);
    }

    // 是否显示 定位城市栏
    private boolean showLocation;

    // 是否显示 热门城市栏
    private boolean showHotCities;

    // 热门城市 标题
    private String strHotCities = "";

    // 是否自定义数据
    private boolean useCustomData = false;

    // 是否自定义热门城市数据
    private boolean useCustomHotData = false;

}
