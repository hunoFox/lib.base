package com.hunofox.baseFramework.widget.selectWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hunofox.baseFramework.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * 项目名称：WxApproval
 * 项目作者：胡玉君
 * 创建日期：2017/7/28 16:50.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class SelectWindow extends PopupWindow {

    private final List<String> datas;
    private final SelectWindowAdaptor adaptor;
    private final View view;
    private final WeakReference<View> anchor;

    private Animation startAnim;
    private Animation dismissAnim;

    public SelectWindow(View anchorView, final List<String> datas) {
        super(anchorView.getContext());

        anchor = new WeakReference<>(anchorView);

        //设置数据
        if(datas == null){
            this.datas = new ArrayList<>();
        }else{
            this.datas = datas;
        }

        view = View.inflate(anchorView.getContext(), R.layout.widget_selector_window, null);
        this.setContentView(view);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(anchorView.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        adaptor = new SelectWindowAdaptor(this.datas);
        recyclerView.setAdapter(adaptor);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //设置宽高
        setPopupHeight();

        //设置动画效果
        dismissAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1);
        dismissAnim.setDuration(250);
        dismissAnim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
                SelectWindow.super.dismiss();
            }
            public void onAnimationRepeat(Animation animation) {}
        });
        startAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0);
        startAnim.setDuration(250);

        //设置点击空白区域消失
        setFocusable(true);
        setOutsideTouchable(true);
    }

    private void setPopupHeight() {
        WindowManager wm = (WindowManager) anchor.get().getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        float childHeight = (datas.size() * (dip2px(24) + sp2px(16) + 1) + dip2px(24) + sp2px(18) + 1);
        if(childHeight > size.y * (5f/8f)){
            this.setWidth(size.x);
            this.setHeight((int) (size.y * (5f/8f)));
        }else{
            this.setHeight((int) childHeight);
            this.setWidth(size.x);
        }
    }
    private float dip2px(float dip){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getContentView().getContext().getApplicationContext().getResources().getDisplayMetrics());
    }
    public float sp2px(float spValue) {
        final float fontScale = getContentView().getContext().getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return spValue * fontScale + 0.5f;
    }

    //展示popupWindow
    public void show() {
        setPopupHeight();

        showAtLocation(anchor.get(), Gravity.START + Gravity.BOTTOM, 0, -100);
        getContentView().startAnimation(startAnim);
        setBackgroundAlpha(0.5f);

        InputMethodManager imm =  (InputMethodManager)anchor.get().getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && anchor.get().getContext() instanceof Activity){
            imm.hideSoftInputFromWindow(((Activity)anchor.get().getContext()).getWindow().getDecorView().getWindowToken(),0);
        }
    }

    //popupWindow消失
    public void dismiss() {
        setBackgroundAlpha(1.0f);
        getContentView().startAnimation(dismissAnim);
    }

    //通知数据发生了改变
    public void notifyDataSetChanged(List<String> datas){
        this.datas.clear();
        this.datas.addAll(datas);
        adaptor.notifyDataSetChanged();
    }
    public void notifyDataSetChanged(){
        adaptor.notifyDataSetChanged();
    }

    //点击事件
    public void setOnSelectListener(final OnSelectListener listener){
        adaptor.setOnItemClickListener(new SelectWindowAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(listener != null){
                    listener.onSelectListener(datas.get(position), position);
                }
            }
        });
    }
    public interface OnSelectListener{
        void onSelectListener(String str, int postion);
    }

    //自定义展示、消失的动画
    public void setAnimation(Animation startAnim, Animation dismissAnim){
        this.startAnim = startAnim;
        this.dismissAnim = dismissAnim;
    }

    /** 设置标题 */
    public void setTitle(String title){
        ((TextView)getContentView().findViewById(R.id.tv_title)).setText(title);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     * 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        if(anchor.get() != null && anchor.get().getContext() instanceof Activity){
            Activity activity = (Activity) anchor.get().getContext();
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = bgAlpha;
            activity.getWindow().setAttributes(lp);
        }
    }

    /**
     * 释放资源
     */
    public void release(){
        this.datas.clear();
        anchor.clear();
    }
}
