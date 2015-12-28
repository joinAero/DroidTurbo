package cc.cubone.turbo.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cc.cubone.turbo.R;

public class BaseListFragment extends BaseFragment {

    public BaseListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        RecyclerView recycler = (RecyclerView) view.findViewById(R.id.recycler);
        onLayoutCreated(recycler, savedInstanceState);
    }

    public void onLayoutCreated(RecyclerView recyclerView, @Nullable Bundle savedInstanceState) {
    }

}
