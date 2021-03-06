package com.zhuxu.citypicker.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhuxu.citypicker.R;
import com.zhuxu.citypicker.adapter.decoration.GridItemDecoration;
import com.zhuxu.citypicker.model.City;
import com.zhuxu.citypicker.model.CityPickerConfig;
import com.zhuxu.citypicker.model.HotCity;
import com.zhuxu.citypicker.model.LocateState;
import com.zhuxu.citypicker.model.LocatedCity;
import com.zhuxu.citypicker.view.AutoLinefeedLayout;

import java.util.List;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/5 12:06
 */
public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.BaseViewHolder> {
    private static final int VIEW_TYPE_LOCATION = 10;
    private static final int VIEW_TYPE_HOT = 11;
    private static final int VIEW_TYPE_CUSTOM = 12;

    private Context mContext;
    private List<City> mData;
    private List<HotCity> mHotData;
    // 自定义模块
    private List<HotCity> mCustomModelData;
    private int locateState;
    private InnerListener mInnerListener;
    private LinearLayoutManager mLayoutManager;
    private boolean stateChanged;
    private boolean autoLocate;

    public CityListAdapter(Context context, List<City> data, List<HotCity> hotData, List<HotCity> customData, int state) {
        this.mData = data;
        this.mContext = context;
        this.mHotData = hotData;
        this.mCustomModelData = customData;
        this.locateState = state;
    }

    private String iconTxt = "热";

    public void setIconTxt(String iconTxt) {
        this.iconTxt = iconTxt;
    }

    CityPickerConfig cityPickerConfig;

    public void setConfig(CityPickerConfig config) {
        cityPickerConfig = config;
        if (cityPickerConfig == null) {
            cityPickerConfig = new CityPickerConfig();
        }
    }

    public void autoLocate(boolean auto) {
        autoLocate = auto;
    }

    public void setLayoutManager(LinearLayoutManager manager) {
        this.mLayoutManager = manager;
    }

    public void updateData(List<City> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void updateLocateState(LocatedCity location, int state) {
        mData.remove(0);
        mData.add(0, location);
        stateChanged = !(locateState == state);
        locateState = state;
        refreshLocationItem();
    }

    public void refreshLocationItem() {
        //如果定位城市的item可见则进行刷新
        if (stateChanged && mLayoutManager.findFirstVisibleItemPosition() == 0) {
            stateChanged = false;
            notifyItemChanged(0);
        }
    }

    /**
     * 滚动RecyclerView到索引位置
     *
     * @param index
     */
    public void scrollToSection(String index) {
        if (mData == null || mData.isEmpty()) return;
        if (TextUtils.isEmpty(index)) return;
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(index.substring(0, 1), mData.get(i).getSection().substring(0, 1))) {
                if (mLayoutManager != null) {
                    mLayoutManager.scrollToPositionWithOffset(i, 0);
                    if (TextUtils.equals(index.substring(0, 1), "定")) {
                        //防止滚动时进行刷新
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (stateChanged) notifyItemChanged(0);
                            }
                        }, 1000);
                    }
                    return;
                }
            }
        }
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_LOCATION:
                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_location_layout, parent, false);
                return new LocationViewHolder(view);
            case VIEW_TYPE_HOT:
//                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_hot_layout, parent, false);
                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_hot_layout2, parent, false);
                return new HotViewHolder(view);
            case VIEW_TYPE_CUSTOM:
//                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_hot_layout, parent, false);
                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_hot_layout2, parent, false);
                return new CustomViewHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.cp_list_item_default_layout, parent, false);
                return new DefaultViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (holder instanceof DefaultViewHolder) {
            final int pos = holder.getAdapterPosition();
            final City data = mData.get(pos);
            if (data == null) return;
            ((DefaultViewHolder) holder).name.setText(data.getName());
            ((DefaultViewHolder) holder).name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mInnerListener != null) {
                        mInnerListener.dismiss(pos, data);
                    }
                }
            });
        }
        //定位城市
        if (holder instanceof LocationViewHolder) {
            final int pos = holder.getAdapterPosition();
            final City data = mData.get(pos);
            if (data == null) return;
            //设置宽高
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            TypedValue typedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.cpGridItemSpace, typedValue, true);
            int space = mContext.getResources().getDimensionPixelSize(R.dimen.cp_grid_item_space);
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.cp_default_padding);
            int indexBarWidth = mContext.getResources().getDimensionPixelSize(R.dimen.cp_index_bar_width);
            int itemWidth = 0;
            if (GridListAdapter.SPAN_COUNT > 4) {
                itemWidth = (screenWidth - padding - space * 2 - indexBarWidth) / 2;
            } else if (GridListAdapter.SPAN_COUNT < 2) {
                itemWidth = (screenWidth - padding - space * (GridListAdapter.SPAN_COUNT - 1) - indexBarWidth) / GridListAdapter.SPAN_COUNT;
            } else {
                itemWidth = (int) ((screenWidth - padding - space * (GridListAdapter.SPAN_COUNT - 1) - indexBarWidth) / GridListAdapter.SPAN_COUNT * 1.5);
            }
//            int itemWidth = (screenWidth - padding - space * (GridListAdapter.SPAN_COUNT - 1) - indexBarWidth) / GridListAdapter.SPAN_COUNT;
            ViewGroup.LayoutParams lp = ((LocationViewHolder) holder).container.getLayoutParams();
            lp.width = itemWidth;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            ((LocationViewHolder) holder).container.setLayoutParams(lp);
            ((LocationViewHolder) holder).type_loc.setVisibility(View.GONE);
            switch (locateState) {
                case LocateState.LOCATING:
                    ((LocationViewHolder) holder).current.setText(R.string.cp_locating);
                    break;
                case LocateState.SUCCESS:
                    ((LocationViewHolder) holder).current.setText(data.getName());
                    ((LocationViewHolder) holder).type_loc.setVisibility(View.VISIBLE);
                    break;
                case LocateState.FAILURE:
                    ((LocationViewHolder) holder).current.setText(R.string.cp_locate_failed);
                    break;
            }
            ((LocationViewHolder) holder).container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (locateState == LocateState.SUCCESS) {
                        if (mInnerListener != null) {
                            mInnerListener.dismiss(pos, data);
                        }
                    } else if (locateState == LocateState.FAILURE) {
                        locateState = LocateState.LOCATING;
                        notifyItemChanged(0);
                        if (mInnerListener != null) {
                            mInnerListener.locate();
                        }
                    }
                }
            });
            //第一次弹窗，如果未定位则自动定位
            if (autoLocate && locateState == LocateState.LOCATING && mInnerListener != null) {
                mInnerListener.locate();
                autoLocate = false;
            }
        }
        //热门城市
        if (holder instanceof HotViewHolder) {
            final int pos = holder.getAdapterPosition();
            final City data = mData.get(pos);
            if (data == null) return;
//            GridListAdapter mAdapter = new GridListAdapter(mContext, mHotData);
//            mAdapter.setInnerListener(mInnerListener);
//            mAdapter.setIconTxt(iconTxt);
//            ((HotViewHolder) holder).mRecyclerView.setAdapter(mAdapter);
            AutoLinefeedLayout autoLinefeedLayout = ((HotViewHolder) holder).autoLinefeedLayout;
            if (autoLinefeedLayout != null && autoLinefeedLayout.getChildCount() > 0){
                autoLinefeedLayout.removeAllViews();
            }
            for (HotCity hotCity : mHotData) {
                autoLinefeedLayout.addView(getHotChildView(hotCity));
            }
        }
        //自定义模块
        if (holder instanceof CustomViewHolder) {
            final int pos = holder.getAdapterPosition();
            final City data = mData.get(pos);
            if (data == null) return;
//            GridListAdapter mAdapter = new GridListAdapter(mContext, mCustomModelData);
//            mAdapter.setInnerListener(mInnerListener);
//            ((CustomViewHolder) holder).mRecyclerView.setAdapter(mAdapter);
            AutoLinefeedLayout autoLinefeedLayout = ((CustomViewHolder) holder).autoLinefeedLayout;
            if (autoLinefeedLayout != null && autoLinefeedLayout.getChildCount() > 0){
                autoLinefeedLayout.removeAllViews();
            }
            for (HotCity hotCity : mCustomModelData) {
                autoLinefeedLayout.addView(getCustomChildView(hotCity));
            }
        }
    }

    private View getHotChildView(final City city) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_hot_child, null);
        TextView childTv = view.findViewById(R.id.item_hot_child_tv);
        childTv.setText(city.getName());
        childTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInnerListener != null) {
                    mInnerListener.dismiss(0, city);
                }
            }
        });
        return view;
    }

    private View getCustomChildView(final City city) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cutom_child, null);
        TextView childTv = view.findViewById(R.id.item_hot_child_tv);
        childTv.setText(city.getName());
        childTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInnerListener != null) {
                    mInnerListener.dismiss(0, city);
                }
            }
        });
        return view;
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(cityPickerConfig == null ? "热门城市" : cityPickerConfig.getStrHotCities(), mData.get(position).getSection())) {
            return VIEW_TYPE_HOT;
        }
        if (TextUtils.equals("定位城市", mData.get(position).getSection())) {
            return VIEW_TYPE_LOCATION;
        }
        if (TextUtils.equals(cityPickerConfig.getStrCustomModelTitle(), mData.get(position).getSection())) {
            return VIEW_TYPE_CUSTOM;
        }
        return super.getItemViewType(position);
    }

    public void setInnerListener(InnerListener listener) {
        this.mInnerListener = listener;
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {
        BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class DefaultViewHolder extends BaseViewHolder {
        TextView name;

        DefaultViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cp_list_item_name);
        }
    }

//    public static class CustomViewHolder extends BaseViewHolder {
//        RecyclerView mRecyclerView;
//
//        CustomViewHolder(View itemView) {
//            super(itemView);
//            mRecyclerView = itemView.findViewById(R.id.cp_hot_list);
//            mRecyclerView.setHasFixedSize(true);
//            mRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(),
//                    GridListAdapter.SPAN_COUNT, LinearLayoutManager.VERTICAL, false));
//            int space = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.cp_grid_item_space);
//            mRecyclerView.addItemDecoration(new GridItemDecoration(GridListAdapter.SPAN_COUNT,
//                    space));
//        }
//    }

    public static class CustomViewHolder extends BaseViewHolder {
        AutoLinefeedLayout autoLinefeedLayout;

        CustomViewHolder(View itemView) {
            super(itemView);
            autoLinefeedLayout = itemView.findViewById(R.id.cp_hot_list_autoline);
        }
    }

//    public static class HotViewHolder extends BaseViewHolder {
//        RecyclerView mRecyclerView;
//
//        HotViewHolder(View itemView) {
//            super(itemView);
//            mRecyclerView = itemView.findViewById(R.id.cp_hot_list);
//            mRecyclerView.setHasFixedSize(true);
//            mRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(),
//                    GridListAdapter.SPAN_COUNT, LinearLayoutManager.VERTICAL, false));
//            int space = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.cp_grid_item_space);
//            mRecyclerView.addItemDecoration(new GridItemDecoration(GridListAdapter.SPAN_COUNT,
//                    space));
//        }
//    }

    public static class HotViewHolder extends BaseViewHolder {
        AutoLinefeedLayout autoLinefeedLayout;

        HotViewHolder(View itemView) {
            super(itemView);
            autoLinefeedLayout = itemView.findViewById(R.id.cp_hot_list_autoline);
        }
    }


    public static class LocationViewHolder extends BaseViewHolder {
        LinearLayout container;
        TextView current;
        ImageView type_loc;

        LocationViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.cp_list_item_location_layout);
            current = itemView.findViewById(R.id.cp_list_item_location);
            type_loc = itemView.findViewById(R.id.cp_gird_item_type_img);
        }
    }
}
