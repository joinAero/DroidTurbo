package cc.eevee.turbo.ui.widget;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import cc.eevee.turbo.R;
import cc.eevee.turbo.core.view.TabFragmentPagerAdapter;
import cc.eevee.turbo.ui.base.TabSightFragment;
import cc.eevee.turbo.ui.widget.custom.CustomFragment;
import cc.eevee.turbo.ui.widget.design.DesignFragment;
import cc.eevee.turbo.ui.widget.hardware.HardwareFragment;
import cc.eevee.turbo.ui.widget.support.SupportFragment;

/**
 * Fragment for practicing support and design libraries.
 *
 * <p>See `Widget.md` for more introductions.
 */
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

        private final int PAGE_COUNT = 4;

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
                case 2: return HardwareFragment.newInstance();
                case 3: return CustomFragment.newInstance();
                default: return null;
            }
        }

        @Override
        public CharSequence getTabText(int position) {
            switch (position) {
                case 0: return "Support";
                case 1: return "Design";
                case 2: return "Hardware";
                case 3: return "Custom";
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
