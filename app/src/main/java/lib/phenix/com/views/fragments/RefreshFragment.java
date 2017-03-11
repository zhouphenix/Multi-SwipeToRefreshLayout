package lib.phenix.com.views.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lib.phenix.com.swipetorefresh.SwipeToRefreshLayout;
import lib.phenix.com.views.Lefter;
import lib.phenix.com.views.LoadMoreFooter;
import lib.phenix.com.swipetorefresh.MaterialRefreshHeader;
import lib.phenix.com.views.R;
import lib.phenix.com.views.Righter;

import static lib.phenix.com.views.R.id.recyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RefreshFragment extends Fragment {

    RecyclerView recycler;
    SwipeToRefreshLayout refresh;

    public RefreshFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_refresh, container, false);
        recycler = (RecyclerView) v.findViewById(recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(new MyAdapter());
        refresh = (SwipeToRefreshLayout) v.findViewById(R.id.refresh);

        MaterialRefreshHeader header = new MaterialRefreshHeader(getActivity());
        header.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));

        header.showProgressArrow(true);
        header.setProgressSize(200);
        header.setProgressColors(new int[]{Color.RED, Color.BLACK, Color.YELLOW});
        header.setProgressStokeWidth(3);
        header.setTextType(1);
        header.setProgressValue(0);
        header.setProgressValueMax(100);
        header.setIsProgressBg(true);
        header.setProgressBg(Color.WHITE);

//        refresh.setTopView(new QQHeader(getActivity()));
        refresh.setTopView(header);
        refresh.setOnRefreshCallback(new SwipeToRefreshLayout.OnRefreshCallback() {
            @Override
            public void onRefresh(final int direction) {
                refresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.refreshCompleted(500);
                    }
                }, 3000);
            }
        });

        refresh.setBottomView(new LoadMoreFooter(getActivity()));
        refresh.setLeftView(new Lefter(getActivity()));
        refresh.setRightView(new Righter(getActivity()));
        return v;
    }
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
    {
        List<String> mDatas;
        public MyAdapter() {
            mDatas = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                mDatas.add("position->"+i);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,200));
            return new MyViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position)
        {
            holder.tv.setText(mDatas.get(position));
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position){
                        case 0:
                            refresh.expandLeft();
                            break;
                        case 1:
                            refresh.expandTop();
                            break;
                        case 2:
                            refresh.expandRight();
                            break;
                        case 3:
                            refresh.expandBottom();
                            break;

                    }
                }
            });
        }

        @Override
        public int getItemCount()
        {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder
        {

            TextView tv;

            public MyViewHolder(View view)
            {
                super(view);
                tv = (TextView)view;
            }
        }
    }

}
