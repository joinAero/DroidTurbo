package cc.cubone.turbo.ui.demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.ColorInt;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.core.rom.MIUIUtils;
import cc.cubone.turbo.model.DataInfo;
import cc.cubone.turbo.view.InfoRecyclerViewAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TransparentStatusBarActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent_status_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.bar);
        setSupportActionBar(toolbar);

        initDrawer(toolbar);
        initStatusBar(toolbar);

        // Set the cross indicator to close this
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
            actionBar.setHomeActionContentDescription("Close");
        }

        initRecyclerView();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        if (mDrawerToggle != null) mDrawerToggle.syncState();
    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer != null) {
            // slide drawer from right to left
            //((DrawerLayout.LayoutParams) drawer.getChildAt(1).getLayoutParams()).gravity = GravityCompat.END;
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            //toggle.setDrawerIndicatorEnabled(false);
            toggle.syncState();
            mDrawerToggle = toggle;
        }
        mDrawerLayout = drawer;
    }

    protected void initStatusBar(View toolbar) {
        // Ensure `setStatusBarImmersiveMode()`
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            // Ensure content view `fitsSystemWindows` is false.
            ViewGroup contentParent = (ViewGroup) findViewById(android.R.id.content);
            View content = contentParent.getChildAt(0);
            // If using `DrawerLayout`, must ensure its subviews `fitsSystemWindows` are all false.
            // Because in some roms, such as MIUI, it will fits system windows for each subview.
            setFitsSystemWindows(content, false, true);

            // Add padding to hold the status bar place.
            clipToStatusBar(toolbar);

            // Add a view to hold the status bar place.
            // Note: if using appbar_scrolling_view_behavior of CoordinatorLayout, however,
            // the holder view could be scrolled to outside as it above the app bar.
            //holdStatusBar(toolbar, R.color.colorPrimary);
        }
    }

    protected void setFitsSystemWindows(View view, boolean fitSystemWindows, boolean applyToChildren) {
        if (view == null) return;
        view.setFitsSystemWindows(fitSystemWindows);
        if (applyToChildren && (view instanceof ViewGroup)) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0, n = viewGroup.getChildCount(); i < n; i++) {
                viewGroup.getChildAt(i).setFitsSystemWindows(fitSystemWindows);
            }
        }
    }

    protected void clipToStatusBar(View view) {
        final int statusBarHeight = getStatusBarHeight(this);
        view.getLayoutParams().height += statusBarHeight;
        view.setPadding(0, statusBarHeight, 0, 0);
    }

    /*protected void holdStatusBar(View view, @ColorRes int resid) {
        ViewGroup toolbarParent = (ViewGroup) view.getParent();
        int i = 0;
        for (int n = toolbarParent.getChildCount(); i < n; i++) {
            if (toolbarParent.getChildAt(i) == view) break;
        }
        View holderView = new View(this);
        holderView.setId(R.id.status_bar);
        holderView.setBackgroundColor(getResources().getColor(resid));
        toolbarParent.addView(holderView, i, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this)));
        if (toolbarParent instanceof RelativeLayout) {
            ((RelativeLayout.LayoutParams) toolbarParent.getLayoutParams())
                    .addRule(RelativeLayout.BELOW, R.id.status_bar);
        }
    }*/

    protected int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected void setStatusBarImmersiveMode(@ColorInt int color) {
        Window win = getWindow();

        // StatusBar
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 21) { // 21, 5.0, LOLLIPOP
            win.getAttributes().systemUiVisibility |=
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.setStatusBarColor(color);
        }

        // Setup immersive mode on third-party rom
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            //FlymeUtils.setStatusBarDarkIcon(win, false);
            MIUIUtils.setStatusBar(win, MIUIUtils.StatusBarMode.TRANSPARENT);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setStatusBarImmersiveMode(Color.TRANSPARENT);
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
            case android.R.id.home: finish(); return true;
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_out_bottom);
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

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        loadApps(recyclerView);
    }

    private void loadApps(final RecyclerView recyclerView) {
        Observable.create(new Observable.OnSubscribe<List<DataInfo<ResolveInfo>>>() {
            @Override
            public void call(Subscriber<? super List<DataInfo<ResolveInfo>>> subscriber) {
                if (subscriber.isUnsubscribed()){
                    return;
                }

                final List<DataInfo<ResolveInfo>> dataList = new ArrayList<>();

                final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

                final PackageManager pm = getPackageManager();
                List<ResolveInfo> infoList = pm.queryIntentActivities(mainIntent, 0);
                for(ResolveInfo info : infoList){
                    dataList.add(new DataInfo<>(
                            info.loadLabel(pm).toString(),
                            (info.activityInfo == null) ? "" : info.activityInfo.packageName,
                            info.loadIcon(pm), info));
                }

                subscriber.onNext(dataList);
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<DataInfo<ResolveInfo>>>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(List<DataInfo<ResolveInfo>> dataList) {
                recyclerView.setAdapter(InfoRecyclerViewAdapter.create(dataList, R.layout.item_app));
            }
        });
    }

}
