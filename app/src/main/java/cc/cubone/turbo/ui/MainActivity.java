package cc.cubone.turbo.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.core.rom.MIUIUtils;
import cc.cubone.turbo.core.view.TabFragmentPagerAdapter;
import cc.cubone.turbo.ui.arch.ArchFragment;
import cc.cubone.turbo.ui.base.BaseActivity;
import cc.cubone.turbo.ui.fever.FeverFragment;
import cc.cubone.turbo.ui.support.SupportFragment;
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

    @Bind(R.id.drawer) DrawerLayout mDrawer;
    @Bind(R.id.pager) ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();
        requestPermissions();
    }

    private void initViews() {
        initToolbar();

        FloatingActionButton fab = ButterKnife.findById(this, R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        NavigationView nav = ButterKnife.findById(this, R.id.nav);
        nav.setNavigationItemSelectedListener(this);

        if (MIUIUtils.isMIUI()) {
            View content = ButterKnife.findById(this, R.id.content);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) content.getLayoutParams();
            lp.topMargin = getStatusBarHeight();
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onToolbarCreated(Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(adapter);
        // retain all pagers
        mPager.setOffscreenPageLimit(adapter.getCount());

        TabLayout barTab = ButterKnife.findById(this, R.id.tab);
        barTab.setupWithViewPager(mPager);
        adapter.customTabViews(barTab);

        // How to set StatusBar to transparent?
        // Issue: Could not set StatusBar to transparent if using DrawerLayout
        // Google: DrawerLayout setStatusBarBackground
        /*drawer.setScrimColor(Color.TRANSPARENT);
        drawer.setStatusBarBackgroundColor(Color.TRANSPARENT);*/
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
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        mDrawer.closeDrawer(GravityCompat.START);
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
                case 0: return SupportFragment.newInstance();
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
