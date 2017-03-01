# Multi-SwipeToRefreshLayout
多方向支持刷新布局

## 更新（Update）
_**2017-2-28**_
 * 提交了WebView，ViewPager Demo及效果,准备发布OverScrollLayout，下一版做刷新

_**2017-2-28**_
 * 提交OverScrollLayout，ScrollView版已测试No problem
 * 适配了下普通布局、RecyclerView、WebView、ViewPager（左右），效果都OK



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

        <lib.phenix.com.swipetorefresh.OverScrollLayout xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:app="http://schemas.android.com/apk/res-auto"
            android:id="@+id/overscroll"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:bottomView="@layout/layout_horizontal"
            app:contentLayoutId="@+id/scroll" //主布局id
            app:layout_behavior="@string/appbar_scrolling_view_behavior"
            app:leftView="@layout/layout_vertical"
            //app:rightView="@layout/layout_vertical" 空View实现
            app:swipeDirection="left|top|right|bottom"
            app:topView="@layout/layout_horizontal">

            <ScrollView
                android:id="@+id/scroll"
                android:layout_width="match_parent"
                android:layout_height="wrap_content">
            </ScrollView>
        </lib.phenix.com.swipetorefresh.OverScrollLayout>

## 效果

* OverScrollLayout

![OverScrollLayout效果gif](screenshots/overscroll.gif)

![OverScrollLayout效果2gif](screenshots/overscroll2.gif)

