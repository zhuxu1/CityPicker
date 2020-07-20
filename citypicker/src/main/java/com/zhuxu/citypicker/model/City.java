package com.zhuxu.citypicker.model;

import android.text.TextUtils;

import com.zhuxu.citypicker.CityPicker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author Bro0cL on 2016/1/26.
 */
public class City {
    private String name;
    private String province;
    private String pinyin;
    private String code;
    private String type = "";
    private boolean isHot = false;
    private boolean isLocation = false;

    public City(String name, String province, String pinyin, String code) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
    }

    public City(String name, String province, String pinyin, String code, boolean isHot, boolean isLocation) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
        this.isHot = isHot;
        this.isLocation = isLocation;
    }

    public void setHot() {
        isHot = true;
        setType(CityPicker.FLAG_HOT);
    }

    /***
     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
     * @return
     */
    public String getSection() {
        if (TextUtils.isEmpty(pinyin)) {
            return "#";
        } else {
            String c = pinyin.substring(0, 1);
            Pattern p = Pattern.compile("[a-zA-Z]");
            Matcher m = p.matcher(c);
            if (m.matches()) {
                return c.toUpperCase();
            }
            //在添加定位和热门数据时设置的section就是‘定’、’热‘开头
            else if (TextUtils.equals(c, "定") || TextUtils.equals(c, "热"))
                return pinyin;
            else if (isHot) {
                return pinyin;
            } else
                return "#";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    public boolean isLocation() {
        return isLocation;
    }

    public void setLocation(boolean location) {
        isLocation = location;
    }

    public String getType() {
        return type;
    }

    public City setType(String type) {
        this.type = type;
        return this;
    }
}
