package cc.cubone.turbo.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.core.view.TabFragmentPagerAdapter;
import cc.cubone.turbo.ui.arch.ArchFragment;
import cc.cubone.turbo.ui.base.BaseActivity;
import cc.cubone.turbo.ui.demo.SnakeSurfaceViewActivity;
import cc.cubone.turbo.ui.demo.TransparentStatusBarActivity;
import cc.cubone.turbo.ui.fever.FeverFragment;
import cc.cubone.turbo.ui.widget.WidgetFragment;
import cc.cubone.turbo.util.ContextUtils;
import cc.cubone.turbo.util.PermissionUtils;
import cc.cubone.turbo.util.TintUtils;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static cc.cubone.turbo.ui.ColorPageFragment.PINK;
import static cc.cubone.turbo.ui.ColorPageFragment.PURPLE;

/**
 * Main activity.
 *
 * <p>UI Guide & Source:
 * <ul>
 * <li><a href="https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer">Fragment Navigation Drawer</a>
 * <li><a href="https://github.com/android/platform_frameworks_support">Platform Frameworks Support</a>
 * </ul>
 */
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final int REQ_WRITE_EXTERNAL_STORAGE = 1;

    @BindView(R.id.panel) SlidingPaneLayout mSlidingPane;
    @BindView(R.id.pager) ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews(savedInstanceState);
        requestPermissions();
    }

    private void initViews(Bundle savedInstanceState) {
        final int dip60 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60,
                getResources().getDisplayMetrics());
        mSlidingPane.setCoveredFadeColor(0);
        mSlidingPane.setSliderFadeColor(0);
        mSlidingPane.setParallaxDistance(dip60);
        mSlidingPane.setShadowResourceLeft(R.drawable.shadow_left);

        Toolbar toolbar = initToolbar();
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener((view) -> {
            if (mSlidingPane.isOpen()) {
                mSlidingPane.closePane();
            } else {
                mSlidingPane.openPane();
            }
        });

        final View content = mSlidingPane;

        NavigationView nav = ButterKnife.findById(content, R.id.nav);
        nav.setNavigationItemSelectedListener(this);
        if (Build.VERSION.SDK_INT >= 21) { // 21, 5.0, LOLLIPOP
            // Ensure `NavigationView` is behind the content.
            nav.setZ(-1);
        }

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(adapter);
        // retain all pagers
        mPager.setOffscreenPageLimit(adapter.getCount());

        TabLayout barTab = ButterKnife.findById(content, R.id.tab);
        barTab.setupWithViewPager(mPager);
        adapter.customTabViews(barTab);

        FloatingActionButton fab = ButterKnife.findById(content, R.id.fab);
        fab.setOnClickListener((view) -> {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        });
    }

    /**
     * Reference:
     * <ul>
     * <li><a href="http://developer.android.com/guide/topics/security/permissions.html">
     *     System Permissions</a>
     * <li><a href="http://developer.android.com/training/permissions/index.html">
     *     Working with System Permissions</a>
     * </ul>
     *
     * Note: Nested fragments do not support the onRequestPermissionsResult() callback.
     *
     * @see <a href="http://stackoverflow.com/questions/33169455/onrequestpermissionsresult-not-being-called-in-dialog-fragment">
     *     onRequestPermissionsResult not being called in dialog fragment</a>
     */
    private void requestPermissions() {
        if (!PermissionUtils.checkPermissionGranted(this, WRITE_EXTERNAL_STORAGE)) {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},
                    "Request WRITE_EXTERNAL_STORAGE to write the data to external storage.",
                    REQ_WRITE_EXTERNAL_STORAGE);
        }
    }

    private void requestPermissions(@NonNull final String[] permissions,
                                    @Nullable final String explanation,
                                    final int requestCode) {
        // Should we show an explanation?
        if (PermissionUtils.shouldPermissionsShowRationale(this, permissions)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            if (explanation == null) {
                return;
            }
            // Display a AlertDialog with an explanation and a button to trigger the request.
            new AlertDialog.Builder(this)
                    .setTitle("Request Permissions")
                    .setMessage(explanation)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    permissions, requestCode);
                        }
                    })
                    .show();
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_WRITE_EXTERNAL_STORAGE) {
            if (PermissionUtils.verifyPermission(grantResults)) {
                // permission was granted, yay!
                // Do the related task you need to do.
                Snackbar.make(mPager, "Permission was granted, yay!",
                        Snackbar.LENGTH_SHORT).show();
            } else {
                // permission denied, boo!
                // Disable the functionality that depends on this permission.
                Snackbar.make(mPager, "Permission denied, boo!",
                        Snackbar.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        associateSearchable(searchItem);

        TintUtils.tintList(this, menu, R.color.bar_icon_color);

        return true;
    }

    /**
     * Associate searchable configuration with the SearchView.
     */
    private void associateSearchable(MenuItem searchItem) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_search: return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mSlidingPane.isOpen()) {
            mSlidingPane.closePane();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onFinish() {
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_status_bar_transparent:
                ContextUtils.startActivity(this, TransparentStatusBarActivity.class);
                break;
            case R.id.nav_snake_surface_view:
                ContextUtils.startActivity(this, SnakeSurfaceViewActivity.class);
                break;
            case R.id.nav_share:
            case R.id.nav_send:
                mSlidingPane.closePane();
                break;
        }
        return true;
    }

    public static class MainPagerAdapter extends TabFragmentPagerAdapter {

        private final int PAGE_COUNT = 3;

        public MainPagerAdapter(FragmentManager fm, Context ctx) {
            super(fm, ctx, R.layout.tab_custom_bar);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return WidgetFragment.newInstance();
                case 1: return FeverFragment.newInstance(1, PINK);
                case 2: return ArchFragment.newInstance(2, PURPLE);
                default: return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

        @Override
        public CharSequence getTabText(int position) {
            return null;
        }

        @Override
        public int getTabIcon(int position) {
            switch (position) {
                case 0: return R.drawable.ic_widgets;
                case 1: return R.drawable.ic_toys;
                case 2: return R.drawable.ic_pets;
                default: return 0;
            }
        }

    }

}
