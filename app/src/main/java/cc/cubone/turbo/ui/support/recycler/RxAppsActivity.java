package cc.cubone.turbo.ui.support.recycler;

import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.core.util.Log;
import cc.cubone.turbo.model.AppInfo;
import cc.cubone.turbo.model.Info;
import cc.cubone.turbo.ui.base.BaseActivity;
import cc.cubone.turbo.util.ToastUtils;
import cc.cubone.turbo.view.AppInfoRecyclerViewAdapter;
import cc.cubone.turbo.view.InfoRecyclerViewAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxAppsActivity extends BaseActivity implements
        AppInfoRecyclerViewAdapter.OnItemViewClickListener<AppInfo> {

    private static final String TAG = "RxAppsActivity";

    private static final boolean DBG = false;

    @Bind(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.recycler) RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_apps);
        ButterKnife.bind(this);
        initViews();
        refreshApps();
    }

    private void initViews() {
        initToolbar();

        mSwipeRefreshLayout.setOnRefreshListener(this::refreshApps);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onItemViewClick(View view, int position, AppInfo appInfo) {
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private Observable<Info> observableApps() {
        return Observable.create(subscriber -> {
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            final PackageManager pm = getPackageManager();
            List<ResolveInfo> infos = pm.queryIntentActivities(mainIntent, 0);

            ComponentInfo compInfo;
            for(ResolveInfo info : infos){
                if (subscriber.isUnsubscribed()){
                    return;
                }
                compInfo = getComponentInfo(info);
                subscriber.onNext(new Info(
                        info.loadLabel(pm).toString(),
                        (compInfo == null) ? "" : compInfo.packageName,
                        info.loadIcon(pm)));
            }
            // after sending all values we complete the sequence
            if (!subscriber.isUnsubscribed()){
                subscriber.onCompleted();
            }
        });
    }

    private ComponentInfo getComponentInfo(ResolveInfo info) {
        if (info.activityInfo != null) return info.activityInfo;
        if (info.serviceInfo != null) return info.serviceInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (info.providerInfo != null) return info.providerInfo;
        }
        return null;
    }

    private void refreshApps() {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
        }
        mRecyclerView.setAdapter(null);
        observableApps()
                .onBackpressureBuffer()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Info>() {

                    private List<Info> mApps = new ArrayList<>();

                    @Override
                    public void onCompleted() {
                        if (DBG) Log.d(TAG, "onCompleted");
                        mRecyclerView.setAdapter(InfoRecyclerViewAdapter.create(mApps, R.layout.item_app));
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (DBG) Log.d(TAG, "onError");
                        e.printStackTrace();
                        mSwipeRefreshLayout.setRefreshing(false);
                        ToastUtils.show(RxAppsActivity.this, "Refresh apps failed!");
                    }

                    @Override
                    public void onNext(Info info) {
                        if (DBG) Log.d(TAG, "onNext: " + info.getTitle());
                        mApps.add(info);
                    }
                });
    }

}
