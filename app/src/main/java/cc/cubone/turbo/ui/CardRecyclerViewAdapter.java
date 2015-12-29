package cc.cubone.turbo.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

    private OnItemClickListener<Data> mOnItemClickListener;

    public CardRecyclerViewAdapter(@NonNull List<Data> cardList) {
        mCardList = cardList;
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener<Data> listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_card, parent, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Card card = mCardList.get(position);
        //if (card == null) return;

        holder.itemView.setTag(position);

        TextView titleView = holder.titleView;
        TextView descView = holder.descView;
        ImageView imageView = holder.imageView;

        titleView.setText(card.getTitle());
        descView.setText(card.getDescription());

        String imagePath = card.getImagePath();
        if (TextUtils.isEmpty(imagePath)) {
            imageView.setImageDrawable(null);
        } else {
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

    public static interface OnItemClickListener<Data extends Card> {
        void onItemClick(View view, int position, Data data);
    }

    public static class ActivityCard extends Card {

        private Class<?> mActivity;

        public ActivityCard(String title, String description, String imagePath, Class<?> activity) {
            super(title, description, imagePath);
            mActivity = activity;
        }

        public Class<?> getActivity() {
            return mActivity;
        }

        public void setActivity(Class<?> activity) {
            mActivity = activity;
        }
    }

}
