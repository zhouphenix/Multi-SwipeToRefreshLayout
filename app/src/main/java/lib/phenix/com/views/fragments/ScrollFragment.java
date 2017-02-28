package lib.phenix.com.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lib.phenix.com.views.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScrollFragment extends Fragment {


    public ScrollFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_scroll, container, false);
    }

}
