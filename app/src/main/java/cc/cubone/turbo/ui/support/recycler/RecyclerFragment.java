package cc.cubone.turbo.ui.support.recycler;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.model.DataCard;
import cc.cubone.turbo.ui.base.ListSightFragment;
import cc.cubone.turbo.util.ContextUtils;
import cc.cubone.turbo.view.CardRecyclerViewAdapter;

public class RecyclerFragment extends ListSightFragment implements
        CardRecyclerViewAdapter.OnItemClickListener<DataCard<Class>> {

    public RecyclerFragment() {
    }

    public static RecyclerFragment newInstance() {
        return new RecyclerFragment();
    }

    @Override
    public void onViewPrepared(RecyclerView recyclerView) {
        CardRecyclerViewAdapter<DataCard<Class>> adapter = new CardRecyclerViewAdapter<>(createCards());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private List<DataCard<Class>> createCards() {
        List<DataCard<Class>> cards = new ArrayList<>();
        cards.add(new DataCard<Class>("All Apps", "Show all apps in list or grid.", null, AllAppsActivity.class));
        return cards;
    }

    @Override
    public void onItemClick(View view, int position, DataCard<Class> data) {
        ContextUtils.startActivity(getActivity(), data.getData());
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
