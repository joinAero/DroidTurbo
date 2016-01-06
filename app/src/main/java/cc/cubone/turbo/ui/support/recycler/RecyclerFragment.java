package cc.cubone.turbo.ui.support.recycler;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.model.DataCard;
import cc.cubone.turbo.ui.base.ListSightFragment;
import cc.cubone.turbo.util.ContextUtils;
import cc.cubone.turbo.view.CardRecyclerViewAdapter;
import pl.droidsonroids.gif.GifDrawable;

public class RecyclerFragment extends ListSightFragment implements
        CardRecyclerViewAdapter.OnItemViewClickListener<DataCard<Class>> {

    public RecyclerFragment() {
    }

    public static RecyclerFragment newInstance() {
        return new RecyclerFragment();
    }

    @Override
    public void onViewPrepared(RecyclerView recyclerView) {
        CardRecyclerViewAdapter<DataCard<Class>, CardRecyclerViewAdapter.ViewHolder> adapter
                = CardRecyclerViewAdapter.create(createCards(), R.layout.item_card);
        adapter.setOnItemViewClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private List<DataCard<Class>> createCards() {
        List<DataCard<Class>> cards = new ArrayList<>();
        cards.add(createCard(R.string.all_apps, "Show all apps in list or grid.",
                "all_apps.gif", AllAppsActivity.class));
        return cards;
    }

    private DataCard<Class> createCard(int titleId, String desc, String gifAsset, Class<?> cls) {
        Drawable drawable = null;
        try {
            drawable = new GifDrawable(getActivity().getAssets(), gifAsset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DataCard<Class>(getString(titleId), desc, drawable, cls);
    }

    @Override
    public void onItemViewClick(View view, int position, DataCard<Class> data) {
        ContextUtils.startActivity(getActivity(), data.getData());
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
