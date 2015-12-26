package cc.cubone.turbo.ui.support;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cc.cubone.turbo.R;
import cc.cubone.turbo.base.BaseTabFragment;
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
    public void onTabCreated(TabLayout tabLayout, ViewPager viewPager,
                             @Nullable Bundle savedInstanceState) {
        SupportPagerAdapter adapter = new SupportPagerAdapter(getChildFragmentManager(),
                getActivity());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        // `customTabViews` must be called after `setupWithViewPager`
        adapter.customTabViews(tabLayout);
    }

    public static class SupportPagerAdapter extends FragmentPagerAdapter {

        private final int PAGE_COUNT = 3;

        private Context mContext;

        public SupportPagerAdapter(FragmentManager fm, Context ctx) {
            super(fm);
            mContext = ctx;
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
            return getTabText(position);
        }

        public CharSequence getTabText(int position) {
            return "Tab " + position;
        }

        @DrawableRes
        public int getTabIcon(int position) {
            switch (position) {
                case 0: return android.R.drawable.ic_dialog_dialer;
                case 1: return android.R.drawable.ic_dialog_map;
                case 2: return android.R.drawable.ic_dialog_info;
                default: return 0;
            }
        }

        public void customTabViews(TabLayout tabLayout) {
            // Iterate over all tabs and set the custom view
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                customTabView(tabLayout, i);
            }
        }

        public void customTabView(TabLayout tabLayout, int position) {
            TabLayout.Tab tab = tabLayout.getTabAt(position);
            if (tab == null) {
                return;
            }
            // `TabView` orientation are vertical in `TabLayout` which will raise the icon.
            //tab.setTag(getTabText(position)); // android.R.id.text1
            //tab.setIcon(getTabIcon(position)); // android.R.id.icon

            View v = LayoutInflater.from(mContext).inflate(R.layout.tab_custom, null);

            TextView textView = (TextView) v.findViewById(R.id.text);
            textView.setText(getTabText(position));

            ColorStateList tabTextColors = tabLayout.getTabTextColors();
            if (tabTextColors != null) {
                textView.setTextColor(tabTextColors);
            }

            ImageView iconView = (ImageView) v.findViewById(R.id.icon);
            iconView.setImageResource(getTabIcon(position));

            tab.setCustomView(v);
        }

    }

}
