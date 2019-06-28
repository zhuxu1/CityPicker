package com.zhuxu.citypicker.adapter;

import com.zhuxu.citypicker.model.City;

public interface OnPickListener {
    void onPick(int position, City data);
    void onLocate();
    void onCancel();
}
