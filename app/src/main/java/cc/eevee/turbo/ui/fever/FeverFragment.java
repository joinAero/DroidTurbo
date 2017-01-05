package cc.eevee.turbo.ui.fever;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import cc.eevee.turbo.R;
import cc.eevee.turbo.core.view.TabFragmentPagerAdapter;
import cc.eevee.turbo.ui.ColorPageFragment;
import cc.eevee.turbo.ui.base.TabSightFragment;
import cc.eevee.turbo.ui.fever.gles.GlesFragment;
import cc.eevee.turbo.ui.fever.ocv.OcvFragment;

import static cc.eevee.turbo.ui.ColorPageFragment.PURPLE;

/**
 * Fragment for practicing fever things, or integrate simple demos.
 *
 * <p>See `Fever.md` for more introductions.
 */
public class FeverFragment extends TabSightFragment {

    public FeverFragment() {
    }

    public static FeverFragment newInstance() {
        return new FeverFragment();
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
                case 0: return GlesFragment.newInstance();
                case 1: return OcvFragment.newInstance();
                case 2: return ColorPageFragment.newInstance(2, PURPLE);
                default: return null;
            }
        }

        @Override
        public CharSequence getTabText(int position) {
            switch (position) {
                case 0: return "OpenGL ES";
                case 1: return "OpenCV";
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
