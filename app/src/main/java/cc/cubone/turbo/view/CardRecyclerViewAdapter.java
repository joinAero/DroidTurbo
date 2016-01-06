package cc.cubone.turbo.view;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.core.view.SimpleRecyclerViewAdapter;
import cc.cubone.turbo.model.Card;

public abstract class CardRecyclerViewAdapter<Data extends Card, VH extends CardRecyclerViewAdapter.ViewHolder>
        extends SimpleRecyclerViewAdapter<Data, VH>
        implements SimpleRecyclerViewAdapter.OnItemViewCreateListener<Data, VH> {

    private int mResource;

    public CardRecyclerViewAdapter(@NonNull List<Data> dataList, @LayoutRes int resource) {
        super(dataList);
        mResource = resource;
        setOnItemViewCreateListener(this);
    }

    public static <Data extends Card> CardRecyclerViewAdapter<Data, CardRecyclerViewAdapter.ViewHolder>
            create(@NonNull List<Data> dataList, @LayoutRes int resource) {
        return new CardRecyclerViewAdapter<Data, ViewHolder>(dataList, resource) {
            @Override
            public ViewHolder onItemViewHolderCreate(View itemView, int viewType) {
                return new ViewHolder(itemView);
            }
        };
    }

    @Override
    public int onItemViewTypeCreate(int position) {
        return 0;
    }

    @Override
    public int onItemViewResourceCreate(int viewType) {
        return mResource;
    }

    @Override
    public void onItemViewHolderBind(Data data, VH holder, int position) {
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

        public final TextView titleView;
        public final TextView descView;
        public final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.title);
            descView = (TextView) itemView.findViewById(R.id.desc);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
