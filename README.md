# Multi-SwipeToRefreshLayout
多方向支持刷新布局

## 更新（Update）
_**2017-2-28**_
 * 提交OverScrollLayout，ScrollView版已测试No problem



## 关于（About）
初衷（Intention）

-->想要实现一个支持多方向的多功能刷新布局，解决刷新布局在方向上的单一问题
  * 可以OverScroll，可以定制OverScroll状态下显示的布局
  * 可以是抽屉（反正我喜欢显示内容多，菜单没事儿显示那么多干嘛）
  * 可以刷新，可以定制刷新状态下显示的布局

怎么做（How to do）：

考虑到功能有点多，可能毅力不行，拆分下，做成两到三个项目，而且方便维护不是

那么（So）：

事要一件一件做，饭要一口一口吃。


## 使用（Usage）

* OverScrollLayout

        <lib.phenix.com.swipetorefresh.OverScrollLayout
            android:background="@color/colorAccent"
            app:swipeDirection="left|top|right|bottom"
            app:contentView="@layout/layout_example"
            app:topView="@layout/layout_horizontal"
            //app:rightView="@layout/layout_vertical"  空View实现
            app:bottomView="@layout/layout_horizontal"
            app:leftView="@layout/layout_vertical"
            android:layout_height="match_parent"
            android:layout_width="match_parent">
        </lib.phenix.com.swipetorefresh.OverScrollLayout>

## 效果

* OverScrollLayout

![OverScrollLayout效果gif](screenshots/overscroll.gif)

