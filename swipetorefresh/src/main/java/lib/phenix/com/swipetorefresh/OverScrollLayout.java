package lib.phenix.com.swipetorefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * @author zhouphenix on 2017-2-27.
 */

public class OverScrollLayout extends ViewGroup {

    public static final int NONE = 0;
    public static final int LEFT = 1;
    public static final int TOP = 1 << 1;
    public static final int RIGHT = 1 << 2;
    public static final int BOTTOM = 1 << 3;

    @IntDef({NONE, LEFT, TOP, RIGHT, BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SwipeDirection {
    }

    private final ViewDragHelper mViewDragHelper;

    int mDirectionMask = TOP;

    @SwipeDirection int mCurrentDirection;

    boolean enableSwipe;

    int mOriginX;
    int mOriginY;

    /**
     * 阻尼因子
     */
    float mFactor = 0.3f;
    /**
     * 水平drag的范围
     */
    int mVerticalDragRange;
    /**
     * 竖直drag的范围
     */
    int mHorizontalDragRange;


    /**
     * 主体View
     */
    private View mContentView;
    private View mLeftView;
    private View mRightView;
    private View mBottomView;
    private View mTopView;

    /**
     * 当前touch的坐标
     */
    private float mTouchX, mTouchY;
    /**
     * 记录MotionEvent.ACTION_DOWN的坐标
     */
    private float downX, downY;


    public OverScrollLayout(@NonNull Context context, @NonNull View contentView, int directionMask) {
        super(context);
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallback());
        mContentView = contentView;
        mDirectionMask = directionMask;
        enableSwipe = true;
    }


    public OverScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallback());

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwipeToRefreshLayout);
        int contentLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_contentView, View.NO_ID);
        int leftLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_leftView, View.NO_ID);
        int topLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_topView, View.NO_ID);
        int rightLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_rightView, View.NO_ID);
        int bottomLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_bottomView, View.NO_ID);
        mDirectionMask = ta.getInt(R.styleable.SwipeToRefreshLayout_swipeDirection, mDirectionMask);
        ta.recycle();

        LayoutInflater inflater = LayoutInflater.from(context);

        if (View.NO_ID != contentLayoutId) {
            mContentView = inflater.inflate(contentLayoutId, this, false);
            addView(mContentView, 0);
        } else {
            throw new IllegalStateException("请为您的SwipeToRefreshLayout设置主体布局，详细参考SwipeToRefreshLayout配置文档");
        }
        if (View.NO_ID != leftLayoutId) {
            mLeftView = inflater.inflate(leftLayoutId, this, false);
            addView(mLeftView);
        }
        if (View.NO_ID != topLayoutId) {
            mTopView = inflater.inflate(topLayoutId, this, false);
            addView(mTopView);
        }
        if (View.NO_ID != rightLayoutId) {
            mRightView = inflater.inflate(rightLayoutId, this, false);
            addView(mRightView);
        }
        if (View.NO_ID != bottomLayoutId) {
            mBottomView = inflater.inflate(bottomLayoutId, this, false);
            addView(mBottomView);
        }
        enableSwipe = true;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHorizontalDragRange = w;
        mVerticalDragRange = h;
    }

    /**
     * 计算所有ChildView的宽度和高度 然后根据ChildView的计算结果，设置自己的宽和高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // 计算出所有的childView的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * 根据childView计算的出的宽和高，以及设置的margin计算容器的宽和高，主要用于容器是warp_content时
         */
        MarginLayoutParams cMarginParams = (MarginLayoutParams) mContentView.getLayoutParams();

        /**
         * 记录如果是wrap_content是设置的宽和高
         */
        int width = cMarginParams.leftMargin + mContentView.getMeasuredWidth() + cMarginParams.rightMargin;
        int height = cMarginParams.topMargin + mContentView.getMeasuredHeight() + cMarginParams.bottomMargin;

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width, heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        /**mContentView*/
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) mContentView.getLayoutParams();
        int cl = marginLayoutParams.leftMargin;
        int ct = marginLayoutParams.topMargin;
        int cr = cl + mContentView.getMeasuredWidth() - marginLayoutParams.leftMargin - marginLayoutParams.rightMargin;
        int cb = ct + mContentView.getMeasuredHeight();
        mContentView.layout(cl, ct, cr, cb);
        mOriginX = mContentView.getLeft();
        mOriginY = mContentView.getTop();
        MarginLayoutParams otherParams;
        /**mLeftView*/
        if (null != mLeftView) {
            otherParams = (MarginLayoutParams) mLeftView.getLayoutParams();
            cl = mContentView.getLeft() - marginLayoutParams.leftMargin - (otherParams.leftMargin + mLeftView.getMeasuredWidth() + otherParams.rightMargin);
            ct = mContentView.getTop() + otherParams.topMargin;
            cr = mContentView.getLeft() - marginLayoutParams.leftMargin - otherParams.rightMargin;
            cb = mContentView.getBottom() - otherParams.bottomMargin;
            mLeftView.layout(cl, ct, cr, cb);
        }

        /**mTopView*/
        if (null != mTopView) {
            otherParams = (MarginLayoutParams) mTopView.getLayoutParams();
            cl = mContentView.getLeft() + otherParams.leftMargin;
            ct = mContentView.getTop() - marginLayoutParams.topMargin - mTopView.getMeasuredHeight() - otherParams.topMargin - otherParams.bottomMargin;
            cr = mContentView.getRight() - otherParams.rightMargin;
            cb = mContentView.getTop() - marginLayoutParams.topMargin - otherParams.bottomMargin;
            mTopView.layout(cl, ct, cr, cb);
        }

        /**mRightView*/
        if (null != mRightView) {
            otherParams = (MarginLayoutParams) mRightView.getLayoutParams();
            cl = mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin;
            ct = mContentView.getTop() + otherParams.topMargin;
            cr = mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin + mRightView.getMeasuredWidth() + otherParams.rightMargin;
            cb = mContentView.getBottom() - otherParams.bottomMargin;
            mRightView.layout(cl, ct, cr, cb);
        }

        /**mBottomView*/
        if (null != mBottomView) {
            otherParams = (MarginLayoutParams) mBottomView.getLayoutParams();
            cl = mContentView.getLeft() + otherParams.leftMargin;
            ct = mContentView.getBottom() + marginLayoutParams.bottomMargin + otherParams.topMargin;
            cr = mContentView.getRight() - otherParams.rightMargin;
            cb = mContentView.getBottom() + marginLayoutParams.bottomMargin + otherParams.topMargin + mBottomView.getMeasuredHeight() + otherParams.bottomMargin;
            mBottomView.layout(cl, ct, cr, cb);
        }

    }


    /**
     * 判断是否可以direction这个方向的划动
     *
     * @param direction DragDirection
     * @return boolean
     */
    private boolean isAllowDragDirection(int direction) {
        return direction == (mDirectionMask & direction);
    }


    class ViewDragHelperCallback extends ViewDragHelper.Callback {
        int mLastDragState;
        int mDragOffset;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mContentView && enableSwipe;
        }


        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            mViewDragHelper.settleCapturedViewAt(mOriginX, mOriginY);
            ViewCompat.postInvalidateOnAnimation(OverScrollLayout.this);
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mDragOffset = mCurrentDirection == LEFT || mCurrentDirection == RIGHT ? Math.abs(left) : Math.abs(top);
            MarginLayoutParams marginLayoutParams, otherParams;
            marginLayoutParams = (MarginLayoutParams) mContentView.getLayoutParams();
            switch (mCurrentDirection & mDirectionMask) {
                case LEFT:
                    if (null != mLeftView){
                        otherParams = (MarginLayoutParams) mLeftView.getLayoutParams();
                        mLeftView.layout(
                                mContentView.getLeft() - marginLayoutParams.leftMargin - (otherParams.leftMargin + mLeftView.getMeasuredWidth() + otherParams.rightMargin),
                                mLeftView.getTop(),
                                mContentView.getLeft() - marginLayoutParams.leftMargin - otherParams.rightMargin,
                                mLeftView.getBottom());
                    }
                    break;
                case TOP:
                    if (null != mTopView) {
                        otherParams = (MarginLayoutParams) mTopView.getLayoutParams();
                        mTopView.layout(mTopView.getLeft(),
                                mContentView.getTop() - marginLayoutParams.topMargin - mTopView.getMeasuredHeight() - otherParams.topMargin - otherParams.bottomMargin,
                                mTopView.getRight(),
                                mContentView.getTop() - marginLayoutParams.topMargin - otherParams.bottomMargin);
                    }
                    break;
                case RIGHT:
                    if (null != mRightView) {
                        otherParams = (MarginLayoutParams) mRightView.getLayoutParams();
                        mRightView.layout(mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin,
                                mRightView.getTop(),
                                mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin + mRightView.getMeasuredWidth() + otherParams.rightMargin,
                                mRightView.getBottom());
                    }
                    break;
                case BOTTOM:
                    if (null != mBottomView) {
                        otherParams = (MarginLayoutParams) mBottomView.getLayoutParams();
                        mBottomView.layout(mTopView.getLeft(),
                                mContentView.getBottom() + marginLayoutParams.bottomMargin + otherParams.topMargin,
                                mTopView.getRight(),
                                mContentView.getBottom() + marginLayoutParams.bottomMargin + otherParams.topMargin + mBottomView.getMeasuredHeight() + otherParams.bottomMargin);
                    }
                    break;
            }


        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mLastDragState == ViewDragHelper.STATE_SETTLING && state == ViewDragHelper.STATE_DRAGGING) {
                if (mViewDragHelper.smoothSlideViewTo(mContentView, mOriginX, mOriginY)) {
                    ViewCompat.postInvalidateOnAnimation(OverScrollLayout.this);
                }
            }
            if (state == ViewDragHelper.STATE_IDLE) {
                mCurrentDirection = NONE;
            }
            Log.e("zhou","==============onViewDragStateChanged=================="+state);
            mLastDragState = state;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) (mHorizontalDragRange * mFactor);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return (int) (mVerticalDragRange * mFactor);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int leftBounds, rightBounds;
            if (isAllowDragDirection(LEFT)
                    && !canScrollRight(mContentView)
                    && left >= mOriginX
                    && mCurrentDirection == LEFT
                    ) {
                leftBounds = getPaddingLeft();
                rightBounds = getViewHorizontalDragRange(child);
                return Math.min(Math.max(left, leftBounds), rightBounds);
            }
            if (isAllowDragDirection(RIGHT)
                    && !canScrollLeft(mContentView)
                    && left <= mOriginX
                    && mCurrentDirection == RIGHT) {
                leftBounds = -getViewHorizontalDragRange(child);
                rightBounds = getPaddingLeft();
                return Math.min(Math.max(left, leftBounds), rightBounds);
            }
            return mOriginX;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int topBounds, bottomBounds;
            if (isAllowDragDirection(TOP)
                    && !canScrollBottom(child)
                    && top >= mOriginY
                    && mCurrentDirection == TOP) {
                topBounds = getPaddingTop();
                bottomBounds = getViewVerticalDragRange(child);
                return Math.min(Math.max(top, topBounds), bottomBounds);
            }
            if (isAllowDragDirection(BOTTOM)
                    && !canScrollTop(child)
                    && top <= mOriginY
                    && mCurrentDirection == BOTTOM) {
                topBounds = -getViewVerticalDragRange(child);
                bottomBounds = getPaddingTop();
                return Math.min(Math.max(top, topBounds), bottomBounds);
            }
            return mOriginY;
        }
    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean handled = false;
        if (isEnabled()) {
            mTouchX = event.getRawX();
            mTouchY = event.getRawY();
            final int action = event.getActionMasked();
            if (mCurrentDirection == NONE) {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downX = mTouchX;
                        downY = mTouchY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float slope = (mTouchY - downY) / (mTouchX - downX);
                        mCurrentDirection = Math.abs(slope) >= 1 ? (mTouchY >= downY ? TOP : BOTTOM) : (mTouchX >= downX ? LEFT : RIGHT);
                        break;
                }
                Log.e("zhou", "++++++++++mCurrentDirection++++++++++++++++++"+mCurrentDirection);
            }
            handled = mViewDragHelper.shouldInterceptTouchEvent(event);
        } else {
            mViewDragHelper.cancel();
        }
        if (!handled) mCurrentDirection = NONE;
        return handled || super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * 支持margin设置，直接使用系统的MarginLayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }


    public boolean canScrollTop(View view) {
        return ViewCompat.canScrollVertically(view, 1);
    }

    public boolean canScrollBottom(View view) {
        return ViewCompat.canScrollVertically(view, -1);
    }

    public boolean canScrollLeft(View view) {
        return ViewCompat.canScrollHorizontally(view, 1);
    }

    public boolean canScrollRight(View view) {
        return ViewCompat.canScrollHorizontally(view, -1);
    }


}
