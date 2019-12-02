package com.hunofox.baseFramework.widget.contactView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

/**
 * 项目名称：TongTong
 * 项目作者：胡玉君
 * 创建日期：2019-09-11 14:48.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class LetterView extends LinearLayout {

    public LetterView(Context context) {
        this(context, null);
    }

    public LetterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        for(char i = 'A';i<='Z';i++){
            addView(createText(i+"", context, layoutParams));
        }
        addView(createText("#",context, layoutParams));

    }

    private TextView createText(final String str, Context context, LinearLayout.LayoutParams layoutParams){
        final TextView tv = new TextView(context);
        tv.setLayoutParams(layoutParams);
        tv.setGravity(Gravity.CENTER);
        tv.setClickable(true);
        tv.setText(str);
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击监听
                if(letterListener != null){
                    letterListener.onClick(str);
                }
            }
        });
        return tv;
    }

    private LetterListener letterListener;
    public void setLetterListener(LetterListener letterListener){
        this.letterListener = letterListener;
    }
    public interface LetterListener{
        void onClick(String text);
    }
}
