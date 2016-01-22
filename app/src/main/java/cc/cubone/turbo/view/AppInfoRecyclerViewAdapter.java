package cc.cubone.turbo.view;

import android.annotation.SuppressLint;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import cc.cubone.turbo.R;
import cc.cubone.turbo.model.AppInfo;

public class AppInfoRecyclerViewAdapter extends
        InfoRecyclerViewAdapter<AppInfo, AppInfoRecyclerViewAdapter.ViewHolder> {

    public AppInfoRecyclerViewAdapter(@NonNull List<AppInfo> appInfos, @LayoutRes int resource) {
        super(appInfos, resource);
    }

    @Override
    public ViewHolder onViewHolderCreate(View itemView, int viewType) {
        return new ViewHolder(itemView);
    }

    @Override
    public void onViewHolderBind(AppInfo appInfo, ViewHolder holder, int position) {
        super.onViewHolderBind(appInfo, holder, position);
        bindInfo(appInfo, holder.infoView);
    }

    @SuppressLint("SetTextI18n")
    private void bindInfo(AppInfo appInfo, TextView infoView) {
        if (infoView == null) return;
        infoView.setText(appInfo.getType().name().toLowerCase()
                + ", " + appInfo.getState().name().toLowerCase());
    }

    public static class ViewHolder extends InfoRecyclerViewAdapter.ViewHolder {

        @Bind(R.id.info) TextView infoView;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
