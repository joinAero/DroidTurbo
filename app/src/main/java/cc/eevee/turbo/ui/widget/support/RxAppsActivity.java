package cc.eevee.turbo.ui.widget.support;

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

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.eevee.turbo.R;
import cc.eevee.turbo.core.util.Log;
import cc.eevee.turbo.model.DataInfo;
import cc.eevee.turbo.ui.base.BaseActivity;
import cc.eevee.turbo.util.ToastUtils;
import cc.eevee.turbo.view.InfoRecyclerViewAdapter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxAppsActivity extends BaseActivity implements
        InfoRecyclerViewAdapter.OnItemViewClickListener<DataInfo<Long>> {

    private static final String TAG = "RxAppsActivity";

    private static final boolean DBG = false;

    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler) RecyclerView mRecyclerView;

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

    private Flowable<DataInfo<Long>> observableApps() {
        return Flowable.create(emitter -> {
            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            final PackageManager pm = getPackageManager();
            List<ResolveInfo> infos = pm.queryIntentActivities(mainIntent, 0);

            ComponentInfo compInfo;
            long i = 0L;
            for(ResolveInfo info : infos){
                if (emitter.isCancelled()){
                    break;
                }
                compInfo = getComponentInfo(info);
                emitter.onNext(new DataInfo<>(
                        info.loadLabel(pm).toString(),
                        (compInfo == null) ? "" : compInfo.packageName,
                        info.loadIcon(pm), i++));
            }
            // after sending all values we complete the sequence
            if (!emitter.isCancelled()){
                emitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }

    private ComponentInfo getComponentInfo(ResolveInfo info) {
        if (info.activityInfo != null) return info.activityInfo;
        if (info.serviceInfo != null) return info.serviceInfo;
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            if (info.providerInfo != null) return info.providerInfo;
        }
        return null;
    }

    private Subscription mSubscription;

    private void refreshApps() {
        mSwipeRefreshLayout.setRefreshing(true);
        mRecyclerView.setAdapter(null);
        if (mSubscription != null) {
            mSubscription.cancel();
        }
        // hot observable begin emitting items as soon as it is created
        observableApps()
            .map(new Function<DataInfo<Long>, DataInfo<Long>>() {
                long mIndex = 0;
                @Override
                public DataInfo<Long> apply(DataInfo<Long> info) throws Exception {
                    if (DBG) Log.i(TAG, "call: " + info.getData());
                    info.setTitle((mIndex++) + " " + info.getTitle());
                    return info;
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<DataInfo<Long>>() {
                InfoRecyclerViewAdapter<DataInfo<Long>, ?> mAdapter;
                @Override
                public void onSubscribe(Subscription s) {
                    mSubscription = s;
                    s.request(1);
                }
                @Override
                public void onNext(DataInfo<Long> info) {
                    if (DBG) Log.i(TAG, "onNext: " + info.getTitle());
                    if (mAdapter == null) {
                        ArrayList<DataInfo<Long>> infoList = new ArrayList<>();
                        infoList.add(info);
                        mAdapter = createAdapter(infoList);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter.addData(info);
                    }
                    mSubscription.request(1);
                }
                @Override
                public void onError(Throwable t) {
                    if (DBG) Log.i(TAG, "onError");
                    t.printStackTrace();
                    mSwipeRefreshLayout.setRefreshing(false);
                    ToastUtils.show(RxAppsActivity.this, "Refresh apps failed!");
                }
                @Override
                public void onComplete() {
                    if (DBG) Log.i(TAG, "onCompleted");
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
    }

    @Override
    protected void onDestroy() {
        if (mSubscription != null) {
            mSubscription.cancel();
        }
        super.onDestroy();
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
