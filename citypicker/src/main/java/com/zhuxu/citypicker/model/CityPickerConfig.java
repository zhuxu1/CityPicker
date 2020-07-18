package com.zhuxu.citypicker.model;

import android.text.TextUtils;

import java.io.Serializable;

public class CityPickerConfig implements Serializable {
    public CityPickerConfig() {
    }

    // 是否显示 定位城市栏
    private boolean showLocation;
    // 是否显示 热门城市栏
    private boolean showHotCities;
    // 热门城市 标题
    private String strHotCities = "";
    // 热门城市 小标内容
    private String strHotCitiesIcon = "";
    // 是否自定义数据
    private boolean useCustomData = false;

    /**
     *
     * @param showLocation 是否显示 定位城市栏
     * @param showHotCities 是否显示 热门城市栏
     * @param strHotCities 热门城市 标题
     * @param strHotCitiesIcon 热门城市 小标内容
     * @param useCustomData 是否自定义数据
     */
    public CityPickerConfig(boolean showLocation, boolean showHotCities, String strHotCities, String strHotCitiesIcon, boolean useCustomData) {
        this.showLocation = showLocation;
        this.showHotCities = showHotCities;
        this.strHotCities = strHotCities;
        this.strHotCitiesIcon = strHotCitiesIcon;
        this.useCustomData = useCustomData;
    }

    public String getStrHotCitiesIcon() {
        return strHotCitiesIcon;
    }

    public void setStrHotCitiesIcon(String strHotCitiesIcon) {
        this.strHotCitiesIcon = strHotCitiesIcon;
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

}
