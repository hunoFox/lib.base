package com.hunofox.baseFramework.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import com.hunofox.baseFramework.R;

import java.lang.ref.WeakReference;

/**
 * 项目名称：只带一个"我知道了"按钮的Dialog,该Dialog点击我知道了会消失
 * 项目作者：胡玉君
 * 创建日期：2017/8/25 9:35.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class HintDialog extends AlertDialog {

    private final WeakReference<Context> reference;
    private final TextView title;
    private final TextView content;
    private final TextView confirm;
    private final View view;

    private HintDialog(@NonNull Context context) {
        this(context, R.style.Dialog);
    }

    private HintDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);

        reference = new WeakReference<>(context);

        view = View.inflate(context, R.layout.dialog_hint, null);
        title = view.findViewById(R.id.tv_title);
        content = view.findViewById(R.id.tv_content);
        confirm = view.findViewById(R.id.tv_confirm);

        //动画效果
        if(getWindow() != null){
            getWindow().setWindowAnimations(R.style.DialogAnimStyle);
        }

        title.setText(R.string.title);
        confirm.setText(R.string.know);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HintDialog.this.dismiss();
            }
        });
    }

    /**
     * 展示弹框
     *
     * @param context   不为null，且必须是Activity的Context
     * @param msg       提示内容
     * @param listener  可传入回调函数编辑提示框内的文字及颜色，若传入null则按照默认文字及点击事件处理
     * @return          dialog实例
     */
    public static HintDialog showDialog(@NonNull Context context, String msg, OnDialogListener listener){
        final HintDialog dialog = new HintDialog(context);

        dialog.content.setText(msg);
        if(listener != null){
            listener.listener(dialog, dialog.title, dialog.confirm);
        }
        dialog.show();

        Window window = dialog.getWindow();
        if(window != null){
            WindowManager.LayoutParams params = window.getAttributes();
            WindowManager manager = (WindowManager) dialog.reference.get().getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            manager.getDefaultDisplay().getSize(point);
            params.width = (int) (point.x * 0.7f);
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }

        dialog.setContentView(dialog.view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    /**
     * 将Dialog展示出来
     *
     * @param msg 内容
     */
    public void show(String msg){
        show(msg, null);
    }

    /**
     * 将Dialog展示出来
     *
     * @param msg       内容
     * @param listener  可配置弹框内容的回调，传入null则按照默认点击事件处理
     */
    public void show(String msg, OnDialogListener listener){
        content.setText(msg);
        if(listener != null){
            listener.listener(this, title, confirm);
        }

        show();
    }

    @Override
    public void dismiss() {
        super.dismiss();

        title.setText(R.string.title);
        confirm.setText(R.string.know);
        title.setOnClickListener(null);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HintDialog.this.dismiss();
            }
        });
    }

    /**
     * 释放资源
     */
    public void release(){
        reference.clear();
    }

    public interface OnDialogListener{
        void listener(final HintDialog dialog, final TextView title, final TextView confirm);
    }


}
