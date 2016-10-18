package cc.cubone.turbo.view;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.core.view.SimpleRecyclerViewAdapter;
import cc.cubone.turbo.model.Info;

public abstract class InfoRecyclerViewAdapter<Data extends Info, VH extends InfoRecyclerViewAdapter.ViewHolder>
        extends SimpleRecyclerViewAdapter<Data, VH> {

    private int mResource;

    private InfoRecyclerViewAdapter(@NonNull List<Data> dataList, @LayoutRes int resource) {
        super(dataList);
        mResource = resource;
    }

    public static <Data extends Info> InfoRecyclerViewAdapter<Data, ViewHolder>
            create(@NonNull List<Data> dataList,
                   @LayoutRes int resource) {
        return new InfoRecyclerViewAdapter<Data, ViewHolder>(dataList, resource) {
            @Override
            public ViewHolder onViewHolderCreate(View itemView, int viewType) {
                return new ViewHolder(itemView);
            }
        };
    }

    public static <Data extends Info> InfoRecyclerViewAdapter<Data, ViewHolder2>
            create(@NonNull List<Data> dataList,
                   @LayoutRes int resource,
                   Binder<TextView, Data> infoBinder) {
        return new InfoRecyclerViewAdapter<Data, ViewHolder2>(dataList, resource) {
            @Override
            public ViewHolder2 onViewHolderCreate(View itemView, int viewType) {
                return new ViewHolder2(itemView);
            }
            @Override
            public void onViewHolderBind(Data data, ViewHolder2 holder, int position) {
                super.onViewHolderBind(data, holder, position);
                TextView infoView = holder.infoView;
                if (infoView != null && infoBinder != null) {
                    infoBinder.bind(infoView, data);
                }
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

        @BindView(R.id.title) public TextView titleView;
        @BindView(R.id.desc) public TextView descView;
        @BindView(R.id.image) public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ViewHolder2 extends ViewHolder {

        @BindView(R.id.info) public TextView infoView;

        public ViewHolder2(View itemView) {
            super(itemView);
        }
    }

    public interface Binder<V extends View, Data extends Info> {
        void bind(@NonNull V view, Data data);
    }

}
