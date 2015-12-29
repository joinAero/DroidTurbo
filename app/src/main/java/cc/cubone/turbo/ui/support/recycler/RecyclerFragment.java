package cc.cubone.turbo.ui.support.recycler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import cc.cubone.turbo.model.Card;
import cc.cubone.turbo.ui.CardRecyclerViewAdapter;
import cc.cubone.turbo.ui.base.BaseListFragment;

public class RecyclerFragment extends BaseListFragment {

    public RecyclerFragment() {
    }

    public static RecyclerFragment newInstance() {
        return new RecyclerFragment();
    }

    @Override
    public void onLayoutCreated(RecyclerView recyclerView, @Nullable Bundle savedInstanceState) {
        CardRecyclerViewAdapter adapter = new CardRecyclerViewAdapter(Card.createList(10));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}
