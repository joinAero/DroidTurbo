package cc.cubone.turbo.view;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.model.AppCard;

public class AppCardRecyclerViewAdapter extends
        CardRecyclerViewAdapter<AppCard, AppCardRecyclerViewAdapter.ViewHolder> {

    public AppCardRecyclerViewAdapter(@NonNull List<AppCard> appCards, @LayoutRes int resource) {
        super(appCards, resource);
    }

    @Override
    public ViewHolder onViewHolderCreate(View itemView, int viewType) {
        return new ViewHolder(itemView);
    }

    @Override
    public void onViewHolderBind(AppCard appCard, ViewHolder holder, int position) {
        super.onViewHolderBind(appCard, holder, position);
        bindInfo(appCard, holder.infoView);
    }

    private void bindInfo(AppCard appCard, TextView infoView) {
        if (infoView == null) return;
    }

    public static class ViewHolder extends CardRecyclerViewAdapter.ViewHolder {

        public final TextView infoView;

        public ViewHolder(View itemView) {
            super(itemView);
            infoView = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
