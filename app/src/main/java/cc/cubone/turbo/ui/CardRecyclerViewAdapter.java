package cc.cubone.turbo.ui;

import android.content.Context;
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

public class CardRecyclerViewAdapter extends
        RecyclerView.Adapter<CardRecyclerViewAdapter.ViewHolder> {

    private List<Card> mCardList;

    public CardRecyclerViewAdapter(List<Card> cardList) {
        mCardList = cardList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Card card = mCardList.get(position);
        //if (card == null) return;

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
        return mCardList == null ? 0 : mCardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleView;
        public TextView descView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.title);
            descView = (TextView) itemView.findViewById(R.id.desc);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }

    }

}
