package com.zhuxu.citypickerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuxu.citypicker.CityPicker;
import com.zhuxu.citypicker.adapter.OnPickListener;
import com.zhuxu.citypicker.model.City;
import com.zhuxu.citypicker.model.HotCity;
import com.zhuxu.citypicker.model.LocateState;
import com.zhuxu.citypicker.model.LocatedCity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private TextView currentTV;
    private CheckBox hotCB;
    private CheckBox animCB;
    private CheckBox enableCB;
    private Button themeBtn;

    private static final String KEY = "current_theme";

    private List<HotCity> hotCities;
    private int anim;
    private int theme;
    private boolean enable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            theme = savedInstanceState.getInt(KEY);
            setTheme(theme > 0 ? theme : R.style.DefaultCityPickerTheme);
        }

        setContentView(R.layout.activity_main);

        currentTV = findViewById(R.id.tv_current);
        hotCB = findViewById(R.id.cb_hot);
        animCB = findViewById(R.id.cb_anim);
        enableCB = findViewById(R.id.cb_enable_anim);
        themeBtn = findViewById(R.id.btn_style);

        if (theme == R.style.DefaultCityPickerTheme) {
            themeBtn.setText("默认主题");
        } else if (theme == R.style.CustomTheme) {
            themeBtn.setText("自定义主题");
        }

        hotCB.setOnCheckedChangeListener(this);
        animCB.setOnCheckedChangeListener(this);
        enableCB.setOnCheckedChangeListener(this);

        themeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (themeBtn.getText().toString().startsWith("自定义")) {
                    themeBtn.setText("默认主题");
                    theme = R.style.DefaultCityPickerTheme;
                } else if (themeBtn.getText().toString().startsWith("默认")) {
                    themeBtn.setText("自定义主题");
                    theme = R.style.CustomTheme;
                }
                recreate();
            }
        });

        findViewById(R.id.btn_pick).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean showAnim = ((CheckBox) findViewById(R.id.cb_enable_anim)).isChecked();
                boolean showHot = ((CheckBox) findViewById(R.id.cb_hot_enable)).isChecked();
                boolean showLocation = ((CheckBox) findViewById(R.id.cb_location_enable)).isChecked();
                boolean showCustomList = ((CheckBox) findViewById(R.id.cb_ccity_enable)).isChecked();
                String strHotCityTitle = ((EditText) findViewById(R.id.edt_hot_title)).getText().toString();
                String strHotCityTitleIcon = ((EditText) findViewById(R.id.edt_hot_title_type)).getText().toString();

                boolean showCustomModel = ((CheckBox) findViewById(R.id.cb_zdy_enable)).isChecked();
                String strCustomModelTitle = ((EditText) findViewById(R.id.edt_zdy_title)).getText().toString();

                CityPicker.from(MainActivity.this)
                        // 设置动画及数据
                        .setAnimation(showAnim, anim)
                        // 设置定位及数据
                        .setLocatedCity(showLocation, new LocatedCity("北京", "北京", "110000"))
                        // 设置热门城市及数据
                        .setHotCities(showHot, null)
                        // 设置自定义列表数据
                        .setCustomData(showCustomList, getListData())
                        // 设置热门城市部分的显示
                        .setHotModel(strHotCityTitle, strHotCityTitleIcon)
                        .setCustomModel(showCustomModel, strCustomModelTitle, getCustomListData())
                        .setOnPickListener(new OnPickListener() {
                            @Override
                            public void onPick(int position, City data) {
                                currentTV.setText(String.format("当前城市：%s，%s", data.getName(), data.getCode()));
                                Toast.makeText(
                                        getApplicationContext(),
                                        String.format("点击的数据：%s，%s", data.getName(), data.getCode()),
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }

                            @Override
                            public void onCancel() {
                                Toast.makeText(getApplicationContext(), "取消选择", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onLocate() {
                                //开始定位，这里模拟一下定位
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        CityPicker.from(MainActivity.this).locateComplete(
                                                new LocatedCity("深圳", "广东", "101280601"), LocateState.SUCCESS);
                                    }
                                }, 3000);
                            }
                        })
                        .show();
            }
        });
    }

    private ArrayList<City> getListData() {
        ArrayList<City> listData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            listData.add(new City("测试" + i, "测试", "", i + ""));
        }
        return listData;
    }

    private ArrayList<HotCity> getCustomListData() {
        ArrayList<HotCity> listData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            HotCity hotCity = new HotCity("自定义" + i, "测试", i + "");
            hotCity.setHot(false);
            listData.add(hotCity);
        }
        return listData;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_hot:
                if (isChecked) {
                    hotCities = new ArrayList<>();
                    hotCities.add(new HotCity("北京", "北京", "101010100"));
                    hotCities.add(new HotCity("上海", "上海", "101020100"));
                    hotCities.add(new HotCity("广州", "广东", "101280101"));
                    hotCities.add(new HotCity("深圳", "广东", "101280601"));
                    hotCities.add(new HotCity("杭州", "浙江", "101210101"));
                } else {
                    hotCities = null;
                }
                break;
            case R.id.cb_anim:
                anim = isChecked ? R.style.CustomAnim : 0;
                break;
            case R.id.cb_enable_anim:
                enable = isChecked;
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY, theme);
    }
}
