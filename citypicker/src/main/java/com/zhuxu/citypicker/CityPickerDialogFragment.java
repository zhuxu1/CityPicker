package com.zhuxu.citypicker;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.zhuxu.citypicker.adapter.CityListAdapter;
import com.zhuxu.citypicker.adapter.InnerListener;
import com.zhuxu.citypicker.adapter.OnPickListener;
import com.zhuxu.citypicker.adapter.decoration.DividerItemDecoration;
import com.zhuxu.citypicker.adapter.decoration.SectionItemDecoration;
import com.zhuxu.citypicker.db.DBManager;
import com.zhuxu.citypicker.model.City;
import com.zhuxu.citypicker.model.CityPickerConfig;
import com.zhuxu.citypicker.model.HotCity;
import com.zhuxu.citypicker.model.LocateState;
import com.zhuxu.citypicker.model.LocatedCity;
import com.zhuxu.citypicker.util.ChineseSortUtil;
import com.zhuxu.citypicker.util.ScreenUtil;
import com.zhuxu.citypicker.view.SideIndexBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/6 20:50
 */
public class CityPickerDialogFragment extends DialogFragment implements TextWatcher,
        View.OnClickListener, SideIndexBar.OnIndexTouchedChangedListener, InnerListener {
    private View mContentView;
    private RecyclerView mRecyclerView;
    private View mEmptyView;
    private TextView mOverlayTextView;
    private SideIndexBar mIndexBar;
    private EditText mSearchBox;
    private TextView mCancelBtn;
    private ImageView mClearAllBtn;

    private LinearLayoutManager mLayoutManager;
    private CityListAdapter mAdapter;
    private List<City> mAllCities;
    private List<HotCity> mHotCities;
    private List<City> mResults;
    private List<City> mList_custom;
    private List<HotCity> mListHot_custom;


    private DBManager dbManager;

    private int height;
    private int width;

    private boolean enableAnim = false;
    private int mAnimStyle = R.style.DefaultCityPickerAnimation;
    private LocatedCity mLocatedCity;
    private int locateState;
    private OnPickListener mOnPickListener;

    public static String CITY_PICKER_DIALOG_CONFIG_FLAG = "dialog_config";

    /**
     * 获取实例
     *
     * @param enable 是否启用动画效果
     * @return
     */
    public static CityPickerDialogFragment newInstance(boolean enable, CityPickerConfig config) {
        final CityPickerDialogFragment fragment = new CityPickerDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("cp_enable_anim", enable);
        args.putSerializable(CITY_PICKER_DIALOG_CONFIG_FLAG, config);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CityPickerStyle);
    }

    public void setLocatedCity(LocatedCity location) {
        mLocatedCity = location;
    }

    public void setHotCities(List<HotCity> data) {
        if (data != null && !data.isEmpty()) {
            this.mHotCities = data;
        }
    }

    @SuppressLint("ResourceType")
    public void setAnimationStyle(@StyleRes int resId) {
        this.mAnimStyle = resId <= 0 ? mAnimStyle : resId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.cp_dialog_city_picker, container, false);
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initViews();
    }

    private void initViews() {
        mRecyclerView = mContentView.findViewById(R.id.cp_city_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(getActivity(), mAllCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), mAllCities), 1);
        mAdapter = new CityListAdapter(getActivity(), mAllCities, mHotCities, locateState);
        mAdapter.autoLocate(true);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        if (config.hasSetStrHotCities() && mAdapter != null) {
            mAdapter.setConfig(config);
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.refreshLocationItem();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });

        mEmptyView = mContentView.findViewById(R.id.cp_empty_view);
        mOverlayTextView = mContentView.findViewById(R.id.cp_overlay);

        mIndexBar = mContentView.findViewById(R.id.cp_side_index_bar);
        mIndexBar.setNavigationBarHeight(ScreenUtil.getNavigationBarHeight(getActivity()));
        mIndexBar.setOverlayTextView(mOverlayTextView)
                .setOnIndexChangedListener(this);

        mSearchBox = mContentView.findViewById(R.id.cp_search_box);
        mSearchBox.addTextChangedListener(this);

        mCancelBtn = mContentView.findViewById(R.id.cp_cancel);
        mClearAllBtn = mContentView.findViewById(R.id.cp_clear_all);
        mCancelBtn.setOnClickListener(this);
        mClearAllBtn.setOnClickListener(this);
    }

    CityPickerConfig config;

    private void initData() {
        Bundle args = getArguments();
        Log.e("zhuxu","1=================================");
        if (args != null) {
            enableAnim = args.getBoolean("cp_enable_anim");
            config = (CityPickerConfig) args.getSerializable(CITY_PICKER_DIALOG_CONFIG_FLAG);
        }
        Log.e("zhuxu","2=================================");
        //初始化热门城市
        if (mHotCities == null || mHotCities.isEmpty()) {
            // 是否使用自定义热门城市
            if (config.isUseCustomHotData()) {
                mHotCities = mListHot_custom;
            } else {
                mHotCities = new ArrayList<>();
                mHotCities.add(new HotCity("北京", "北京", "101010100"));
                mHotCities.add(new HotCity("上海", "上海", "101020100"));
                mHotCities.add(new HotCity("广州", "广东", "101280101"));
                mHotCities.add(new HotCity("深圳", "广东", "101280601"));
                mHotCities.add(new HotCity("天津", "天津", "101030100"));
                mHotCities.add(new HotCity("杭州", "浙江", "101210101"));
                mHotCities.add(new HotCity("南京", "江苏", "101190101"));
                mHotCities.add(new HotCity("成都", "四川", "101270101"));
                mHotCities.add(new HotCity("武汉", "湖北", "101200101"));
            }
        }
        Log.e("zhuxu","3=================================");
        //初始化定位城市，默认为空时会自动回调定位
        if (mLocatedCity == null) {
            mLocatedCity = new LocatedCity(getString(R.string.cp_locating), "未知", "0");
            locateState = LocateState.LOCATING;
        } else {
            locateState = LocateState.SUCCESS;
        }
        Log.e("zhuxu","4=================================");

        // 判断是否使用自定义数据
        if (!config.isUseCustomData()) {
            dbManager = new DBManager(getActivity());
            mAllCities = dbManager.getAllCities();
        } else {
            mAllCities = mList_custom;
        }
        Log.e("zhuxu","5=================================");

        // 初始化拼音数据
        initPinyinData(mAllCities);

        // 是否显示热门
        if (config.isShowHotCities()) {
            HotCity hotCity = new HotCity(config.hasSetStrHotCities() ? config.getStrHotCities() : "热门城市", "未知", "0");
            hotCity.setHot();
            mAllCities.add(0, hotCity);
        }
        Log.e("zhuxu","6=================================");

        // 是否显示定位
        if (config.isShowLocation()) {
            mAllCities.add(0, mLocatedCity);
        }
        Log.e("zhuxu","7=================================");

        mResults = mAllCities;
    }

    private void initPinyinData(List<City> list) {
        for (City city : list) {
            if (TextUtils.isEmpty(city.getPinyin())) {
                // 自动获取首字母
                city.setPinyin(Pinyin.toPinyin(city.getName().charAt(0)));
            }
        }
        // 排序
        ChineseSortUtil.sortList(list);
        mAllCities = list;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mOnPickListener != null) {
                        mOnPickListener.onCancel();
                    }
                }
                return false;
            }
        });

        measure();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(width, height - ScreenUtil.getStatusBarHeight(getActivity()));
            if (enableAnim) {
                window.setWindowAnimations(mAnimStyle);
            }
        }
    }

    //测量宽高
    private void measure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            height = dm.heightPixels;
            width = dm.widthPixels;
        } else {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            height = dm.heightPixels;
            width = dm.widthPixels;
        }
    }

    /**
     * 搜索框监听
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mResults = mAllCities;
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            mAdapter.updateData(mResults);
        } else {
            mClearAllBtn.setVisibility(View.VISIBLE);
            //开始数据库查找
            mResults = dbManager.searchCity(keyword);
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            if (mResults == null || mResults.isEmpty()) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mAdapter.updateData(mResults);
            }
        }
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cp_cancel) {
            dismiss();
            if (mOnPickListener != null) {
                mOnPickListener.onCancel();
            }
        } else if (id == R.id.cp_clear_all) {
            mSearchBox.setText("");
        }
    }


    @Override
    public void onIndexChanged(String index, int position) {
        //滚动RecyclerView到索引位置
        mAdapter.scrollToSection(index);
    }

    public void locationChanged(LocatedCity location, int state) {
        mAdapter.updateLocateState(location, state);
    }

    /**
     * 设置自定义数据
     *
     * @param list
     */
    public void setCustomData(ArrayList<City> list) {
        this.mList_custom = list;
    }

    /**
     * 设置自定义热门数据
     *
     * @param list
     */
    public void setCustomHotData(ArrayList<HotCity> list) {
        this.mListHot_custom = list;
    }

    @Override
    public void dismiss(int position, City data) {
        dismiss();
        if (mOnPickListener != null) {
            mOnPickListener.onPick(position, data);
        }
    }

    @Override
    public void locate() {
        if (mOnPickListener != null) {
            mOnPickListener.onLocate();
        }
    }

    public void setOnPickListener(OnPickListener listener) {
        this.mOnPickListener = listener;
    }
}
