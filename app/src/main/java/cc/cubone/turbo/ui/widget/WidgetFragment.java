package cc.cubone.turbo.ui.widget;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import cc.cubone.turbo.R;
import cc.cubone.turbo.core.view.TabFragmentPagerAdapter;
import cc.cubone.turbo.ui.base.TabSightFragment;
import cc.cubone.turbo.ui.widget.custom.CustomFragment;
import cc.cubone.turbo.ui.widget.design.DesignFragment;
import cc.cubone.turbo.ui.widget.support.SupportFragment;

public class WidgetFragment extends TabSightFragment {

    public WidgetFragment() {
    }

    public static WidgetFragment newInstance() {
        return new WidgetFragment();
    }

    @Override
    public void onViewPrepared(TabLayout tabLayout, ViewPager viewPager) {
        SupportPagerAdapter adapter = new SupportPagerAdapter(getChildFragmentManager(),
                getActivity());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);

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
                case 0: return SupportFragment.newInstance();
                case 1: return DesignFragment.newInstance();
                case 2: return CustomFragment.newInstance();
                default: return null;
            }
        }

        @Override
        public CharSequence getTabText(int position) {
            switch (position) {
                case 0: return "Recycler";
                case 1: return "Design";
                case 2: return "Custom";
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
