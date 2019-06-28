package com.zhuxu.citypicker.adapter;

import com.zhuxu.citypicker.model.City;

public interface InnerListener {
    void dismiss(int position, City data);
    void locate();
}
