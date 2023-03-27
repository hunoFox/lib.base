package com.hunofox.baseFramework.widget.recyclerView.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.hunofox.baseFramework.utils.CheckUtils;

import java.util.List;

/**
 * 项目名称：TongTong
 * 项目作者：胡玉君
 * 创建日期：2019-09-11 15:30.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * <p>
 * 展示一个分类悬停的decoration
 * ----------------------------------------------------------------------------------------------------
 */
public class ClassAndHoverDecoration extends RecyclerView.ItemDecoration {

    private List<String> datas;

    //悬停及分类标题的高度、文字大小
    private final float hoverHeight;
    private final float textSize;

    private final Paint paint;
    private final Rect bounds;

    //标题和背景的颜色
    private final int colorBg = Color.parseColor("#f2f2f2");
    private final int textColor = Color.parseColor("#333333");

    public ClassAndHoverDecoration(Context context, List<String> datas) {
        this.datas = datas;

        hoverHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                25, context.getResources().getDisplayMetrics());
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                12, context.getResources().getDisplayMetrics());

        paint = new Paint();
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);

        bounds = new Rect();
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (position > -1) {
            if (position == 0) {
                outRect.set(0, (int) hoverHeight, 0, 0);
            } else if(position >= datas.size()) {
                outRect.set(0, 0, 0, 0);
            } else {
                String firstCase = CheckUtils.str2pinyin(datas.get(position));
                String lastFirstCase = CheckUtils.str2pinyin(datas.get(position - 1));
                if (!CheckUtils.isEmpty(firstCase) && !firstCase.substring(0, 1).equals(lastFirstCase.substring(0, 1))) {
                    outRect.set(0, (int) hoverHeight, 0, 0);
                } else {
                    outRect.set(0, 0, 0, 0);
                }
            }
        }

    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewLayoutPosition();
            if (position > -1) {
                if (position == 0) {
                    //第一个要画title
                    drawTitle(c, left, right, child, params, position);
                } else if(position >= datas.size()){

                } else {
                    String firstCase = CheckUtils.str2pinyin(datas.get(position));
                    String lastFirstCase = CheckUtils.str2pinyin(datas.get(position - 1));
                    if (!CheckUtils.isEmpty(firstCase) && !firstCase.substring(0, 1).equals(lastFirstCase.substring(0, 1))) {
                        drawTitle(c, left, right, child, params, position);
                    }
                }
            }

        }
    }

    private void drawTitle(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {
        paint.setColor(colorBg);
        c.drawRect(left, child.getTop() - params.topMargin - hoverHeight, right, child.getTop() - params.topMargin, paint);
        paint.setColor(textColor);

        paint.getTextBounds(CheckUtils.str2pinyin(datas.get(position)).substring(0, 1), 0, 1, bounds);
        c.drawText(CheckUtils.str2pinyin(datas.get(position)).substring(0, 1),
                child.getPaddingLeft(),
                child.getTop() - params.topMargin - (hoverHeight / 2 - bounds.height() / 2), paint);
    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        if (position == -1) return;//在搜索到没有的索引的时候position可能等于-1，所以在这里判断一下
        String tag = CheckUtils.str2pinyin(datas.get(position)).substring(0, 1);
        View child = parent.findViewHolderForLayoutPosition(position).itemView;
        //Canvas是否位移过的标志
        boolean flag = false;
        if ((position + 1) < datas.size()) {
            //当前第一个可见的Item的字母索引，不等于其后一个item的字母索引，说明悬浮的View要切换了
            if (!tag.equals(CheckUtils.str2pinyin(datas.get(position + 1)).substring(0, 1))) {
                //当第一个可见的item在屏幕中剩下的高度小于title的高度时，开始悬浮Title的动画
                if (child.getHeight() + child.getTop() < hoverHeight) {
                    c.save();
                    flag = true;
                    /**
                     * 下边的索引把上边的索引顶上去的效果
                     */
                    c.translate(0, child.getHeight() + child.getTop() - hoverHeight);

                    /**
                     * 头部折叠起来的视效（下边的索引慢慢遮住上边的索引）
                     */
                    /*c.clipRect(parent.getPaddingLeft(),
                            parent.getPaddingTop(),
                            parent.getRight() - parent.getPaddingRight(),
                            parent.getPaddingTop() + child.getHeight() + child.getTop());*/
                }
            }
        }
        paint.setColor(colorBg);
        c.drawRect(parent.getPaddingLeft(),
                parent.getPaddingTop(),
                parent.getRight() - parent.getPaddingRight(),
                parent.getPaddingTop() + hoverHeight, paint);
        paint.setColor(textColor);
        paint.getTextBounds(tag, 0, tag.length(), bounds);
        c.drawText(tag, child.getPaddingLeft(),
                parent.getPaddingTop() + hoverHeight - (hoverHeight / 2 - bounds.height() / 2),
                paint);
        if (flag)
            c.restore();//恢复画布到之前保存的状态

    }
}
