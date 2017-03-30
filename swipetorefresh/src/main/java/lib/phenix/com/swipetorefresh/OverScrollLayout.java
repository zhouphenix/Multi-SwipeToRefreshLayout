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
    int contentLayoutId;
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


    private float damping = 0.65f;


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
        contentLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_contentLayoutId, View.NO_ID);
        int leftLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_leftView, View.NO_ID);
        int topLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_topView, View.NO_ID);
        int rightLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_rightView, View.NO_ID);
        int bottomLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_bottomView, View.NO_ID);

        int leftColorResourceId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_leftColor, View.NO_ID);
        int topColorResourceId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_topColor, View.NO_ID);
        int rightColorResourceId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_rightColor, View.NO_ID);
        int bottomColorResourceId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_bottomColor, View.NO_ID);

        mDirectionMask = ta.getInt(R.styleable.SwipeToRefreshLayout_swipeDirection, mDirectionMask);
        mFactor = ta.getFloat(R.styleable.SwipeToRefreshLayout_horizontalRangeFactor, 0.3f);
        ta.recycle();

        LayoutInflater inflater = LayoutInflater.from(context);

        if (View.NO_ID != leftLayoutId) {
            mLeftView = inflater.inflate(leftLayoutId, this, false);
        }
        if (null == mLeftView && View.NO_ID != leftColorResourceId) {
            mLeftView = new View(context);
            mLeftView.setBackgroundColor(getResources().getColor(leftColorResourceId));
            MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mLeftView.setLayoutParams(lp);
        }
        if(null!=mLeftView){
            addView(mLeftView);
        }
        if (View.NO_ID != topLayoutId) {
            mTopView = inflater.inflate(topLayoutId, this, false);
        }
        if (null == mTopView && View.NO_ID != topColorResourceId) {
            mTopView = new View(context);
            mTopView.setBackgroundColor(getResources().getColor(topColorResourceId));
            MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mTopView.setLayoutParams(lp);
        }
        if(null!=mTopView){
            addView(mTopView);
        }
        if (View.NO_ID != rightLayoutId) {
            mRightView = inflater.inflate(rightLayoutId, this, false);
        }
        if (null == mRightView && View.NO_ID != rightColorResourceId) {
            mRightView = new View(context);
            mRightView.setBackgroundColor(getResources().getColor(rightColorResourceId));
            MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mRightView.setLayoutParams(lp);
        }
        if (null != mRightView) {
            addView(mBottomView);
        }
        if (View.NO_ID != bottomLayoutId) {
            mBottomView = inflater.inflate(bottomLayoutId, this, false);
        }
        if (null == mBottomView && View.NO_ID != bottomColorResourceId) {
            mBottomView = new View(context);
            mBottomView.setBackgroundColor(getResources().getColor(bottomColorResourceId));
            MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mBottomView.setLayoutParams(lp);
        }
        if (null != mBottomView) {
            addView(mBottomView);
        }
        enableSwipe = true;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (View.NO_ID != contentLayoutId) {
            mContentView = findViewById(contentLayoutId);
        } else {
            throw new IllegalStateException("请为OverScrollLayout添加contentLayoutId属性，以索引目标View");
        }
    }

    public void setDamping(float damping) {
        this.damping = damping;
    }
    /**
     * 设置拖动百分比限制
     * @param mFactor
     */
    public void setFactor(float mFactor) {
        this.mFactor = mFactor;
    }

    public void enableSwipe(boolean enableSwipe) {
        this.enableSwipe = enableSwipe;
    }
    /**
     * 添加可以direction划动
     *
     * @param direction SwipeDirection
     */
    public void enableDragDirection(int direction) {
        mDirectionMask |= direction;
    }

    /**
     * 删除可以direction划动
     *
     * @param direction SwipeDirection
     */
    public void disableDragDirection(int direction) {
        mDirectionMask &= ~direction;
    }

    /**
     * 是否禁用了direction
     * @param direction SwipeDirection 禁用了该方向
     * @return boolean
     */
    public boolean isNotAllowDragDirection(int direction) {
        return (mDirectionMask & direction) == 0;
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
        int cl = marginLayoutParams.leftMargin ;
        int ct = marginLayoutParams.topMargin ;
        int cr = cl + mContentView.getMeasuredWidth() - marginLayoutParams.leftMargin - marginLayoutParams.rightMargin;
        int cb = ct + mContentView.getMeasuredHeight();
        mContentView.layout(cl, ct, cr, cb);
        mOriginX = mContentView.getLeft();
        mOriginY = mContentView.getTop();
        MarginLayoutParams otherParams;

        int expand = 0;

        /**mLeftView*/
        if (null != mLeftView) {
            otherParams = (MarginLayoutParams) mLeftView.getLayoutParams();
            cl = mContentView.getLeft() - marginLayoutParams.leftMargin - (otherParams.leftMargin + mLeftView.getMeasuredWidth() + otherParams.rightMargin);
            ct = mContentView.getTop() + otherParams.topMargin;
            cr = mContentView.getLeft() - marginLayoutParams.leftMargin - otherParams.rightMargin;
            cb = mContentView.getBottom() - otherParams.bottomMargin;
            mLeftView.layout(cl, ct, cr, cb);
        }
        /**mRightView*/
        if (null != mRightView) {
            otherParams = (MarginLayoutParams) mRightView.getLayoutParams();
            cl = mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin ;
            ct = mContentView.getTop() + otherParams.topMargin;
            cr = mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin + mRightView.getMeasuredWidth() + otherParams.rightMargin;
            cb = mContentView.getBottom() - otherParams.bottomMargin;
            mRightView.layout(cl, ct, cr, cb);
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
            mDragOffset = dx != 0 ? Math.abs(left) : Math.abs(top);
            MarginLayoutParams marginLayoutParams;
            marginLayoutParams = (MarginLayoutParams) mContentView.getLayoutParams();
            switch (mCurrentDirection) {
                case LEFT:
                case RIGHT:
                    layoutLeftAndRight(marginLayoutParams);
                    break;
                case TOP:
                case BOTTOM:
                    layoutTopAndBottom(marginLayoutParams);
                    break;
                case NONE:
                    layoutLeftAndRight(marginLayoutParams);
                    layoutTopAndBottom(marginLayoutParams);
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
            int leftBounds, rightBounds, result;
            if (isAllowDragDirection(LEFT)
                    && !canScrollRight(mContentView)
                    && left >= mOriginX
                    && mCurrentDirection == LEFT
                    ) {
                leftBounds = getPaddingLeft();
                rightBounds = getViewHorizontalDragRange(child);
                result = Math.min(Math.max(left, leftBounds), rightBounds) ;
                result -= (int)(dx * damping);
                return result;
            }
            if (isAllowDragDirection(RIGHT)
                    && !canScrollLeft(mContentView)
                    && left <= mOriginX
                    && mCurrentDirection == RIGHT) {
                leftBounds = -getViewHorizontalDragRange(child);
                rightBounds = getPaddingLeft();
                result = Math.min(Math.max(left, leftBounds), rightBounds);
                result -= (int)(dx * damping);
                return result;
            }
            return mOriginX;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int topBounds, bottomBounds, result;
            if (isAllowDragDirection(TOP)
                    && !canScrollBottom(child)
                    && top >= mOriginY
                    && mCurrentDirection == TOP) {
                topBounds = getPaddingTop();
                bottomBounds = getViewVerticalDragRange(child);
                result = Math.min(Math.max(top, topBounds), bottomBounds);
                result -= (int)(dy * damping);
                return result;
            }
            if (isAllowDragDirection(BOTTOM)
                    && !canScrollTop(child)
                    && top <= mOriginY
                    && mCurrentDirection == BOTTOM) {
                topBounds = -getViewVerticalDragRange(child);
                bottomBounds = getPaddingTop();
                result = Math.min(Math.max(top, topBounds), bottomBounds);
                result -= (int)(dy * damping);
                return result;
            }
            return mOriginY;
        }
    }

    private void layoutTopAndBottom(MarginLayoutParams marginLayoutParams) {
        MarginLayoutParams otherParams;
        if (null != mTopView) {
            otherParams = (MarginLayoutParams) mTopView.getLayoutParams();
            mTopView.layout(mTopView.getLeft(),
                    mContentView.getTop() - marginLayoutParams.topMargin - mTopView.getMeasuredHeight() - otherParams.topMargin - otherParams.bottomMargin,
                    mTopView.getRight(),
                    mContentView.getTop() - marginLayoutParams.topMargin - otherParams.bottomMargin);
        }
        if (null != mBottomView) {
            otherParams = (MarginLayoutParams) mBottomView.getLayoutParams();
            mBottomView.layout(mBottomView.getLeft(),
                    mContentView.getBottom() + marginLayoutParams.bottomMargin + otherParams.topMargin,
                    mBottomView.getRight(),
                    mContentView.getBottom() + marginLayoutParams.bottomMargin + otherParams.topMargin + mBottomView.getMeasuredHeight() + otherParams.bottomMargin);
        }
    }

    private void layoutLeftAndRight(MarginLayoutParams marginLayoutParams) {
        MarginLayoutParams otherParams;
        if (null != mLeftView){
            otherParams = (MarginLayoutParams) mLeftView.getLayoutParams();
            mLeftView.layout(
                    mContentView.getLeft() - marginLayoutParams.leftMargin - (otherParams.leftMargin + mLeftView.getMeasuredWidth() + otherParams.rightMargin),
                    mLeftView.getTop(),
                    mContentView.getLeft() - marginLayoutParams.leftMargin - otherParams.rightMargin,
                    mLeftView.getBottom());
        }
        if (null != mRightView) {
            otherParams = (MarginLayoutParams) mRightView.getLayoutParams();
            mRightView.layout(mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin,
                    mRightView.getTop(),
                    mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin + mRightView.getMeasuredWidth() + otherParams.rightMargin,
                    mRightView.getBottom());
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
        calculateForCurrentDirection(event);
        if (isEnabled()) {
            handled = mViewDragHelper.shouldInterceptTouchEvent(event);
        } else {
            mViewDragHelper.cancel();
        }
        if (!handled) mCurrentDirection = NONE;
        return handled || super.onInterceptTouchEvent(event);
    }

    private void calculateForCurrentDirection(MotionEvent event) {
        if (isEnabled()){
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
                        mCurrentDirection = Math.abs(slope) >= 1f ? (mTouchY >= downY ? TOP : BOTTOM) : (mTouchX >= downX ? LEFT : RIGHT);
                        break;
                }
                Log.e("zhou", "++++++++++mCurrentDirection++++++++++++++++++"+mCurrentDirection);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        calculateForCurrentDirection(event);
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
