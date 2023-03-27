package com.hunofox.baseFramework.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.hunofox.baseFramework.R;

import java.lang.ref.WeakReference;

/**
 * 项目名称：kcwxApp
 * 项目作者：胡玉君
 * 创建日期：2017/8/25 13:42.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class HeaderBar extends RelativeLayout {

    private final WeakReference<Activity> reference;

    public final RelativeLayout rlContainer;
    public final TextView tvTitle;
    public final ImageView ivLeft;
    public final ImageView ivRight;
    public final TextView tvRight;

    public HeaderBar(Context context) {
        this(context, null);
    }

    public HeaderBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HeaderBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        reference = new WeakReference<>((Activity) context);

        LayoutInflater.from(context).inflate(R.layout.widget_header, this);

        rlContainer = findViewById(R.id.rl_container);
        tvTitle = findViewById(R.id.tv_title);
        ivLeft = findViewById(R.id.iv_left);
        ivRight = findViewById(R.id.iv_right);
        tvRight = findViewById(R.id.tv_right);

        ivLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.get().onBackPressed();
            }
        });
    }
}
