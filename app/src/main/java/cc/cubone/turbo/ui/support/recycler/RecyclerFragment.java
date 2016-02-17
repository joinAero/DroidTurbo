package cc.cubone.turbo.ui.support.recycler;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.model.DataInfo;
import cc.cubone.turbo.ui.base.ListSightFragment;
import cc.cubone.turbo.util.ContextUtils;
import cc.cubone.turbo.view.InfoRecyclerViewAdapter;
import pl.droidsonroids.gif.GifDrawable;

public class RecyclerFragment extends ListSightFragment implements
        InfoRecyclerViewAdapter.OnItemViewClickListener<DataInfo<Class>> {

    public RecyclerFragment() {
    }

    public static RecyclerFragment newInstance() {
        return new RecyclerFragment();
    }

    @Override
    public void onViewPrepared(RecyclerView recyclerView) {
        InfoRecyclerViewAdapter<DataInfo<Class>, InfoRecyclerViewAdapter.ViewHolder> adapter
                = InfoRecyclerViewAdapter.create(createInfos(), R.layout.item_card);
        adapter.setOnItemViewClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private List<DataInfo<Class>> createInfos() {
        List<DataInfo<Class>> infos = new ArrayList<>();
        infos.add(createInfo(R.string.all_apps, "Show all apps in list or grid.",
                "all_apps.gif", AllAppsActivity.class));
        infos.add(createInfo(R.string.rx_apps, "Show all apps with RxJava.",
                "rx_apps.gif", RxAppsActivity.class));
        return infos;
    }

    private DataInfo<Class> createInfo(int titleId, String desc, String gifAsset, Class<?> cls) {
        Drawable drawable = null;
        if (gifAsset != null) {
            try {
                drawable = new GifDrawable(getActivity().getAssets(), gifAsset);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new DataInfo<Class>(getString(titleId), desc, drawable, cls);
    }

    @Override
    public void onItemViewClick(View view, int position, DataInfo<Class> data) {
        ContextUtils.startActivity(getActivity(), data.getData());
    }

}
