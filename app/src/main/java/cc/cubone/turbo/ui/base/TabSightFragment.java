package cc.cubone.turbo.ui.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.cubone.turbo.R;

public class TabSightFragment extends BaseSightFragment {

    public TabSightFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    protected void onViewCreatedFirstSight(View view) {
        super.onViewCreatedFirstSight(view);
        TabLayout tab = (TabLayout) view.findViewById(R.id.tab);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        onViewPrepared(tab, pager);
    }

    public void onViewPrepared(TabLayout tabLayout, ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
    }

}
