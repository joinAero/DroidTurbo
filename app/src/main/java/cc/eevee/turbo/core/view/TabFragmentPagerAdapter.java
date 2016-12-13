package cc.eevee.turbo.core.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cc.eevee.turbo.R;

public abstract class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    private final Context mContext;
    private final int mResource;

    public TabFragmentPagerAdapter(FragmentManager fm, Context ctx, @LayoutRes int res) {
        super(fm);
        mContext = ctx;
        mResource = res;
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

        View v = LayoutInflater.from(mContext).inflate(mResource, null);

        TextView textView = (TextView) v.findViewById(R.id.text);
        if (textView != null) {
            final CharSequence text = getTabText(position);
            if (TextUtils.isEmpty(text)) {
                textView.setText(null);
                textView.setVisibility(View.GONE);
            } else {
                textView.setText(text);
                textView.setVisibility(View.VISIBLE);
            }
            // Apply style colors to text view.
            ColorStateList tabTextColors = tabLayout.getTabTextColors();
            if (tabTextColors != null) {
                textView.setTextColor(tabTextColors);
            }
        }
        // Select tab at current position to make style color valid.
        final int curPos = tabLayout.getSelectedTabPosition();
        if (curPos == position) {
            v.setSelected(true);
        }

        ImageView iconView = (ImageView) v.findViewById(R.id.icon);
        if (iconView != null) {
            final int iconResId = getTabIcon(position);
            if (iconResId == 0) {
                //iconView.setImageResource(0);
                iconView.setVisibility(View.GONE);
            } else {
                iconView.setImageResource(iconResId);
                iconView.setVisibility(View.VISIBLE);
            }
        }

        onCustomTabViewCreated(v, position, textView, iconView);

        tab.setCustomView(v);
    }

    protected void onCustomTabViewCreated(View view,
                                          int position,
                                          @Nullable TextView textView,
                                          @Nullable ImageView iconView) {
    }

}
