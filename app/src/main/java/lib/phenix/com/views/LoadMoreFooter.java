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

public class LoadMoreFooter extends FrameLayout implements OnRefreshListener {


    private Animation rotate_up;
    private Animation rotate_down;
    private Animation rotate_infinite;
    private TextView textView;
    private View arrowIcon;
    private View successIcon;
    private View loadingIcon;

    private boolean isPull;

    public LoadMoreFooter(Context context) {
        this(context, null);
    }

    public LoadMoreFooter(Context context, AttributeSet attrs) {
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
        textView.setText("上拉加载更多");
        successIcon.setVisibility(INVISIBLE);
        arrowIcon.setVisibility(VISIBLE);
        arrowIcon.clearAnimation();
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
        isPull = false;
    }

    @Override
    public void onDragging() {
    }

    @Override
    public void onLoading() {
        arrowIcon.setVisibility(INVISIBLE);
        loadingIcon.setVisibility(VISIBLE);
        textView.setText("正在加载...");
        arrowIcon.clearAnimation();
        loadingIcon.startAnimation(rotate_infinite);
    }

    @Override
    public void onSettling() {
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
    }

    @Override
    public void onPositionChange(@SwipeToRefreshLayout.SwipeDirection int direction,
                                 @SwipeToRefreshLayout.State int state,
                                 int refreshPoint, int currentX, int currentY, int lastX, int lastY) {
        // 往上拉
        if (currentY >= refreshPoint) {
            if (!isPull && state == SwipeToRefreshLayout.DRAGGING) {
                textView.setText("上拉加载");
                isPull = true;
                arrowIcon.clearAnimation();
                arrowIcon.startAnimation(rotate_up);
            }
            // 往下拉
        } else {
            if (isPull && state == SwipeToRefreshLayout.DRAGGING) {
                textView.setText("释放立即加载");
                isPull = false;
                arrowIcon.clearAnimation();
                arrowIcon.startAnimation(rotate_down);
            }
        }


    }


    @Override
    public void onCompleted() {
        loadingIcon.setVisibility(INVISIBLE);
        loadingIcon.clearAnimation();
        successIcon.setVisibility(VISIBLE);
        textView.setText("加载成功");
    }
}
