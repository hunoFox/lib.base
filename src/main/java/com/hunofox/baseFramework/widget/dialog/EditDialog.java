package com.hunofox.baseFramework.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;


import com.hunofox.baseFramework.R;

import java.lang.ref.WeakReference;

/**
 * 项目名称：带EditText文本框的dialog
 * 项目作者：胡玉君
 * 创建日期：2017/8/25 9:35.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 *
 * 如果不设置左右两个按钮的点击事件，则默认：点击弹框消失
 * ----------------------------------------------------------------------------------------------------
 */
public class EditDialog extends AlertDialog {

    private final WeakReference<Context> reference;
    private final TextView title;
    private final EditText content;
    private final TextView confirm;
    private final TextView cancel;
    private final View view;

    private EditDialog(@NonNull Context context) {
        this(context, R.style.Dialog);
    }

    private EditDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);

        reference = new WeakReference<>(context);

        view = View.inflate(context, R.layout.dialog_edit, null);
        title = (TextView) view.findViewById(R.id.tv_title);
        content = (EditText) view.findViewById(R.id.tv_content);
        confirm = (TextView) view.findViewById(R.id.tv_confirm);
        cancel = (TextView) view.findViewById(R.id.tv_cancel);

        //动画效果
        getWindow().setWindowAnimations(R.style.DialogAnimStyle);

        title.setText(R.string.title);
        confirm.setText(R.string.confirm);
        cancel.setText(R.string.cancel);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    /**
     * 展示弹框
     *
     * @param context   不为null，且必须是Activity的Context
     * @param hintMsg       提示内容
     * @param listener  可传入回调函数编辑提示框内的文字及颜色，若传入null则按照默认文字及点击事件处理
     * @return          dialog实例
     */
    public static EditDialog showDialog(@NonNull Context context, @Nullable String hintMsg, @NonNull OnDialogListener listener){
        final EditDialog dialog = new EditDialog(context);

        dialog.content.setHint(hintMsg==null?"":hintMsg);
        listener.listener(dialog, dialog.cancel, dialog.confirm, dialog.title, dialog.content);
        dialog.show();

        Window window = dialog.getWindow();
        if(window != null){
            WindowManager.LayoutParams params = window.getAttributes();
            WindowManager manager = (WindowManager) dialog.reference.get().getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            manager.getDefaultDisplay().getSize(point);
            params.width = (int) (point.x * 0.7f);
            window.setAttributes(params);
        }

        dialog.setContentView(dialog.view);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 将Dialog展示出来
     *
     * @param msg       提示内容
     * @param listener  可配置弹框内容的回调，传入null则按照默认点击事件处理
     */
    public void show(String msg, @NonNull OnDialogListener listener){
        content.setHint(msg==null?"":msg);
        listener.listener(this, cancel, confirm, title, content);
        show();
    }

    @Override
    public void dismiss() {
        super.dismiss();

        title.setText(R.string.title);
        confirm.setText(R.string.confirm);
        cancel.setText(R.string.cancel);
        title.setOnClickListener(null);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
        void listener(final EditDialog dialog, final TextView left, final TextView right, final TextView title, final EditText tvContent);
    }
}
