package cc.eevee.turbo.ui.demo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import cc.eevee.turbo.R;
import cc.eevee.turbo.model.DataInfo;
import cc.eevee.turbo.ui.base.BaseActivity;
import cc.eevee.turbo.view.InfoRecyclerViewAdapter;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TransparentStatusBarActivity extends BaseActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent_status_bar);
        initToolbar();
        initRecyclerView();
    }

    @Override
    protected void onToolbarCreated(Toolbar toolbar, @StatusBarMode int statusBarMode) {
        super.onToolbarCreated(toolbar, statusBarMode);
        initDrawer(toolbar);
    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer != null) {
            // slide drawer from right to left
            //((DrawerLayout.LayoutParams) drawer.getChildAt(1).getLayoutParams()).gravity = GravityCompat.END;
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.syncState();
            mDrawerToggle = toggle;
        }
        mDrawerLayout = drawer;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        loadApps(recyclerView);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        if (mDrawerToggle != null) mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDrawerLayout != null) {
            getMenuInflater().inflate(R.menu.transparent_status_bar, menu);
        }
        //TintUtils.tintList(this, menu, R.color.bar_icon_color);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu: toggleDrawer(GravityCompat.END); return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isDrawerOpen(GravityCompat.END)) {
            toggleDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    private boolean isDrawerOpen(int drawerGravity) {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(drawerGravity);
    }

    private void toggleDrawer(int drawerGravity) {
        if (mDrawerLayout == null) return;
        if (mDrawerLayout.isDrawerVisible(drawerGravity)) {
            mDrawerLayout.closeDrawer(drawerGravity);
        } else {
            mDrawerLayout.openDrawer(drawerGravity);
        }
    }

    private Subscription mSubscription;

    private void loadApps(final RecyclerView recyclerView) {
        if (mSubscription != null) {
            mSubscription.cancel();
        }
        Flowable.create((FlowableOnSubscribe<DataInfo<ResolveInfo>>) e -> {
            //long amount = e.requested();

            final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            final PackageManager pm = getPackageManager();
            List<ResolveInfo> infoList = pm.queryIntentActivities(mainIntent, 0);
            for(ResolveInfo info : infoList) {
                if (e.isCancelled()) break;
                e.onNext(new DataInfo<>(
                        info.loadLabel(pm).toString(),
                        (info.activityInfo == null) ? "" : info.activityInfo.packageName,
                        info.loadIcon(pm), info));
            }

            e.onComplete();
        }, BackpressureStrategy.BUFFER)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<DataInfo<ResolveInfo>>() {
            InfoRecyclerViewAdapter<DataInfo<ResolveInfo>, ?> mAdapter;
            @Override
            public void onSubscribe(Subscription s) {
                mSubscription = s;
                s.request(1);
            }
            @Override
            public void onNext(DataInfo<ResolveInfo> info) {
                if (mAdapter == null) {
                    ArrayList<DataInfo<ResolveInfo>> dataList = new ArrayList<>();
                    dataList.add(info);
                    mAdapter = InfoRecyclerViewAdapter.create(dataList, R.layout.item_app);
                    recyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.addData(info);
                }
                mSubscription.request(1);
            }
            @Override
            public void onError(Throwable t) {
            }
            @Override
            public void onComplete() {
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

}
