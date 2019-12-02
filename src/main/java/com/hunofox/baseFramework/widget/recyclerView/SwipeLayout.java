package com.hunofox.baseFramework.widget.recyclerView;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * 项目名称：侧滑删除控件
 * 项目作者：胡玉君
 * 创建日期：2016/4/7 17:56.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class SwipeLayout extends FrameLayout {

	private Status status = Status.Close;
	private OnSwipeLayoutListener swipeLayoutListener;
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public OnSwipeLayoutListener getSwipeLayoutListener() {
		return swipeLayoutListener;
	}

	public void setSwipeLayoutListener(OnSwipeLayoutListener swipeLayoutListener) {
		this.swipeLayoutListener = swipeLayoutListener;
	}

	public static enum Status{
		Close, Open, Draging
	}
	public interface OnSwipeLayoutListener {
		/**
		 * 1. 用户按下时
		 */
		void onPressed(SwipeLayout swipeLayout);

		/**
		 * 2. 用户在竖向滑动时
		 */
		void onVerticalScroll(SwipeLayout mSwipeLayout);

		/**
		 * 3. 要去开启
		 */
		void onStartOpen(SwipeLayout mSwipeLayout);

		/**
		 * 3. 要去关闭
		 */
		void onStartClose(SwipeLayout mSwipeLayout);

		/**
		 * 4. 滑动时
		 */
		void onDragging(SwipeLayout mSwipeLayout);

		/**
		 * 5. 开启时
		 */
		void onOpen(SwipeLayout mSwipeLayout);

		/**
		 * 6. 关闭时
		 */
		void onClose(SwipeLayout mSwipeLayout);
	}
	
	public SwipeLayout(Context context) {
		this(context, null);
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
	}
	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
		// c. 重写监听
		@Override
		public boolean tryCaptureView(View view, int id) {
			return !flag;
		}
		
		// 限定移动范围
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// left
			if(child == mFrontView){
				if(left > 0){
					return 0;
				}else if(left < -mRange){
					return -mRange;
				}
			}else if (child == mBackView) {
				if(left > mWidth){
					return mWidth;
				}else if (left < mWidth - mRange) {
					return mWidth - mRange;
				}
			}

			/** 返回值不做限定，仅仅决定执行动画速度 */
			return left;
		}
		
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

			// 传递事件
			if(changedView == mFrontView){
				mBackView.offsetLeftAndRight(dx);
				
			}else if (changedView == mBackView) {
				mFrontView.offsetLeftAndRight(dx);
			}
			
			dispatchSwipeEvent();
			invalidate();
			
		}
		
		public void onViewReleased(View releasedChild, float xvel, float yvel) {

			if (xvel == 0 && mFrontView.getLeft() <= -mRange / 5f) {
				open();
			}
//			if(xvel == 0){
//				close();
//			}
			else if (xvel < 0) {
				open();
			}else if(xvel > 0){
				close();
			}else{
				close();
			}

		}
		
	};
	private ViewDragHelper mDragHelper;
	private View mBackView;
	private View mFrontView;
	private int mHeight;
	private int mWidth;
	private int mRange;
	
	// b. 传递触摸事件
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mDragHelper.shouldInterceptTouchEvent(ev);
	}
	
	protected void dispatchSwipeEvent() {
		
		if(swipeLayoutListener != null){
			swipeLayoutListener.onDragging(this);
		}
		
		// 记录上一次的状态
		Status preStatus = status;
		// 更新当前状态
		status = updateStatus();
		if (preStatus != status && swipeLayoutListener != null) {
			if (status == Status.Close) {
				swipeLayoutListener.onClose(this);
			} else if (status == Status.Open) {
				swipeLayoutListener.onOpen(this);
			} else if (status == Status.Draging) {
				if(preStatus == Status.Close){
					swipeLayoutListener.onStartOpen(this);
				}else if (preStatus == Status.Open) {
					swipeLayoutListener.onStartClose(this);
				}
			}
		}
	}

	private Status updateStatus() {
		
		int left = mFrontView.getLeft();
		if(left == 0){
			return Status.Close;
		}else if (left == -mRange) {
			return Status.Open;
		}
		return Status.Draging;
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		
		if(mDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
		
	}

	/** 该方法与RecyclerView的滑动监听相抵触，若不在RecyclerView的Adaptor中设置点击监听，则需要返回true */
	float startX, startY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()){
			case MotionEvent.ACTION_DOWN:
				startX = event.getRawX();
				startY = event.getRawY();
				if(swipeLayoutListener != null){
					swipeLayoutListener.onPressed(this);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				float endX = event.getRawX();
				float endY = event.getRawY();

				boolean flag = Math.abs(endX - startX) < Math.abs(endY - startY);
				if(flag){
					if(swipeLayoutListener != null){
						swipeLayoutListener.onVerticalScroll(this);
					}
					return false;
				}
				break;
			case MotionEvent.ACTION_UP:
				startX = 0f;
				startY = 0f;
				break;
			case MotionEvent.ACTION_CANCEL:
				startX = 0f;
				startY = 0f;
				break;
		}
		try {
			mDragHelper.processTouchEvent(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onTouchEvent(event);
//		return true;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// 摆放位置
		layoutContent(false);
	}
	
	private void layoutContent(boolean isOpen) {
		// 摆放前View
		Rect frontRect = computeFrontViewRect(isOpen);
		mFrontView.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);
		// 摆放后View
		Rect backRect = computeBackViewViaFront(frontRect);
		mBackView.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);
		
		// 调整顺序, 把mFrontView前置
		bringChildToFront(mFrontView);
	}

	private Rect computeBackViewViaFront(Rect frontRect) {
		int left = frontRect.right;
		return new Rect(left, 0, left + mRange, 0 + mHeight);
	}

	private Rect computeFrontViewRect(boolean isOpen) {
		int left = 0;
		if(isOpen){
			left = -mRange;
		}
		return new Rect(left, 0, left + mWidth, 0 + mHeight);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// 当xml被填充完毕时调用
		mBackView = getChildAt(0);
		mFrontView = getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mHeight = mFrontView.getMeasuredHeight();
		mWidth = mFrontView.getMeasuredWidth();
		
		mRange = mBackView.getMeasuredWidth();
	}

	/**
	 * Activity中可执行方法
	 */
	public void close() {
		close(true);
	}
	public void close(boolean isSmooth){
		int finalLeft = 0;
		if(isSmooth){
			//开始动画
			if(mDragHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)){
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else {
			layoutContent(false);
		}
	}

	public void open() {
		open(true);
	}
	public void open(boolean isSmooth){
		int finalLeft = -mRange;
		if(isSmooth){
			//开始动画
			if(mDragHelper.smoothSlideViewTo(mFrontView, finalLeft, 0)){
				ViewCompat.postInvalidateOnAnimation(this);
			}
		}else {
			layoutContent(true);
		}
	}

	public boolean isOpen(){
		return status == Status.Open;
	}
	public boolean isClose(){
		return status == Status.Close;
	}
	public boolean isDragging(){
		return status == Status.Draging;
	}

	private boolean flag = false;
	public void forbiddenSwipe(boolean flag){
		this.flag = flag;
	}


}
