package com.hunofox.baseFramework.widget.recyclerView;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

/**
 * 项目名称：OpenEyeVideo
 * 项目作者：胡玉君
 * 创建日期：2017/8/29 16:03.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class LoadMoreRecyclerView extends RecyclerView {

    private int columnCount = 1;

    //上拉加载相关
    private final LoadMoreFooter loadMoreFooter;
    private final WeakReference<Context> reference;

    //上拉加载相关参数
    private int lastVisiblePosition = 0;
    private boolean loadMoreEnable = true;

    private int loadMoreState = State.STATE_COMPLETE_IDLE;

    //滑动监听
    private State listener;
    public void setOnScrollerListener(State listener){
        this.listener = listener;
    }
    public interface State{
        int STATE_COMPLETE_IDLE = 1;
        int STATE_LOADING_DATA = 0;
        int STATE_NO_MORE = -1;

        void onScrollStateChanged(RecyclerView recyclerView, int newState);
        void onScrolled(RecyclerView recyclerView, int dx, int dy);
        void onNestedScrolled(NestedScrollView recyclerView, int dx, int dy, int oldX, int oldY);
    }

    //上拉加载监听
    private OnLoadMoreListener loadMoreListener;
    public void setOnLoadMoreListener(OnLoadMoreListener listener){
        this.loadMoreListener = listener;
    }
    public interface OnLoadMoreListener{
        void onLoadMore();
    }

    public LoadMoreFooter getLoadMoreFooter(){
        return loadMoreFooter;
    }

    public void setLoadMoreHint(String str){
        if(loadMoreFooter != null){
            loadMoreFooter.setLoadingHint(str);
        }
    }
    public void setNoMoreHint(String str){
        if(loadMoreFooter != null){
            loadMoreFooter.setNoMoreHint(str);
        }
    }
    public void setNoNetworkHint(String str){
        if(loadMoreFooter != null){
            loadMoreFooter.setNoNetWorkHint(str);
        }
    }

    public LoadMoreRecyclerView(Context context) {
        this(context, null);
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

        reference = new WeakReference<>(context);
        loadMoreFooter = new LoadMoreFooter(reference.get());
    }

    public LoadMoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

        reference = new WeakReference<>(context);
        loadMoreFooter = new LoadMoreFooter(reference.get());
    }

    /**
     * 设置列数
     *
     * @param columnCount   列数
     */
    public void setColumnCount(int columnCount){
        this.columnCount = columnCount;
    }

    /**
     * 设置上拉加载是否可用
     *
     * @param enable true为启用
     */
    public void setLoadMoreEnable(boolean enable){
        this.loadMoreEnable = enable;
        showFooter(enable);
    }

    /**
     * 设置加载状态
     *
     * 可传入参数值为
     *
     * STATE_COMPLETE_IDLE, STATE_LOADING_DATA, STATE_NO_MORE
     */
    public void setLoadMoreState(int state) {
        this.loadMoreState = state;
        if(loadMoreEnable){
            showFooter(true);
            if (state == State.STATE_LOADING_DATA) {
                loadMoreFooter.onLoading();
            } else if(state == State.STATE_NO_MORE){
                loadMoreFooter.onNoMore();
            } else {
                loadMoreFooter.onComplete();
            }
        }else{
            setLoadMoreEnable(false);
        }
    }
    public int getLoadMoreState(){
        return loadMoreState;
    }

    //是否展示加载更多的footer
    private void showFooter(boolean show){
        try {
            if (show) {
                loadMoreFooter.setVisibility(VISIBLE);
                loadMoreFooter.getFooterChildView().setVisibility(VISIBLE);
            } else {
                loadMoreFooter.setVisibility(GONE);
                loadMoreFooter.getFooterChildView().setVisibility(GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化recyclerView
    private void init() {
        setHasFixedSize(true);
        setItemAnimator(new DefaultItemAnimator());
        setNestedScrollingEnabled(false);
        setFocusable(false);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (loadMoreEnable
                        && loadMoreState == State.STATE_COMPLETE_IDLE
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                        && getAdapter() != null
                        && lastVisiblePosition + 1 == getAdapter().getItemCount()) {
                    LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();

                    if (visibleItemCount > 0
                            && lastVisiblePosition >= totalItemCount - 1
                            && totalItemCount >= visibleItemCount) {
                        if(loadMoreListener != null){
                            setLoadMoreState(State.STATE_LOADING_DATA);
                            loadMoreListener.onLoadMore();
                        }
                    }
                }

                if(listener != null){
                    listener.onScrollStateChanged(recyclerView, newState);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    lastVisiblePosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                } else if (layoutManager instanceof GridLayoutManager) {
                    lastVisiblePosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                }

                if(listener != null){
                    listener.onScrolled(recyclerView, dx, dy);
                }
            }
        });
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if(getLayoutManager() == null){
            if(columnCount <= 1){
                setLayoutManager(new LinearLayoutManager(reference.get()));
            }else{
                setLayoutManager(new GridLayoutManager(reference.get(), columnCount));
            }
        }
        if(adapter instanceof BaseRecyclerAdapter){
            ((BaseRecyclerAdapter) adapter).addFootView(loadMoreFooter);
        }
        super.setAdapter(adapter);
    }

    //嵌套NestedScrollerView
    public void setNestedScrollView(final NestedScrollView nestedScrollView) {
        if(nestedScrollView != null){
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        if (loadMoreEnable
                                && loadMoreState == State.STATE_COMPLETE_IDLE
                                && getAdapter() != null){
                            LayoutManager layoutManager = LoadMoreRecyclerView.this.getLayoutManager();
                            int visibleItemCount = layoutManager.getChildCount();
                            int totalItemCount = layoutManager.getItemCount();

                            if (visibleItemCount > 0
                                    && totalItemCount >= visibleItemCount) {
                                if(loadMoreListener != null){
                                    setLoadMoreState(State.STATE_LOADING_DATA);
                                    loadMoreListener.onLoadMore();
                                }
                            }
                        }
                    }

                    if(listener != null){
                        listener.onNestedScrolled(nestedScrollView, scrollX, scrollY, oldScrollX, oldScrollY);
                    }
                }
            });
        }
    }

}
