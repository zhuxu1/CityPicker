package com.zhuxu.citypicker.adapter.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.zhuxu.citypicker.R;
import com.zhuxu.citypicker.model.City;

import java.util.List;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private float dividerHeight;
    private float dividerHeight_big;
    private Paint mPaint;
    private Paint mPaint_big;
    private List<City> mData;

    private int mBgColor;

    public DividerItemDecoration(Context context, List<City> data) {
        this.mData = data;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_big = new Paint(Paint.ANTI_ALIAS_FLAG);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.cpSectionSpacingBackground, typedValue, true);
        mPaint.setColor(context.getResources().getColor(typedValue.resourceId));

        TypedValue typedValue_deep = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.cpSectionSpacingDeepBackground, typedValue_deep, true);
        mPaint_big.setColor(context.getResources().getColor(typedValue_deep.resourceId));
//        context.getTheme().resolveAttribute(R.attr.cpSectionBackground, typedValue, true);
//        mBgColor = context.getResources().getColor(typedValue.resourceId);
//        mPaint_big.setColor(mBgColor);


        dividerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.getResources().getDisplayMetrics());
        dividerHeight_big = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = (int) dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight() - 120;

        for (int i = 0; i < childCount - 1; i++) {
            String section = mData.get(i).getSection();
            View view = parent.getChildAt(i);
            if (TextUtils.isEmpty((CharSequence) view.getTag())) {
                view.setTag(section);
            }

            if (view.getTag().equals("定位城市")) {

            } else if (view.getTag().equals("热门城市")) {
                float top = view.getBottom();
                float bottom = view.getBottom() + dividerHeight_big;
                c.drawRect(left, top, right + 120, bottom, mPaint_big);
            } else {
                float top = view.getBottom();
                float bottom = view.getBottom() + dividerHeight;
                c.drawRect(left + 40, top, right, bottom, mPaint);
            }
//            Log.e("zhuxu", "section is : " + i + ":" + section);
//            if (section.equals("定位城市")) {
//            } else if (section.equals("热门城市")) {
////                float top = view.getBottom();
////                float bottom = view.getBottom() + dividerHeight_big;
////                c.drawRect(left + 40, top, right, bottom, mPaint_big);
//            } else {
//                float top = view.getBottom();
//                float bottom = view.getBottom() + dividerHeight;
//                c.drawRect(left, top, right, bottom, mPaint);
//            }
        }
    }
}
