package com.zhuxu.citypicker;

import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.zhuxu.citypicker.adapter.OnPickListener;
import com.zhuxu.citypicker.model.City;
import com.zhuxu.citypicker.model.CityPickerConfig;
import com.zhuxu.citypicker.model.HotCity;
import com.zhuxu.citypicker.model.LocateState;
import com.zhuxu.citypicker.model.LocatedCity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/6 17:52
 */
public class CityPicker {
    private static final String TAG = "CityPicker";

    private WeakReference<FragmentActivity> mContext;
    private WeakReference<Fragment> mFragment;
    private WeakReference<FragmentManager> mFragmentManager;

    private boolean enableAnim;
    private int mAnimStyle;
    private LocatedCity mLocation;
    private List<HotCity> mHotCities;
    private OnPickListener mOnPickListener;
    private CityPickerConfig cityPickerConfig;
    private ArrayList<City> custom_listdata;
    private ArrayList<HotCity> custom_hot_listdata;

    private CityPicker() {
    }

    private CityPicker(Fragment fragment) {
        this(fragment.getActivity(), fragment);
        mFragmentManager = new WeakReference<>(fragment.getChildFragmentManager());
    }

    private CityPicker(FragmentActivity activity) {
        this(activity, null);
        mFragmentManager = new WeakReference<>(activity.getSupportFragmentManager());
    }

    private CityPicker(FragmentActivity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static CityPicker from(Fragment fragment) {
        return new CityPicker(fragment);
    }

    public static CityPicker from(FragmentActivity activity) {
        return new CityPicker(activity);
    }

    /**
     * 设置动画效果
     *
     * @param animStyle
     * @return
     */
    public CityPicker setAnimationStyle(@StyleRes int animStyle) {
        this.mAnimStyle = animStyle;
        return this;
    }

    /**
     * 设置当前已经定位的城市
     *
     * @param location
     * @return
     */
    public CityPicker setLocatedCity(LocatedCity location) {
        this.mLocation = location;
        return this;
    }

    public CityPicker setHotCities(List<HotCity> data) {
        this.mHotCities = data;
        return this;
    }

    /**
     * 自定义配置文件
     *
     * @param config
     * @return
     */
    public CityPicker setConfig(CityPickerConfig config) {
        this.cityPickerConfig = config;
        return this;
    }

    /**
     * 自定义数据
     * @param listdata
     * @return
     */
    public CityPicker setCustomData(ArrayList<City> listdata) {
        this.custom_listdata = listdata;
        return this;
    }

    /**
     * 自定义热门数据
     * @param listdata
     * @return
     */
    public CityPicker setCustomHotData(ArrayList<HotCity> listdata) {
        this.custom_hot_listdata = listdata;
        return this;
    }


    /**
     * 启用动画效果，默认为false
     *
     * @param enable
     * @return
     */
    public CityPicker enableAnimation(boolean enable) {
        this.enableAnim = enable;
        return this;
    }

    /**
     * 设置选择结果的监听器
     *
     * @param listener
     * @return
     */
    public CityPicker setOnPickListener(OnPickListener listener) {
        this.mOnPickListener = listener;
        return this;
    }

    public void show() {
        FragmentTransaction ft = mFragmentManager.get().beginTransaction();
        final Fragment prev = mFragmentManager.get().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev).commit();
            ft = mFragmentManager.get().beginTransaction();
        }
        ft.addToBackStack(null);
        final CityPickerDialogFragment cityPickerFragment =
                CityPickerDialogFragment.newInstance(enableAnim, cityPickerConfig);
        cityPickerFragment.setLocatedCity(mLocation);
        cityPickerFragment.setHotCities(mHotCities);
        cityPickerFragment.setAnimationStyle(mAnimStyle);
        cityPickerFragment.setOnPickListener(mOnPickListener);
        if (cityPickerConfig.isUseCustomData()){
            cityPickerFragment.setCustomData(custom_listdata);
        }
        if (cityPickerConfig.isUseCustomHotData()){
            cityPickerFragment.setCustomHotData(custom_hot_listdata);
        }
        cityPickerFragment.show(ft, TAG);
    }

    /**
     * 定位完成
     *
     * @param location
     * @param state
     */
    public void locateComplete(LocatedCity location, @LocateState.State int state) {
        CityPickerDialogFragment fragment = (CityPickerDialogFragment) mFragmentManager.get().findFragmentByTag(TAG);
        if (fragment != null) {
            fragment.locationChanged(location, state);
        }
    }
}
