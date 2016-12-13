package cc.eevee.turbo.ui.base;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cc.eevee.turbo.R;

public class ListFragment extends BaseFragment {

    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        RecyclerView recycler = ButterKnife.findById(view, R.id.recycler);
        onViewPrepared(recycler);
    }

    public void onViewPrepared(RecyclerView recyclerView) {
    }

}
