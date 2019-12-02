package com.hunofox.baseFramework.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * simple and powerful Keyboard show/hidden listener,view {@android.R.id.content} and {@ViewTreeObserver.OnGlobalLayoutListener}
 * Created by yes.cpu@gmail.com 2016/7/13.
 */
public class KeyboardChangeListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private static final String TAG = "ListenerHandler";
    private View mContentView;
    private int mOriginHeight;
    private int mPreHeight;
    private int rootViewVisibleHeight;
    private KeyBoardListener mKeyBoardListen;

    private boolean isShowing = false;
    private boolean isFullScreen = false;

    public boolean isShowing() {
        return isShowing;
    }

    public interface KeyBoardListener {
        /**
         * call back
         * @param isShow true is show else hidden
         * @param keyboardHeight keyboard height
         */
        void onKeyboardChange(boolean isShow, int keyboardHeight);
    }

    public void setKeyBoardListener(KeyBoardListener keyBoardListen) {
        this.mKeyBoardListen = keyBoardListen;
    }

    public KeyboardChangeListener(Activity contextObj, boolean isFullScreen) {
        if (contextObj == null) {
            return;
        }
        this.isFullScreen = isFullScreen;
        if(isFullScreen){
            mContentView = contextObj.getWindow().getDecorView();
        }else{
            mContentView = findContentView(contextObj);
        }

        if (mContentView != null) {
            addContentTreeObserver();
        }
    }

    private View findContentView(Activity contextObj) {
        return contextObj.findViewById(android.R.id.content);
    }

    private void addContentTreeObserver() {
        mContentView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if(isFullScreen){
            //获取当前根视图在屏幕上显示的大小
            Rect r = new Rect();
            mContentView.getWindowVisibleDisplayFrame(r);
            int visibleHeight = r.height();

            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight;
                return;
            }
            //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
            if (rootViewVisibleHeight == visibleHeight) {
                return;
            }
            //根视图显示高度变小超过200，可以看作软键盘显示了
            if (rootViewVisibleHeight - visibleHeight > 200) {
                isShowing = true;
                if (mKeyBoardListen != null) {
                    mKeyBoardListen.onKeyboardChange(true, rootViewVisibleHeight - visibleHeight);
                }
                rootViewVisibleHeight = visibleHeight;
                return;
            }
            //根视图显示高度变大超过200，可以看作软键盘隐藏了
            if (visibleHeight - rootViewVisibleHeight > 200) {
                isShowing = false;
                if (mKeyBoardListen != null) {
                    mKeyBoardListen.onKeyboardChange(false, visibleHeight - rootViewVisibleHeight);
                }
                rootViewVisibleHeight = visibleHeight;
                return;
            }
        }else{
            int currHeight = mContentView.getHeight();
            if (currHeight == 0) {
                Log.i(TAG, "currHeight is 0");
                return;
            }
            boolean hasChange = false;
            if (mPreHeight == 0) {
                mPreHeight = currHeight;
                mOriginHeight = currHeight;
            } else {
                if (mPreHeight != currHeight) {
                    hasChange = true;
                    mPreHeight = currHeight;
                } else {
                    hasChange = false;
                }
            }
            if (hasChange) {
                boolean isShow;
                int keyboardHeight = 0;
                if (mOriginHeight == currHeight) {
                    //hidden
                    isShow = false;
                } else {
                    //show
                    keyboardHeight = mOriginHeight - currHeight;
                    isShow = true;
                }

                isShowing = isShow;
                if (mKeyBoardListen != null) {
                    mKeyBoardListen.onKeyboardChange(isShow, keyboardHeight);
                }
            }
        }
    }

    public void destroy() {
        if (mContentView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }
}
