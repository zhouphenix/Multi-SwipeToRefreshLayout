package lib.phenix.com.views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhouphenix on 2017-2-25.
 */

public class SimplePagerAdapter extends FragmentPagerAdapter {

    List<Class<? extends Fragment>> classes;

    public SimplePagerAdapter(FragmentManager fm, List<Class<? extends Fragment>> classes) {
        super(fm);
        this.classes = classes;
    }

    public SimplePagerAdapter(FragmentManager fm, Class<? extends Fragment>... classes) {
        super(fm);
        this.classes = new ArrayList<>();
        this.classes.addAll(Arrays.asList(classes));
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return classes.get(position).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        return classes.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return classes.get(position).getSimpleName();
    }


}
