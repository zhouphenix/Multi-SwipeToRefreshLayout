package lib.phenix.com.views.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lib.phenix.com.views.R;
import lib.phenix.com.views.SimplePagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {


    public ViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_pager, container, false);
        ViewPager viewPager = (ViewPager) v.findViewById(R.id.pager);
        SimplePagerAdapter pagerAdapter = new SimplePagerAdapter(getChildFragmentManager(), ScrollFragment.class,RecyclerFragment.class,WebViewFragment.class);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }

}
