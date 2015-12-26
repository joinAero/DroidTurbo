package cc.cubone.turbo.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.cubone.turbo.R;

public class BaseTabFragment extends BaseFragment {

    public BaseTabFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TabLayout tab = (TabLayout) view.findViewById(R.id.tab);
        ViewPager pager = (ViewPager) view.findViewById(R.id.pager);

        boolean attach = onTabCreated(tab, pager, savedInstanceState);
        if (attach) {
            tab.setupWithViewPager(pager);
        }
    }

    public boolean onTabCreated(TabLayout tab, ViewPager pager, @Nullable Bundle savedInstanceState) {
        return true;
    }

}