package cc.eevee.turbo.ui.widget.design;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.eevee.turbo.R;
import cc.eevee.turbo.model.DataInfo;
import cc.eevee.turbo.ui.base.ListSightFragment;
import cc.eevee.turbo.util.ContextUtils;
import cc.eevee.turbo.view.InfoRecyclerViewAdapter;
import pl.droidsonroids.gif.GifDrawable;

public class DesignFragment extends ListSightFragment implements
        InfoRecyclerViewAdapter.OnItemViewClickListener<DataInfo<Class>> {

    public DesignFragment() {
    }

    public static DesignFragment newInstance() {
        return new DesignFragment();
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
        infos.add(createInfo(R.string.bottom_sheet, "How to using bottom sheet.",
                null, BottomSheetActivity.class));
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
