package com.zhuxu.citypicker;

import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

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
    /**
     * 热门城市
     */
    public static String FLAG_HOT = "FLAG_HOT";
    /**
     * 定位信息
     */
    public static String FLAG_LOCATION = "FLAG_LOCATION";
    /**
     * 列表
     */
    public static String FLAG_LIST = "FLAG_LIST";

    private static final String TAG = "CityPicker";

    private WeakReference<FragmentActivity> mContext;
    private WeakReference<Fragment> mFragment;
    private WeakReference<FragmentManager> mFragmentManager;

    private boolean enableAnim;
    private int mAnimStyle;
    private LocatedCity mLocation;
    private List<HotCity> mHotCities;
    private List<HotCity> mCustomModelData;
    private OnPickListener mOnPickListener;
    // 新增的参数
    private CityPickerConfig cityPickerConfig = new CityPickerConfig();
    private ArrayList<City> custom_listdata;

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
        cityPickerConfig = new CityPickerConfig();
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
     * 设置是否使用自定义数据模块
     *
     * @param enable           是否开启自定义数据模块
     * @param title            自定义数据模块的标题
     * @param mCustomModelData 自定义数据模块的列表数据
     * @return
     */
    public CityPicker setCustomModel(boolean enable, String title, List<HotCity> mCustomModelData) {
        cityPickerConfig.setUseCustomModel(enable);
        if (!enable) {
            return this;
        }
        this.mCustomModelData = mCustomModelData;
        cityPickerConfig.setStrCustomModelTitle(TextUtils.isEmpty(title) ? "自定义模块" : title);
        return this;
    }

    public CityPicker setCustomModel(boolean enable) {
        cityPickerConfig.setUseCustomModel(enable);
        return this;
    }

    /**
     * 设置自定义模块内容
     *
     * @param location 定位信息
     * @return
     */
    public CityPicker setLocatedCity(boolean enable, LocatedCity location) {
        cityPickerConfig.setShowLocation(enable);
        if (!enable) {
            return this;
        }
        this.mLocation = location;
        return this;
    }

    /**
     * 设置热门城市信息
     *
     * @param enable 是否启用热门城市
     * @param data   热门城市数据,如需使用默认则设为null
     * @return
     */
    public CityPicker setHotCities(boolean enable, List<HotCity> data) {
        cityPickerConfig.setShowHotCities(enable);
        if (!enable) {
            return this;
        }
        this.mHotCities = data;
        return this;
    }

    public CityPicker setHotCities(boolean enable) {
        cityPickerConfig.setShowHotCities(enable);
        return this;
    }

    /**
     * 设置热门城市部分内容
     *
     * @param title   设置“热门城市”标题
     * @param iconTxt 设置“热门城市”模块下的标记内容
     * @return
     */
    public CityPicker setHotModel(String title, String iconTxt) {
        if (title == null || TextUtils.isEmpty(title)) {
            cityPickerConfig.setStrHotCities("热门城市");
        } else {
            cityPickerConfig.setStrHotCities(title);
        }
        if (iconTxt == null || TextUtils.isEmpty(iconTxt)) {
            cityPickerConfig.setStrHotCitiesIcon("热");
        } else {
            cityPickerConfig.setStrHotCitiesIcon(iconTxt);
        }
        return this;
    }

    /**
     * 自定义配置文件
     *
     * @param config
     * @return
     */
    public CityPicker setConfig(boolean enable, CityPickerConfig config) {
        this.cityPickerConfig = config;
        return this;
    }

    /**
     * 自定义数据
     *
     * @param enable   是否启用自定义数据，启用自定义数据将会代替现有数据库数据
     * @param listdata 自定义数据
     * @return
     */
    public CityPicker setCustomData(boolean enable, ArrayList<City> listdata) {
        cityPickerConfig.setUseCustomData(enable);
        if (!enable) {
            return this;
        }
        this.custom_listdata = listdata;
        return this;
    }

    public CityPicker setCustomData(boolean enable) {
        cityPickerConfig.setUseCustomData(enable);
        return this;
    }

    /**
     * 启用动画效果，默认为false
     *
     * @param enable    是否开启动画
     * @param animStyle 动画效果 如果enable为true且animStyle为0，则自动加载默认动画
     * @return
     */
    public CityPicker setAnimation(boolean enable, int animStyle) {
        this.enableAnim = enable;
        if (!enable) {
            return this;
        }
        if (enableAnim && animStyle == 0) {
            setAnimationStyle(R.style.DefaultCityPickerAnimation);
        } else {
            setAnimationStyle(animStyle);
        }
        return this;
    }

    public CityPicker setAnimation(boolean enable) {
        this.enableAnim = enable;
        if (enableAnim) {
            setAnimationStyle(R.style.DefaultCityPickerAnimation);
        }
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
        cityPickerFragment.setCustomModelList(mCustomModelData);
        cityPickerFragment.setAnimationStyle(mAnimStyle);
        cityPickerFragment.setIconTxt(cityPickerConfig.getStrHotCitiesIcon());
        cityPickerFragment.setOnPickListener(mOnPickListener);
        cityPickerFragment.searchInterface(searchActionInterface);
        if (cityPickerConfig.isUseCustomData()) {
            cityPickerFragment.setCustomData(custom_listdata);
        }
//        if (cityPickerConfig.isUseCustomHotData()) {
//            cityPickerFragment.setCustomHotData(custom_hot_listdata);
//        }
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

    SearchActionInterface searchActionInterface;

    /**
     * 设置搜索回调
     */
    public CityPicker searchInterface(SearchActionInterface _searchActionInterface) {
        Log.e("zhuxu", " set searchInterface is 1 " + (_searchActionInterface == null));
        searchActionInterface = _searchActionInterface;
        return this;
    }

    /**
     * 更新数据
     */
    public void updateResult(List<City> _mResults) {
        CityPickerDialogFragment fragment = (CityPickerDialogFragment) mFragmentManager.get().findFragmentByTag(TAG);
        if (fragment != null) {
            fragment.updateResult(_mResults);
        }
    }
}
