package cc.cubone.turbo.view;

import android.annotation.SuppressLint;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
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

    @SuppressLint("SetTextI18n")
    private void bindInfo(AppCard appCard, TextView infoView) {
        if (infoView == null) return;
        infoView.setText(appCard.getType().name().toLowerCase()
                + ", " + appCard.getState().name().toLowerCase());
    }

    public static class ViewHolder extends CardRecyclerViewAdapter.ViewHolder {

        @Bind(R.id.info) TextView infoView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
