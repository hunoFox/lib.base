package com.hunofox.baseFramework.widget.recyclerView.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.DimenRes;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 项目名称：kcwxApp
 * 项目作者：胡玉君
 * 创建日期：2017/10/13 9:32.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 *
 * 示例代码：
 *
 * recyclerView.addItemDecoration(HorizontalDividerItemDecoration.Builder(AppApplication.CONTEXT)
 *      .color(Color.RED)//分割线颜色
 *      .size(UiUtils.dp2px(8f).toInt())//分割线高度
 *      .showLastDivider(false)//是否展示最下面的一条分割线
 *      .margin(UiUtils.dp2px(20f).toInt(), UiUtils.dp2px(8f).toInt())//分割线左右距离
 *      .showFooterDivider(false)//是否展示footer的分割线，若不展示，则需要设置footer的数量.footerCount(1)
 *      .footerCount(1)//footer的数量
 *      .build()
 * )
 *
 * 除此以外，还有一些provider回调，在此不再展示
 * ----------------------------------------------------------------------------------------------------
 */

public class HorizontalDividerItemDecoration extends BaseItemDecoration{

    private MarginProvider mMarginProvider;

    protected HorizontalDividerItemDecoration(Builder builder) {
        super(builder);
        mMarginProvider = builder.mMarginProvider;
    }

    @Override
    protected Rect getDividerBound(int position, RecyclerView parent, View child) {
        Rect bounds = new Rect(0, 0, 0, 0);
        int transitionX = (int) ViewCompat.getTranslationX(child);
        int transitionY = (int) ViewCompat.getTranslationY(child);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        bounds.left = parent.getPaddingLeft() +
                mMarginProvider.dividerLeftMargin(position, parent) + transitionX;
        bounds.right = parent.getWidth() - parent.getPaddingRight() -
                mMarginProvider.dividerRightMargin(position, parent) + transitionX;

        int dividerSize = getDividerSize(position, parent);
        if (mDividerType == DividerType.DRAWABLE) {
            bounds.top = child.getBottom() + params.topMargin + transitionY;
            bounds.bottom = bounds.top + dividerSize;
        } else {
            bounds.top = child.getBottom() + params.topMargin + dividerSize / 2 + transitionY;
            bounds.bottom = bounds.top;
        }

        return bounds;
    }

    @Override
    protected void setItemOffsets(Rect outRect, int position, RecyclerView parent) {
        outRect.set(0, 0, 0, getDividerSize(position, parent));
    }

    private int getDividerSize(int position, RecyclerView parent) {
        if (mPaintProvider != null) {
            return (int) mPaintProvider.dividerPaint(position, parent).getStrokeWidth();
        } else if (mSizeProvider != null) {
            return mSizeProvider.dividerSize(position, parent);
        } else if (mDrawableProvider != null) {
            Drawable drawable = mDrawableProvider.drawableProvider(position, parent);
            return drawable.getIntrinsicHeight();
        }
        throw new RuntimeException("failed to get size");
    }

    /**
     * Interface for controlling divider margin
     */
    public interface MarginProvider {

        /**
         * Returns left margin of divider.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return left margin
         */
        int dividerLeftMargin(int position, RecyclerView parent);

        /**
         * Returns right margin of divider.
         *
         * @param position Divider position
         * @param parent   RecyclerView
         * @return right margin
         */
        int dividerRightMargin(int position, RecyclerView parent);
    }

    public static class Builder extends BaseItemDecoration.Builder<Builder> {

        private MarginProvider mMarginProvider = new MarginProvider() {
            @Override
            public int dividerLeftMargin(int position, RecyclerView parent) {
                return 0;
            }

            @Override
            public int dividerRightMargin(int position, RecyclerView parent) {
                return 0;
            }
        };

        public Builder(Context context) {
            super(context);
        }

        public Builder margin(final int leftMargin, final int rightMargin) {
            return marginProvider(new MarginProvider() {
                @Override
                public int dividerLeftMargin(int position, RecyclerView parent) {
                    return leftMargin;
                }

                @Override
                public int dividerRightMargin(int position, RecyclerView parent) {
                    return rightMargin;
                }
            });
        }

        public Builder margin(int horizontalMargin) {
            return margin(horizontalMargin, horizontalMargin);
        }

        public Builder marginResId(@DimenRes int leftMarginId, @DimenRes int rightMarginId) {
            return margin(mResources.getDimensionPixelSize(leftMarginId),
                    mResources.getDimensionPixelSize(rightMarginId));
        }

        public Builder marginResId(@DimenRes int horizontalMarginId) {
            return marginResId(horizontalMarginId, horizontalMarginId);
        }

        public Builder marginProvider(MarginProvider provider) {
            mMarginProvider = provider;
            return this;
        }

        public HorizontalDividerItemDecoration build() {
            checkBuilderParams();
            return new HorizontalDividerItemDecoration(this);
        }
    }
}
