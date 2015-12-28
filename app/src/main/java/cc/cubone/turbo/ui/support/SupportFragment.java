package cc.cubone.turbo.ui.support;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import cc.cubone.turbo.R;
import cc.cubone.turbo.ui.ColorPageFragment;
import cc.cubone.turbo.ui.base.BaseTabFragment;
import cc.cubone.turbo.ui.support.recycler.RecyclerFragment;
import cc.cubone.turbo.view.TabFragmentPagerAdapter;

import static cc.cubone.turbo.ui.ColorPageFragment.PINK;
import static cc.cubone.turbo.ui.ColorPageFragment.PURPLE;

/**
 * Fragment for practicing new APIs in support libraries.
 *
 * <p>See `Support.md` for more introductions.
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
                case 0: return RecyclerFragment.newInstance();
                case 1: return ColorPageFragment.newInstance(1, PINK);
                case 2: return ColorPageFragment.newInstance(2, PURPLE);
                default: return null;
            }
        }

        @Override
        public CharSequence getTabText(int position) {
            switch (position) {
                case 0: return "Recycler";
                default: return "Tab " + position;
            }
        }

        @Override
        public int getTabIcon(int position) {
            /*switch (position) {
                case 0: return R.drawable.ic_apps;
                case 1: return R.drawable.ic_apps;
                case 2: return R.drawable.ic_apps;
                default: return 0;
            }*/
            return 0;
        }
    }

}
