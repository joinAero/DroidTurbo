package cc.cubone.turbo.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cc.cubone.turbo.R;

public abstract class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private final int mTabResId;

    public TabFragmentPagerAdapter(FragmentManager fm, Context ctx, @LayoutRes int tabResId) {
        super(fm);
        mContext = ctx;
        mTabResId = tabResId;
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
        return 0;
    }

    /**
     * Must be called after tabs are created.
     */
    public void customTabViews(TabLayout tabLayout) {
        final int count = tabLayout.getTabCount();
        if (count <= 0) {
            throw new IllegalStateException("TabLayout does not have any tabs");
        }
        // Iterate over all tabs and set the custom view
        for (int i = 0; i < count; i++) {
            customTabView(tabLayout, i);
        }
    }

    private void customTabView(TabLayout tabLayout, int position) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab == null) {
            return;
        }
        // `TabView` orientation are vertical in `TabLayout` which will raise the icon.
        //tab.setTag(getTabText(position)); // android.R.id.text1
        //tab.setIcon(getTabIcon(position)); // android.R.id.icon

        View v = LayoutInflater.from(mContext).inflate(mTabResId, null);

        TextView textView = (TextView) v.findViewById(R.id.text);
        textView.setText(getTabText(position));
        // Apply style colors to text view.
        ColorStateList tabTextColors = tabLayout.getTabTextColors();
        if (tabTextColors != null) {
            textView.setTextColor(tabTextColors);
        }
        // Select tab at current position to make style color valid.
        final int curPos = tabLayout.getSelectedTabPosition();
        if (curPos == position) {
            v.setSelected(true);
        }

        ImageView iconView = (ImageView) v.findViewById(R.id.icon);
        final int iconResId = getTabIcon(position);
        if (iconResId != 0) {
            iconView.setImageResource(iconResId);
            iconView.setVisibility(View.VISIBLE);
        } else {
            iconView.setVisibility(View.GONE);
        }

        tab.setCustomView(v);
    }

}
