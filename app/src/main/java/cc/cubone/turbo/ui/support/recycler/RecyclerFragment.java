package cc.cubone.turbo.ui.support.recycler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import cc.cubone.turbo.base.BaseListFragment;
import cc.cubone.turbo.ui.CardRecyclerViewAdapter;

public class RecyclerFragment extends BaseListFragment {

    public RecyclerFragment() {
    }

    public static RecyclerFragment newInstance() {
        return new RecyclerFragment();
    }

    @Override
    public void onLayoutCreated(RecyclerView recyclerView, @Nullable Bundle savedInstanceState) {
        RecyclerCardAdapter adapter = new RecyclerCardAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public static class RecyclerCardAdapter extends CardRecyclerViewAdapter {

        @Override
        public int getItemCount() {
            return 10;
        }

    }

}
