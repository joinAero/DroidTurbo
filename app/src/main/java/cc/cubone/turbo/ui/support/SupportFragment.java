package cc.cubone.turbo.ui.support;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import cc.cubone.turbo.R;
import cc.cubone.turbo.base.BaseTabFragment;
import cc.cubone.turbo.test.TestFragment;
import cc.cubone.turbo.view.TabFragmentPagerAdapter;

import static cc.cubone.turbo.test.TestFragment.PINK;
import static cc.cubone.turbo.test.TestFragment.PURPLE;
import static cc.cubone.turbo.test.TestFragment.RED;

/**
 * Fragment for practicing new APIs in support libraries.
 *
 * <p>Reference:
 * <ul>
 * <li><a href="http://developer.android.com/tools/support-library/features.html">Support Library Features</a>
 * <li><a href="https://guides.codepath.com/android/">CodePath Android Cliffnotes</a>
 * </ul>
 */
public class SupportFragment extends BaseTabFragment {

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
    public void onLayoutCreated(TabLayout tabLayout, ViewPager viewPager,
                                @Nullable Bundle savedInstanceState) {
        SupportPagerAdapter adapter = new SupportPagerAdapter(getChildFragmentManager(),
                getActivity());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        // `customTabViews` must be called after `setupWithViewPager`
        adapter.customTabViews(tabLayout);
    }

    public static class SupportPagerAdapter extends TabFragmentPagerAdapter {

        private final int PAGE_COUNT = 3;

        public SupportPagerAdapter(FragmentManager fm, Context ctx) {
            super(fm, ctx, R.layout.tab_custom);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return TestFragment.newInstance(0, RED);
                case 1: return TestFragment.newInstance(1, PINK);
                case 2: return TestFragment.newInstance(2, PURPLE);
                default: return null;
            }
        }

        @Override
        public int getTabIcon(int position) {
            switch (position) {
                case 0: return android.R.drawable.ic_dialog_dialer;
                case 1: return android.R.drawable.ic_dialog_map;
                case 2: return android.R.drawable.ic_dialog_info;
                default: return 0;
            }
        }
    }

}
