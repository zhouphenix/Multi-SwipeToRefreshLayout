package lib.phenix.com.swipetorefresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import lib.phenix.com.swipetorefresh.view.CircleProgressBar;
import lib.phenix.com.swipetorefresh.view.MaterialProgressDrawable;

/**
 * @author  zhouphenix on 2017-3-10.
 */

public class MaterialRefreshHeader extends RelativeLayout implements OnRefreshListener {

    private CircleProgressBar mProgressView;
    private MaterialProgressDrawable mProgressDrawable;
    private int progressTextColor;
    private int[] progress_colors;
    private int progressStokeWidth;
    private boolean isShowArrow, isShowProgressBg;
    private int progressValue, progressValueMax;
    private int textType;
    private int progressBg;
    private int progressSize;

    private float startAngle, endAngle;

    public MaterialRefreshHeader(Context context) {
        this(context, null);
    }

    public MaterialRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) return;
        setClipToPadding(false);
        setWillNotDraw(false);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mProgressView = new CircleProgressBar(getContext());
        RelativeLayout.LayoutParams params = new LayoutParams(progressSize, progressSize);
        params.addRule(CENTER_IN_PARENT);
        mProgressView.setLayoutParams(params);
        mProgressView.setColorSchemeColors(progress_colors);
        mProgressView.setProgressStokeWidth(progressStokeWidth);
        mProgressView.setShowArrow(isShowArrow);
        mProgressView.setShowProgressText(textType == 0);
        mProgressView.setTextColor(progressTextColor);
        mProgressView.setProgress(progressValue);
        mProgressView.setMax(progressValueMax);
        mProgressView.setCircleBackgroundEnabled(isShowProgressBg);
        mProgressView.setProgressBackGroundColor(progressBg);

        addView(mProgressView,params);
        mProgressDrawable = mProgressView.getProgressDrawable();
    }


    public void setProgressSize(int progressSize) {
        this.progressSize = progressSize;
    }

    public void setProgressBg(int progressBg) {
        this.progressBg = progressBg;
        if(mProgressView!=null)
            mProgressView.setProgressBackGroundColor(progressBg);
    }

    public void setIsProgressBg(boolean isShowProgressBg) {
        this.isShowProgressBg = isShowProgressBg;
        if(mProgressView!=null)
            mProgressView.setCircleBackgroundEnabled(isShowProgressBg);
    }

    public void setProgressTextColor(int textColor) {
        this.progressTextColor = textColor;
    }

    public void setProgressColors(int[] colors) {
        this.progress_colors = colors;
        if(mProgressView!=null)
            mProgressView.setColorSchemeColors(progress_colors);
    }

    public void setTextType(int textType) {
        this.textType = textType;
    }

    public void setProgressValue(int value) {
        this.progressValue = value;
        this.post(new Runnable() {
            @Override
            public void run() {
                if (mProgressView != null) {
                    mProgressView.setProgress(progressValue);
                }
            }
        });

    }

    public void setProgressValueMax(int value) {
        this.progressValueMax = value;
    }

    public void setProgressStokeWidth(int w) {
        this.progressStokeWidth = w;
        if(mProgressView!=null)
            mProgressView.setProgressStokeWidth(progressStokeWidth);
    }

    public void showProgressArrow(boolean isShowArrow) {
        this.isShowArrow = isShowArrow;
        if(mProgressView!=null)
            mProgressView.setShowArrow(isShowArrow);
    }

    @Override
    public void onIdle() {
        if (mProgressDrawable != null){
            mProgressDrawable.showArrow(true);
            mProgressDrawable.setStartEndTrim(0, 300f);

        }
        if (mProgressView != null) mProgressView.requestLayout();
    }

    @Override
    public void onDragging() {
        if (mProgressView != null) {
            setVisibility(VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (mProgressDrawable != null) {
            mProgressDrawable.start();
        }
    }

    @Override
    public void onSettling() {

    }

    @Override
    public void onCompleted() {
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
            mProgressDrawable.setStartEndTrim(0, 360f);
        }
    }

    @Override
    public void onPositionChange(@SwipeToRefreshLayout.SwipeDirection int direction,
                                 @SwipeToRefreshLayout.State int state,
                                 int refreshPoint, int range,
                                 int currentX, int currentY,
                                 int lastX, int lastY,
                                 float touchX, float touchY) {
        if (mProgressView != null) {
            mProgressDrawable.setProgressRotation(currentY *1.0f / range);
            float fraction = currentY >= refreshPoint? 1: currentY * 1.0f/refreshPoint;
            ViewCompat.setScaleX(mProgressView, fraction);
            ViewCompat.setScaleY(mProgressView, fraction);
            ViewCompat.setAlpha(mProgressView, fraction);
        }

    }
}
