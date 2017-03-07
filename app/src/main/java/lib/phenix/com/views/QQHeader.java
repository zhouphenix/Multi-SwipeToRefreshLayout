package lib.phenix.com.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import lib.phenix.com.swipetorefresh.OnRefreshListener;
import lib.phenix.com.swipetorefresh.SwipeToRefreshLayout;

public class QQHeader extends FrameLayout implements OnRefreshListener {


    private Animation rotate_up;
    private Animation rotate_down;
    private Animation rotate_infinite;
    private TextView textView;
    private View arrowIcon;
    private View successIcon;
    private View loadingIcon;

    private boolean isPull = true;

    public QQHeader(Context context) {
        this(context, null);
    }

    public QQHeader(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化动画
        rotate_up = AnimationUtils.loadAnimation(context , R.anim.rotate_up);
        rotate_down = AnimationUtils.loadAnimation(context , R.anim.rotate_down);
        rotate_infinite = AnimationUtils.loadAnimation(context , R.anim.rotate_infinite);

        inflate(context, R.layout.header_qq, this);

        textView = (TextView) findViewById(R.id.text);
        arrowIcon = findViewById(R.id.arrowIcon);
        successIcon = findViewById(R.id.successIcon);
        loadingIcon = findViewById(R.id.loadingIcon);
    }

    @Override
    public void onIdle() {
        Log.i("zhou", "---------onIdle---------------");
        textView.setText("下拉刷新");
        successIcon.setVisibility(INVISIBLE);
        arrowIcon.setVisibility(VISIBLE);
        arrowIcon.clearAnimation();
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
        isPull = true;
    }

    @Override
    public void onDragging() {
        Log.i("zhou", "---------onDragging---------------");
    }

    @Override
    public void onLoading() {
        Log.i("zhou", "---------onLoading---------------");
        arrowIcon.setVisibility(INVISIBLE);
        loadingIcon.setVisibility(VISIBLE);
        textView.setText("正在刷新...");
        arrowIcon.clearAnimation();
        loadingIcon.startAnimation(rotate_infinite);
    }

    @Override
    public void onSettling() {
        Log.i("zhou", "===========onSettling==================");
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
    }

    @Override
    public void onPositionChange(@SwipeToRefreshLayout.SwipeDirection int direction,
                                 @SwipeToRefreshLayout.State int state,
                                 int refreshPoint, int currentX, int currentY, int lastX, int lastY) {
        // 往上拉
        if (currentY <= refreshPoint) {
            if (!isPull && state == SwipeToRefreshLayout.DRAGGING) {
                Log.i("zhou", "===========下拉刷新==================");
                textView.setText("下拉刷新");
                isPull = true;
                arrowIcon.clearAnimation();
                arrowIcon.startAnimation(rotate_down);
            }
            // 往下拉
        } else {
            if (isPull && state == SwipeToRefreshLayout.DRAGGING) {
                Log.i("zhou", "===========释放立即刷新==================");
                textView.setText("释放立即刷新");
                isPull = false;
                arrowIcon.clearAnimation();
                arrowIcon.startAnimation(rotate_up);
            }
        }


    }


    @Override
    public void onCompleted() {
        Log.i("zhou", "===========刷新成功==================");
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
        successIcon.setVisibility(VISIBLE);
        textView.setText("刷新成功");
    }
}
