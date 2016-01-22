package cc.cubone.turbo.view;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.core.view.SimpleRecyclerViewAdapter;
import cc.cubone.turbo.model.Info;

public abstract class InfoRecyclerViewAdapter<Data extends Info, VH extends InfoRecyclerViewAdapter.ViewHolder>
        extends SimpleRecyclerViewAdapter<Data, VH> {

    private int mResource;

    public InfoRecyclerViewAdapter(@NonNull List<Data> dataList, @LayoutRes int resource) {
        super(dataList);
        mResource = resource;
    }

    public static <Data extends Info> InfoRecyclerViewAdapter<Data, InfoRecyclerViewAdapter.ViewHolder>
            create(@NonNull List<Data> dataList, @LayoutRes int resource) {
        return new InfoRecyclerViewAdapter<Data, ViewHolder>(dataList, resource) {
            @Override
            public ViewHolder onViewHolderCreate(View itemView, int viewType) {
                return new ViewHolder(itemView);
            }
        };
    }

    @Override
    public int getItemViewResource(int viewType) {
        return mResource;
    }

    @Override
    public void onViewHolderBind(Data data, VH holder, int position) {
        TextView titleView = holder.titleView;
        if (titleView != null) {
            titleView.setText(data.getTitle());
        }
        TextView descView = holder.descView;
        if (descView != null) {
            descView.setText(data.getDescription());
        }
        ImageView imageView = holder.imageView;
        if (imageView != null) {
            imageView.setImageDrawable(data.getDrawable());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title) TextView titleView;
        @Bind(R.id.desc) TextView descView;
        @Bind(R.id.image) ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
