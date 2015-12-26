package cc.cubone.turbo.ui.support;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.cubone.turbo.R;
import cc.cubone.turbo.ui.test.TestFragment;

/**
 * Fragment for practising new APIs in support libraries.
 *
 * <p>Reference:
 * <ul>
 * <li><a href="http://developer.android.com/tools/support-library/features.html">Support Library Features</a>
 * <li><a href="https://guides.codepath.com/android/">CodePath Android Cliffnotes</a>
 * </ul>
 */
public class SupportFragment extends Fragment {

    public SupportFragment() {
    }

    public static SupportFragment newInstance() {
        return new SupportFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_support, container, false);

        ViewPager pager = (ViewPager) v.findViewById(R.id.pager);
        pager.setAdapter(new SupportPagerAdapter(getChildFragmentManager()));

        TabLayout tabs = (TabLayout) v.findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);

        return v;
    }

    public static class SupportPagerAdapter extends FragmentPagerAdapter {

        private final int PAGE_COUNT = 3;

        public SupportPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return TestFragment.newInstance(0, 0xFFE57373);
                case 1: return TestFragment.newInstance(1, 0xFFF06292);
                case 2: return TestFragment.newInstance(2, 0xFFBA68C8);
                default: return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Tab " + position;
        }

    }

}
