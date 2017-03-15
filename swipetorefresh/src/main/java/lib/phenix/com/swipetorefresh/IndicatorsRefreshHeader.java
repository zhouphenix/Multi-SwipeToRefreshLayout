package lib.phenix.com.swipetorefresh;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import lib.phenix.com.swipetorefresh.progressindicator.AVLoadingIndicatorView;
import lib.phenix.com.swipetorefresh.progressindicator.indicator.BaseIndicatorController;

/**
 * @author zhouphenix on 2017-3-12.
 */

public class IndicatorsRefreshHeader extends RelativeLayout implements OnRefreshListener{

    AVLoadingIndicatorView mIndicatorView;

    TextView mText;

    public IndicatorsRefreshHeader(Context context) {
        super(context);
        init(context);
    }


    public IndicatorsRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IndicatorsRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IndicatorsRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    private void init(Context context) {
        if (isInEditMode()) return;
        mIndicatorView = new AVLoadingIndicatorView(context);
        mIndicatorView.setIndicatorId(AVLoadingIndicatorView.Pacman);
        mIndicatorView.setIndicatorColor(Color.WHITE);
        mIndicatorView.setId(R.id.left);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_IN_PARENT);
        addView(mIndicatorView, layoutParams);

        mText = new TextView(context);
        mText.setEms(6);
        mText.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams textLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayoutParams.addRule(CENTER_IN_PARENT);
        textLayoutParams.addRule(BELOW, mIndicatorView.getId());
        addView(mText, textLayoutParams);
    }

    @Override
    public void onIdle() {
        mText.setText("滑动刷新...");
    }

    @Override
    public void onDragging() {
        mIndicatorView.setAnimationStatus(BaseIndicatorController.AnimStatus.END);
        mText.setText("滑动刷新...");
    }

    @Override
    public void onLoading() {
        mIndicatorView.setAnimationStatus(BaseIndicatorController.AnimStatus.START);
        mText.setText("正在刷新...");
    }

    @Override
    public void onSettling() {

    }

    @Override
    public void onCompleted() {
        mIndicatorView.setAnimationStatus(BaseIndicatorController.AnimStatus.END);
        mText.setText("刷新完成");
    }

    @Override
    public void onPositionChange(@SwipeToRefreshLayout.SwipeDirection int direction, @SwipeToRefreshLayout.State int state, int refreshPoint, int range, int currentX, int currentY, int lastX, int lastY, float touchX, float touchY) {
        float fraction = currentX >= refreshPoint? 1: currentX * 1.0f/refreshPoint;
        ViewCompat.setScaleX(mIndicatorView, fraction);
        ViewCompat.setScaleY(mIndicatorView, fraction);
        ViewCompat.setRotation(mIndicatorView, currentX * 1.0f/refreshPoint * 360);
        ViewCompat.setAlpha(mIndicatorView, fraction);

        if (currentX < refreshPoint){
            mText.setText("拖拽刷新");
        }else{
            mText.setText("释放刷新");
        }
    }
}
