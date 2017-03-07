package lib.phenix.com.swipetorefresh;

/**
 * Created by zhouphenix on 2017-3-6.
 */

public interface OnRefreshListener {
    void onIdle();
    void onDragging();
    void onLoading();
    void onSettling();
    void onCompleted();

    /**
     * 监听位置改变
     * @param direction 当前swipe方向
     * @param state 刷新状态
     * @param refreshPoint 刷新点，通常是实现OnRefreshListener接口view的宽或高
     * @param currentX 拖拽View的左上顶点x坐标
     * @param currentY 拖拽View的左上顶点Y坐标
     * @param lastX 拖拽View的MotionEvent.ACTION_DOWN时左上顶点x坐标
     * @param lastY 拖拽View的MotionEvent.ACTION_DOWN时左上顶点y坐标
     */
    void onPositionChange(@SwipeToRefreshLayout.SwipeDirection int direction,
                          @SwipeToRefreshLayout.State int state,
                          int refreshPoint,
                          int currentX,int currentY,
                          int lastX,int lastY
                          );
}
