package cc.cubone.turbo.ui.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cc.cubone.turbo.R;

public class ListSightFragment extends BaseSightFragment {

    public ListSightFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    protected void onViewCreatedFirstSight(View view) {
        super.onViewCreatedFirstSight(view);
        RecyclerView recycler = ButterKnife.findById(view, R.id.recycler);
        onViewPrepared(recycler);
    }

    public void onViewPrepared(RecyclerView recyclerView) {
    }

}
