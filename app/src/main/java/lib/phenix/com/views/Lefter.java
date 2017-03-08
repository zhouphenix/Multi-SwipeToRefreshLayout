package lib.phenix.com.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import lib.phenix.com.swipetorefresh.OnRefreshListener;
import lib.phenix.com.swipetorefresh.SwipeToRefreshLayout;

public class Lefter extends FrameLayout implements OnRefreshListener {


    private Animation rotate_left;
    private Animation rotate_right;
    private Animation rotate_infinite;
    private TextView textView;
    private View arrowIcon;
    private View successIcon;
    private View loadingIcon;

    private boolean isPull = true;

    public Lefter(Context context) {
        this(context, null);
    }

    public Lefter(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 初始化动画
        rotate_left = AnimationUtils.loadAnimation(context , R.anim.rotate_up);
        rotate_right = AnimationUtils.loadAnimation(context , R.anim.rotate_down);

        rotate_infinite = AnimationUtils.loadAnimation(context , R.anim.rotate_infinite);

        inflate(context, R.layout.lefter_qq, this);

        textView = (TextView) findViewById(R.id.text);
        arrowIcon = findViewById(R.id.arrowIcon);
        arrowIcon.setRotation(-90);
        successIcon = findViewById(R.id.successIcon);
        loadingIcon = findViewById(R.id.loadingIcon);
    }

    @Override
    public void onIdle() {
        textView.setText("下拉刷新");
        successIcon.setVisibility(INVISIBLE);
        arrowIcon.setVisibility(VISIBLE);
        arrowIcon.clearAnimation();
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
        isPull = true;
    }

    @Override
    public void onDragging(){
    }

    @Override
    public void onLoading() {
        arrowIcon.setVisibility(INVISIBLE);
        loadingIcon.setVisibility(VISIBLE);
        textView.setText("正在刷新...");
        arrowIcon.clearAnimation();
        loadingIcon.startAnimation(rotate_infinite);
    }

    @Override
    public void onSettling() {
    }

    @Override
    public void onPositionChange(@SwipeToRefreshLayout.SwipeDirection int direction,
                                 @SwipeToRefreshLayout.State int state,
                                 int refreshPoint, int currentX, int currentY, int lastX, int lastY) {

        if (state == SwipeToRefreshLayout.DRAGGING) {
            // 往上拉
            if (!isPull && currentX < refreshPoint) {
                textView.setText("下拉刷新");
                isPull = true;
                arrowIcon.clearAnimation();
                arrowIcon.startAnimation(rotate_right);
                // 往下拉
            } else if (isPull && currentX > refreshPoint) {
                textView.setText("释放立即刷新");
                isPull = false;
                arrowIcon.clearAnimation();
                arrowIcon.startAnimation(rotate_left);
            }
        }
    }


    @Override
    public void onCompleted() {
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
        successIcon.setVisibility(VISIBLE);
        textView.setText("刷新成功");
    }
}
