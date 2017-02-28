package lib.phenix.com.views.fragments;


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

import lib.phenix.com.views.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerFragment extends Fragment {

    RecyclerView recycler;

    public RecyclerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_recycler, container, false);
        recycler = (RecyclerView) v.findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(new HomeAdapter());
        return v;
    }
    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>
    {
        List<String> mDatas;
        public HomeAdapter() {
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
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            holder.tv.setText(mDatas.get(position));
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
