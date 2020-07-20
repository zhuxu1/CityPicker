package com.zhuxu.citypicker.model;

import com.zhuxu.citypicker.CityPicker;

public class LocatedCity extends City {

    public LocatedCity(String name, String province, String code) {
        super(name, province, "定位城市", code);
        setType(CityPicker.FLAG_LOCATION);
    }

    @Override
    public LocatedCity setType(String type) {
        super.setType(type);
        return this;
    }
}
