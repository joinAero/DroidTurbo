package cc.cubone.turbo.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.model.Card;

public class CardRecyclerViewAdapter<Data extends Card> extends
        RecyclerView.Adapter<CardRecyclerViewAdapter.ViewHolder> implements View.OnClickListener {

    private List<Data> mCardList;
    private int mResource;

    private OnItemClickListener<Data> mOnItemClickListener;

    public CardRecyclerViewAdapter(@NonNull List<Data> cardList, @LayoutRes int resource) {
        mCardList = cardList;
        mResource = resource;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener<Data> listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(mResource, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Card card = mCardList.get(position);
        //if (card == null) return;
        holder.itemView.setTag(position);

        TextView titleView = holder.titleView;
        if (titleView != null) {
            titleView.setText(card.getTitle());
        }

        TextView descView = holder.descView;
        if (descView != null) {
            descView.setText(card.getDescription());
        }

        ImageView imageView = holder.imageView;
        if (imageView != null) {
            imageView.setImageDrawable(card.getDrawable());
        }
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            final int position = (int) v.getTag();
            mOnItemClickListener.onItemClick(v, position, mCardList.get(position));
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

    public interface OnItemClickListener<Data extends Card> {
        void onItemClick(View view, int position, Data data);
    }

}
