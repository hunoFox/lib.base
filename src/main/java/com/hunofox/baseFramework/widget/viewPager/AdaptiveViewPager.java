package com.hunofox.baseFramework.widget.viewPager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 项目名称：根据不同条目的高度，自适应高度的ViewPager
 * 项目作者：胡玉君
 * 创建日期：2017/9/13 17:51.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：嵌套在NestedScrollerView中的ViewPager
 * ----------------------------------------------------------------------------------------------------
 */

public class AdaptiveViewPager extends ViewPager {

    private int current;
    private int height = 0;

    /**
     * 保存position与对于的View
     */
    private HashMap<Integer, View> mChildrenViews = new LinkedHashMap<>();

    public AdaptiveViewPager(Context context) {
        this(context, null);
    }

    public AdaptiveViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetHeight(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        resetHeight(0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mChildrenViews.size() > current) {
            View child = mChildrenViews.get(current);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            height = child.getMeasuredHeight();
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void resetHeight(int current) {
        this.current = current;
        if (mChildrenViews.size() > current) {
            if(getLayoutParams() instanceof LinearLayout.LayoutParams){
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
                if (layoutParams == null) {
                    layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                } else {
                    layoutParams.height = height;
                }
                setLayoutParams(layoutParams);
            }else {
                ViewGroup.LayoutParams params = getLayoutParams();
                if(params != null){
                    params.height = height;
                    setLayoutParams(params);
                }
            }
        }
    }


    /**
     * 保存position与对于的View
     */
    public void setObjectForPosition(View view, int position) {
        mChildrenViews.put(position, view);
    }
}
