package cc.cubone.turbo.ui.support.recycler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.ui.CardRecyclerViewAdapter;
import cc.cubone.turbo.ui.CardRecyclerViewAdapter.ActivityCard;
import cc.cubone.turbo.ui.base.BaseListFragment;
import cc.cubone.turbo.util.ContextUtils;

public class RecyclerFragment extends BaseListFragment implements
        CardRecyclerViewAdapter.OnItemClickListener<ActivityCard> {

    public RecyclerFragment() {
    }

    public static RecyclerFragment newInstance() {
        return new RecyclerFragment();
    }

    @Override
    public void onLayoutCreated(RecyclerView recyclerView, @Nullable Bundle savedInstanceState) {
        CardRecyclerViewAdapter<ActivityCard> adapter = new CardRecyclerViewAdapter<>(createCards());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private List<ActivityCard> createCards() {
        List<ActivityCard> cards = new ArrayList<>();
        cards.add(new ActivityCard("All Apps", "Show all apps in list or grid.", null, AllAppsActivity.class));
        return cards;
    }

    @Override
    public void onItemClick(View view, int position, ActivityCard card) {
        ContextUtils.startActivity(getActivity(), card.getActivity());
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
