package cc.cubone.turbo.ui.support.recycler;

import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import cc.cubone.turbo.model.DataInfo;
import cc.cubone.turbo.ui.base.BaseActivity;
import cc.cubone.turbo.util.ToastUtils;
import cc.cubone.turbo.view.InfoRecyclerViewAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RxAppsActivity extends BaseActivity implements
        InfoRecyclerViewAdapter.OnItemViewClickListener<DataInfo<Long>> {

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
    }

    private void initViews() {
        initToolbar();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshApps);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        // should post `setRefreshing()` to take effect when called in `onCreate()`
        mSwipeRefreshLayout.post(this::refreshApps);
    }

    @Override
    public void onItemViewClick(View view, int position, DataInfo<Long> info) {
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private Observable<DataInfo<Long>> observableApps() {
        return Observable.create(subscriber -> {
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            final PackageManager pm = getPackageManager();
            List<ResolveInfo> infos = pm.queryIntentActivities(mainIntent, 0);

            ComponentInfo compInfo;
            long i = 0L;
            for(ResolveInfo info : infos){
                if (subscriber.isUnsubscribed()){
                    return;
                }
                compInfo = getComponentInfo(info);
                subscriber.onNext(new DataInfo<>(
                        info.loadLabel(pm).toString(),
                        (compInfo == null) ? "" : compInfo.packageName,
                        info.loadIcon(pm), i++));
                //break; // test complete immediately
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
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            if (info.providerInfo != null) return info.providerInfo;
        }
        return null;
    }

    private void refreshApps() {
        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.setAdapter(null);
        // hot observable begin emitting items as soon as it is created
        observableApps()
                .map(new Func1<DataInfo<Long>, DataInfo<Long>>() {
                    long mIndex = 0;
                    @Override
                    public DataInfo<Long> call(DataInfo<Long> info) {
                        if (DBG) Log.i(TAG, "call: " + info.getData());
                        info.setTitle((mIndex++) + " " + info.getTitle());
                        return info;
                    }
                })
                .onBackpressureBuffer() // emit faster than observer consume
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DataInfo<Long>>() {
                    private List<DataInfo<Long>> mApps = new ArrayList<>();
                    @Override
                    public void onCompleted() {
                        if (DBG) Log.i(TAG, "onCompleted");
                        mRecyclerView.setAdapter(createAdapter(mApps));
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    @Override
                    public void onError(Throwable e) {
                        if (DBG) Log.i(TAG, "onError");
                        e.printStackTrace();
                        mSwipeRefreshLayout.setRefreshing(false);
                        ToastUtils.show(RxAppsActivity.this, "Refresh apps failed!");
                    }
                    @Override
                    public void onNext(DataInfo<Long> info) {
                        if (DBG) Log.i(TAG, "onNext: " + info.getTitle());
                        mApps.add(info);
                    }
                });
    }

    private InfoRecyclerViewAdapter<DataInfo<Long>, InfoRecyclerViewAdapter.ViewHolder2>
            createAdapter(@NonNull List<DataInfo<Long>> dataList) {
        InfoRecyclerViewAdapter<DataInfo<Long>, InfoRecyclerViewAdapter.ViewHolder2> adapter =
                InfoRecyclerViewAdapter.create(dataList, R.layout.item_app,
                        (view, info) -> view.setText("index: " + info.getData()));
        adapter.setOnItemViewClickListener(this);
        return adapter;
    }

}
