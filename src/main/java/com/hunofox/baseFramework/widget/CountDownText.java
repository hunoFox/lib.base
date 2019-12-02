package com.hunofox.baseFramework.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.hunofox.baseFramework.R;

/**
 * 项目名称：TongTong
 * 项目作者：胡玉君
 * 创建日期：2019/8/20 0020 16:34.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 *
 * 倒计时按钮
 * ----------------------------------------------------------------------------------------------------
 */
public class CountDownText extends TextView {

    private int mClickedBackground;//点击后背景
    private int mBackground;//当前背景
    private CharSequence mText;
    private int mCountdownTime = 60;
    private TimeCount mTimeCount;

    private int currentState = 0;//0闲置状态 1倒计时状态 -1不可点击状态

    public CountDownText(Context context) {
        this(context, null);
    }

    public CountDownText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textStyle);
    }

    public CountDownText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.count_down_timer);
        mBackground = typedArray.getResourceId(R.styleable.count_down_timer_android_background, mBackground);
        mClickedBackground = typedArray.getResourceId(R.styleable.count_down_timer_clickedBackground, mClickedBackground);
        mCountdownTime = typedArray.getInt(R.styleable.count_down_timer_countdownTime, mCountdownTime);
        mText = getText();
        typedArray.recycle();

        setBackgroundResource(mBackground);
        mTimeCount = new TimeCount(mCountdownTime * 1000, 1000);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }

    public void start(){
        if(listener == null || listener.beforeStart()){
            mTimeCount.start();
            if(listener != null){
                listener.onStart();
            }
        }
    }
    public void cancel(){
        mTimeCount.cancel();
        if(listener != null){
            listener.onCancel();
        }
    }
    public void setEnable(boolean isEnable){
        if(isEnable){
            //不可用时才调用
            if(currentState == -1){
                currentState = 0;
                setClickable(true);
                setEnabled(true);
                setBackgroundResource(mBackground);
            }
        }else{
            if(currentState != -1){
                if(currentState == 1){
                    cancel();
                }
                currentState = -1;
                setClickable(false);
                setEnabled(false);
                setBackgroundResource(mClickedBackground);
            }
        }
    }

    private CountDownListener listener;
    public void setOnCountDownListener(CountDownListener listener){
        this.listener = listener;
    }


    class TimeCount extends CountDownTimer {

        /**
         * @param millisInFuture  总时间
         * @param countDownInterval 间隔时间
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * @param millisUntilFinished 当前时间
         */
        @Override
        public void onTick(long millisUntilFinished) {
            currentState = 1;
            setClickable(false);
            setEnabled(false);
            setText(millisUntilFinished / 1000 + "s后重发");
            setBackgroundResource(mClickedBackground);
        }

        @Override
        public void onFinish() {
            currentState = 0;
            setClickable(true);
            setEnabled(true);
            setText(mText != null ? mText : "");
            setBackgroundResource(mBackground);
            if(listener != null){
                listener.onFinish();
            }
        }
    }

    public interface CountDownListener{

        //返回true才能开始计时，并调用onStart
        boolean beforeStart();

        void onStart();

        void onFinish();

        void onCancel();
    }
}
