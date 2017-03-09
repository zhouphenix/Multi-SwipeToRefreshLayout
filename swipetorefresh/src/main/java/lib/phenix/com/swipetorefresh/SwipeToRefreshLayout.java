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

public class SwipeToRefreshLayout extends ViewGroup {

    public static final int NONE = 0;
    public static final int LEFT = 1;
    public static final int TOP = 1 << 1;
    public static final int RIGHT = 1 << 2;
    public static final int BOTTOM = 1 << 3;


    @IntDef({NONE,
            LEFT,
            TOP,
            RIGHT,
            BOTTOM})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SwipeDirection {
    }

    public static final int IDLE = 0;
    public static final int DRAGGING = 1;
    public static final int LOADING = 2;
    public static final int SETTLING = 3;
    public static final int COMPLETED = 4;

    @IntDef({IDLE,
            DRAGGING,
            LOADING,
            SETTLING,
            COMPLETED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {

    }

    @State
    int mState;

    private final ViewDragHelper mViewDragHelper;

    int mDirectionMask = TOP;

    @SwipeDirection
    int mCurrentDirection;

    boolean enableSwipe;


    int mOriginX;
    int mOriginY;

    /**
     * 阻尼因子
     */
    float mHorizontalFactor = 0.3f;
    float mVerticalFactor = 0.3f;
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

    private View mRefreshView;

    /**
     * 当前touch的坐标
     */
    private float mTouchX, mTouchY;
    /**
     * 记录MotionEvent.ACTION_DOWN的坐标
     */
    private float downX, downY;

    @SwipeDirection
    int mLockDirection;

    int lastDownLeft, lastDownTop;




    private Runnable mOnCompletedCallback =new  Runnable() {

        @Override
        public void run() {
            if (mViewDragHelper.smoothSlideViewTo(mContentView, mOriginX, mOriginY)) {
                ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
            }
        }
    };


    public SwipeToRefreshLayout(@NonNull Context context, @NonNull View contentView, int directionMask) {
        super(context);
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallback());
        mContentView = contentView;
        mDirectionMask = directionMask;
        enableSwipe = true;
    }

    public SwipeToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallback());

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SwipeToRefreshLayout);
        contentLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_contentLayoutId, View.NO_ID);
        int leftLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_leftView, View.NO_ID);
        int topLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_topView, View.NO_ID);
        int rightLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_rightView, View.NO_ID);
        int bottomLayoutId = ta.getResourceId(R.styleable.SwipeToRefreshLayout_bottomView, View.NO_ID);
        mDirectionMask = ta.getInt(R.styleable.SwipeToRefreshLayout_swipeDirection, mDirectionMask);
        mHorizontalFactor = ta.getFloat(R.styleable.SwipeToRefreshLayout_horizontalRangeFactor, 0.3f);
        mVerticalFactor = ta.getFloat(R.styleable.SwipeToRefreshLayout_verticalRangeFactor, 0.3f);
        ta.recycle();

        LayoutInflater inflater = LayoutInflater.from(context);

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

    public void setTopView(View view) {
        if (view != null && view != mTopView) {
            removeView(mTopView);
            MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
            if (null == layoutParams){
                layoutParams = new MarginLayoutParams(MarginLayoutParams.MATCH_PARENT,MarginLayoutParams.MATCH_PARENT );
                view.setLayoutParams(layoutParams);
            }
            mTopView = view;
            addView(mTopView);
        }
    }


    public void setBottomView(View view) {
        if (view != null && view != mBottomView) {
            removeView(mBottomView);
            MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
            if (null == layoutParams){
                layoutParams = new MarginLayoutParams(MarginLayoutParams.MATCH_PARENT,MarginLayoutParams.MATCH_PARENT );
                view.setLayoutParams(layoutParams);
            }
            mBottomView = view;
            addView(mBottomView);
        }
    }


    public void setLeftView(View view) {
        if (view != null && view != mLeftView) {
            removeView(mLeftView);
            MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
            if (null == layoutParams){
                layoutParams = new MarginLayoutParams(MarginLayoutParams.MATCH_PARENT,MarginLayoutParams.MATCH_PARENT );
                view.setLayoutParams(layoutParams);
            }
            mLeftView = view;
            addView(mLeftView);
        }
    }

    public void setRightView(View view) {
        if (view != null && view != mRightView) {
            removeView(mRightView);
            MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
            if (null == layoutParams){
                layoutParams = new MarginLayoutParams(MarginLayoutParams.MATCH_PARENT,MarginLayoutParams.MATCH_PARENT );
                view.setLayoutParams(layoutParams);
            }
            mRightView = view;
            addView(mRightView);
        }
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

    /**
     * 设置拖动百分比限制
     *
     * @param mHorizontalFactor
     */
    public void setHorizontalFactor(float mHorizontalFactor) {
        this.mHorizontalFactor = mHorizontalFactor;
    }
    /**
     * 设置拖动百分比限制
     *
     * @param mVerticalFactor
     */
    public void setVerticalFactor(float mVerticalFactor) {
        this.mVerticalFactor = mVerticalFactor;
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
     *
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

        int vWithSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int vHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        mTopView.measure(vWithSpec, vHeightSpec);
        mBottomView.measure(vWithSpec, vHeightSpec);

        int hWithSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int hHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        mLeftView.measure(hWithSpec, hHeightSpec);
        mRightView.measure(hWithSpec, hHeightSpec);

        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width, heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mState != IDLE) return; //解决header刷新，如setText()方法，造成重新调用onLayout方法，避免影响其他View
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
        /**mRightView*/
        if (null != mRightView) {
            otherParams = (MarginLayoutParams) mRightView.getLayoutParams();
            cl = mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin;
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
            int offset;
            switch (mCurrentDirection) {
                case LEFT:
                    offset = mContentView.getLeft() - mOriginX;
                    if (null != mLeftView && offset >= mLeftView.getWidth()
                            && mViewDragHelper.settleCapturedViewAt(mContentView.getLeft() - mLeftView.getLeft(), mOriginY)) {
                        ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                        mLockDirection = LEFT;
                    } else {
                        if (mViewDragHelper.settleCapturedViewAt(mOriginX, mOriginY)) {
                            ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                        }

                    }

                    break;
                case RIGHT:
                    offset = mOriginX - mContentView.getLeft();
                    if (null != mRightView && offset >= mRightView.getWidth()
                            && mViewDragHelper.settleCapturedViewAt(mContentView.getRight() - mRightView.getRight(), mOriginY)) {
                        ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                        mLockDirection = RIGHT;
                    } else {
                        if (mViewDragHelper.settleCapturedViewAt(mOriginX, mOriginY)) {
                            ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                        }

                    }
                    break;
                case TOP:
                    offset = mContentView.getTop() - mOriginY;
                    if (null != mTopView && offset >= mTopView.getHeight()
                            && mViewDragHelper.settleCapturedViewAt(mOriginX, mContentView.getTop() - mTopView.getTop())) {
                        ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                        mLockDirection = TOP;
                    } else {
                        if (mViewDragHelper.settleCapturedViewAt(mOriginX, mOriginY)) {
                            ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                        }

                    }
                    break;

                case BOTTOM:
                    offset = mOriginY - mContentView.getTop();
                    if (null != mBottomView && offset >= mBottomView.getHeight()
                            && mViewDragHelper.settleCapturedViewAt(mOriginX, mContentView.getBottom() - mBottomView.getBottom())) {
                        ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                        mLockDirection = BOTTOM;
                    } else {
                        if (mViewDragHelper.settleCapturedViewAt(mOriginX, mOriginY)) {
                            ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                        }

                    }

                    break;
            }
        }


        @Override
        public void onViewPositionChanged(final View changedView, int left, final int top, final int dx, final int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mDragOffset = dx != 0 ? Math.abs(left) : Math.abs(top);
            MarginLayoutParams marginLayoutParams  = (MarginLayoutParams) mContentView.getLayoutParams();
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
            if (null != mOnSwipeListener)
                mOnSwipeListener.onSwipe(mCurrentDirection,
                        mDragOffset,
                        dx != 0 ? getViewHorizontalDragRange(mContentView) : getViewVerticalDragRange(mContentView)
                );

        }

        @Override
        public void onViewDragStateChanged(final int state) {
            super.onViewDragStateChanged(state);

            Log.e("zhou", "==============onViewDragStateChanged==================" + state);
            if (mLastDragState == ViewDragHelper.STATE_SETTLING && state == ViewDragHelper.STATE_DRAGGING && mLockDirection == NONE) {
                if (mViewDragHelper.smoothSlideViewTo(mContentView, mOriginX, mOriginY)) {
                    ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                }
            }
            switch (state) {
                case ViewDragHelper.STATE_DRAGGING:
                    if (!(mState == COMPLETED || mState == LOADING)) {
                        changeState(DRAGGING);
                    }
                    break;
                case ViewDragHelper.STATE_IDLE:
                    if (mState != LOADING){
                        if (COMPLETED == mState && mViewDragHelper.smoothSlideViewTo(mContentView, mOriginX, mOriginY)){
                            ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                        }else{
                            changeState(
                                    mContentView.getTop() != mOriginY || mContentView.getLeft() != mOriginX ?
                                            LOADING : IDLE);
                        }
                    }

                    mCurrentDirection = NONE;
                    if (mState != LOADING && mContentView.getLeft() == mOriginX && mContentView.getTop() == mOriginY)
                        mLockDirection = NONE;
                    Log.e("zhou", "==onViewDragStateChanged===mLockDirection====" + mLockDirection);
                    Log.i("zhou", "***************************END*********************************************");
                    break;
                case ViewDragHelper.STATE_SETTLING:
                    if (!(mState == COMPLETED || mState == LOADING))changeState(SETTLING);
                    break;
            }
            mLastDragState = state;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) (mHorizontalDragRange * mHorizontalFactor);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return (int) (mVerticalDragRange * mVerticalFactor);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            int result = mOriginX;
            if (mLockDirection == NONE) {
                int leftBounds, rightBounds;
                if (isAllowDragDirection(LEFT)
                        && !canScrollRight(mContentView)
                        && left >= mOriginX
                        && mCurrentDirection == LEFT) {
                    mRefreshView = mLeftView;
                    leftBounds = getLeft();
                    rightBounds = getViewHorizontalDragRange(child);
                    result = Math.min(Math.max(left, leftBounds), rightBounds);
                }
                if (isAllowDragDirection(RIGHT)
                        && !canScrollLeft(mContentView)
                        && left <= mOriginX
                        && mCurrentDirection == RIGHT) {
                    mRefreshView = mRightView;
                    leftBounds = -getViewHorizontalDragRange(child);
                    rightBounds = getLeft();
                    result = Math.min(Math.max(left, leftBounds), rightBounds);
                }
            } else {
                if (mLockDirection == TOP || mLockDirection == BOTTOM) result = child.getLeft();
                else {
                    if (mCurrentDirection == TOP || mCurrentDirection == BOTTOM)
                        result = child.getLeft();
                    else {
                        if (mLockDirection == LEFT)
                            result = Math.max(mOriginX, Math.min(left, getViewHorizontalDragRange(child)));
                        else
                            result = Math.min(mOriginX, Math.max(left, -getViewHorizontalDragRange(child)));
                    }
                }

            }
            return result;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int result = mOriginY;
            if (mLockDirection == NONE) {
                int topBounds, bottomBounds;
                if (isAllowDragDirection(TOP)
                        && !canScrollBottom(child)
                        && top >= mOriginY
                        && mCurrentDirection == TOP) {
                    mRefreshView = mTopView;
                    topBounds = getTop();
                    bottomBounds = getViewVerticalDragRange(child);
                    result = Math.min(Math.max(top, topBounds), bottomBounds);
                }
                if (isAllowDragDirection(BOTTOM)
                        && !canScrollTop(child)
                        && top <= mOriginY
                        && mCurrentDirection == BOTTOM) {
                    mRefreshView = mBottomView;
                    topBounds = -getViewVerticalDragRange(child);
                    bottomBounds = getTop();
                    result = Math.min(Math.max(top, topBounds), bottomBounds);
                }
            } else {
                if (mLockDirection == LEFT || mLockDirection == RIGHT) result = child.getTop();
                else {
                    if (mCurrentDirection == LEFT || mCurrentDirection == RIGHT)
                        result = child.getTop();
                    else {
                        if (mLockDirection == TOP)
                            result = Math.max(mOriginY, Math.min(top, getViewVerticalDragRange(child)));
                        else
                            result = Math.min(mOriginY, Math.max(top, -getViewVerticalDragRange(child)));
                    }
                }

            }
            return result;
        }
    }

    private void changeState(int state) {
        if (null != mRefreshView && mRefreshView instanceof OnRefreshListener){
            this.mState = state;
            OnRefreshListener onRefresh = (OnRefreshListener) mRefreshView;
            switch (state) {
                case IDLE:
                    onRefresh.onIdle();
                    Log.i("zhou", "--------onIdle-----------");
                    break;
                case DRAGGING:
                    onRefresh.onDragging();
                    Log.i("zhou", "--------onDragging-----------");
                    break;
                case LOADING:
                    onRefresh.onLoading();
                    Log.i("zhou", "--------onLoading-----------");
                    if (null != mOnRefreshCallback)
                        mOnRefreshCallback.onRefresh(mLockDirection);
                    break;
                case SETTLING:
                    onRefresh.onSettling();
                    Log.i("zhou", "--------onSettling-----------");
                    break;
                case COMPLETED:
                    onRefresh.onCompleted();
                    Log.i("zhou", "--------onCompleted-----------");
                    break;
            }
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

            if (mTopView instanceof OnRefreshListener)
                ((OnRefreshListener) mTopView).onPositionChange(TOP, mState,
                        mTopView.getHeight(),
                        mContentView.getLeft(), mContentView.getTop(),
                        lastDownLeft, lastDownTop,
                        mTouchX, mTouchY
                );
        }
        if (null != mBottomView) {
            otherParams = (MarginLayoutParams) mBottomView.getLayoutParams();
            mBottomView.layout(mTopView.getLeft(),
                    mContentView.getBottom() + marginLayoutParams.bottomMargin + otherParams.topMargin,
                    mTopView.getRight(),
                    mContentView.getBottom() + marginLayoutParams.bottomMargin + otherParams.topMargin + mBottomView.getMeasuredHeight() + otherParams.bottomMargin);
            if (mBottomView instanceof OnRefreshListener)
                ((OnRefreshListener) mBottomView).onPositionChange(BOTTOM, mState,
                        -mBottomView.getHeight(),
                        mContentView.getLeft(), mContentView.getTop(),
                        lastDownLeft, lastDownTop,
                        mTouchX, mTouchY
                );

        }
    }

    private void layoutLeftAndRight(MarginLayoutParams marginLayoutParams) {
        MarginLayoutParams otherParams;
        if (null != mLeftView) {
            otherParams = (MarginLayoutParams) mLeftView.getLayoutParams();
            mLeftView.layout(
                    mContentView.getLeft() - marginLayoutParams.leftMargin - (otherParams.leftMargin + mLeftView.getMeasuredWidth() + otherParams.rightMargin),
                    mLeftView.getTop(),
                    mContentView.getLeft() - marginLayoutParams.leftMargin - otherParams.rightMargin,
                    mLeftView.getBottom());
            if (mLeftView instanceof OnRefreshListener)
                ((OnRefreshListener) mLeftView).onPositionChange(LEFT, mState,
                        mLeftView.getWidth(),
                        mContentView.getLeft(), mContentView.getTop(),
                        lastDownLeft, lastDownTop,
                        mTouchX, mTouchY
                );
        }
        if (null != mRightView) {
            otherParams = (MarginLayoutParams) mRightView.getLayoutParams();
            mRightView.layout(mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin,
                    mRightView.getTop(),
                    mContentView.getRight() + marginLayoutParams.rightMargin + otherParams.leftMargin + mRightView.getMeasuredWidth() + otherParams.rightMargin,
                    mRightView.getBottom());
            if (mRightView instanceof OnRefreshListener)
                ((OnRefreshListener) mRightView).onPositionChange(RIGHT, mState,
                        -mRightView.getWidth(),
                        mContentView.getLeft(), mContentView.getTop(),
                        lastDownLeft, lastDownTop,
                        mTouchX, mTouchY
                );
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
            calculateForCurrentDirection(event);
            handled = mContentView != null && mViewDragHelper.shouldInterceptTouchEvent(event);
        } else {
            mViewDragHelper.cancel();
        }
        if (!handled) {
            mCurrentDirection = NONE;
        }
        return handled || super.onInterceptTouchEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {//对于非scroll视图或者view（ViewGroup在onInterceptTouchEvent方法中判断）
            calculateForCurrentDirection(event);
        }
        mViewDragHelper.processTouchEvent(event);
        return true;
    }


    private void calculateForCurrentDirection(MotionEvent event) {
        mTouchX = event.getRawX();
        mTouchY = event.getRawY();
        final int action = event.getActionMasked();
        if (mCurrentDirection == NONE) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downX = mTouchX;
                    downY = mTouchY;
                    lastDownLeft = mContentView.getLeft();
                    lastDownTop = mContentView.getTop();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float slope = (mTouchY - downY) / (mTouchX - downX);
                    mCurrentDirection = Math.abs(slope) >= 1 ? (mTouchY >= downY ? TOP : BOTTOM) : (mTouchX >= downX ? LEFT : RIGHT);
                    Log.e("zhou", "++++++++++mCurrentDirection++++++++++++++++++" + mCurrentDirection);
                    break;
            }
        }
    }


    /**
     * 重置状态，外部调用的时候需要调用
     * @return true 有移动距离，反之，已经到达该点
     */
    public boolean reset() {
        boolean isReset = mViewDragHelper.smoothSlideViewTo(mContentView, mOriginX, mOriginY);
        if (isReset) {
            ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
        }
        changeState(IDLE);
        return isReset;
    }

    public void refreshCompleted(long delay) {
        if (mState == LOADING ){//只在刷新状态调用
            changeState(COMPLETED);
            postDelayed(mOnCompletedCallback, delay);
        }
    }

    public void expandLeft() {
        if (!isAllowDragDirection(LEFT))return;
        if(reset()){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    expandLeft();
                }
            }, 200);
        }else{
            if (null != mLeftView && mViewDragHelper.smoothSlideViewTo(mContentView, mContentView.getLeft() - mLeftView.getLeft(), mOriginY)) {
                ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                mLockDirection = LEFT;
                mCurrentDirection = LEFT;
                mRefreshView = mLeftView;
            }
        }

    }

    public void expandRight() {
        if (!isAllowDragDirection(RIGHT))return;
        if (reset()){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    expandRight();
                }
            }, 200);
        }else {
            if (null != mRightView && mViewDragHelper.smoothSlideViewTo(mContentView, mContentView.getRight() - mRightView.getRight(), mOriginY)) {
                ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                mLockDirection = RIGHT;
                mCurrentDirection = RIGHT;
                mRefreshView = mRightView;
            }
        }
    }

    public void expandTop() {
        if (!isAllowDragDirection(TOP))return;
        if (reset()){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    expandTop();
                }
            }, 200);
        }else {
            if (isAllowDragDirection(TOP) && null != mTopView && mViewDragHelper.smoothSlideViewTo(mContentView, mOriginX, mContentView.getTop() - mTopView.getTop())) {
                ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                mLockDirection = TOP;
                mCurrentDirection = TOP;
                mRefreshView = mTopView;
            }
        }

    }

    public void expandBottom() {
        if (!isAllowDragDirection(BOTTOM))return;
        if (reset()){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    expandBottom();
                }
            }, 200);
        }else {
            if (isAllowDragDirection(BOTTOM) && null != mBottomView && mViewDragHelper.smoothSlideViewTo(mContentView, mOriginX, mContentView.getBottom() - mBottomView.getBottom())) {
                ViewCompat.postInvalidateOnAnimation(SwipeToRefreshLayout.this);
                mLockDirection = BOTTOM;
                mCurrentDirection = BOTTOM;
                mRefreshView = mBottomView;
            }
        }
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


    OnSwipeListener mOnSwipeListener;

    public void setOnSwipeListener(OnSwipeListener mOnSwipeListener) {
        this.mOnSwipeListener = mOnSwipeListener;
    }

    public interface OnSwipeListener {
        void onSwipe(@SwipeDirection int direction, int distance, int max);
    }


    OnRefreshCallback mOnRefreshCallback;

    public void setOnRefreshCallback(OnRefreshCallback mOnRefreshCallback) {
        this.mOnRefreshCallback = mOnRefreshCallback;
    }

    public interface OnRefreshCallback {
        void onRefresh(@SwipeDirection int direction);
    }

}
