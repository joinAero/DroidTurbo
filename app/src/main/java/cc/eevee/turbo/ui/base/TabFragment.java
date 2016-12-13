package cc.eevee.turbo.ui.base;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cc.eevee.turbo.R;
import cc.eevee.turbo.core.app.SightFragment;

public class TabFragment extends SightFragment {

    public TabFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TabLayout tab = ButterKnife.findById(view, R.id.tab);
        ViewPager pager = ButterKnife.findById(view, R.id.pager);
        onViewPrepared(tab, pager);
    }

    public void onViewPrepared(TabLayout tabLayout, ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager);
    }

}
