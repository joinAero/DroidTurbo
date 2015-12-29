package cc.cubone.turbo.ui.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.cubone.turbo.R;
import cc.cubone.turbo.core.app.SightFragment;

public class TabFragment extends SightFragment {

    public TabFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TabLayout tab = (TabLayout) view.findViewById(R.id.tab);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        onViewPrepared(tab, pager);
    }

    public void onViewPrepared(TabLayout tabLayout, ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
    }

}
