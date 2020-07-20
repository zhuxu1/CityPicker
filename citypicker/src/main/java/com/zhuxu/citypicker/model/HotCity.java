package com.zhuxu.citypicker.model;

public class HotCity extends City {

    public HotCity(String name, String province, String code) {
        super(name, province, name, code, true, false);
//        super(name, province, "热门城市", code);
    }

    @Override
    public HotCity setType(String type) {
        super.setType(type);
        return this;
    }
}
