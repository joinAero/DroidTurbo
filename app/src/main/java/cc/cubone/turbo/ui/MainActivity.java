package cc.cubone.turbo.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import cc.cubone.turbo.R;
import cc.cubone.turbo.core.view.TabFragmentPagerAdapter;
import cc.cubone.turbo.ui.arch.ArchFragment;
import cc.cubone.turbo.ui.base.BaseActivity;
import cc.cubone.turbo.ui.fever.FeverFragment;
import cc.cubone.turbo.ui.support.SupportFragment;
import cc.cubone.turbo.util.TintUtils;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initViews();
    }

    @Override
    protected void onToolbarCreated(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        pager.setAdapter(adapter);
        // retain all pagers
        pager.setOffscreenPageLimit(adapter.getCount());

        TabLayout barTabs = (TabLayout) toolbar.findViewById(R.id.tab);
        barTabs.setupWithViewPager(pager);
        adapter.customTabViews(barTabs);

        // How to set StatusBar to transparent?
        // Issue: Could not set StatusBar to transparent if using DrawerLayout
        // Google: DrawerLayout setStatusBarBackground
        /*drawer.setScrimColor(Color.TRANSPARENT);
        drawer.setStatusBarBackgroundColor(Color.TRANSPARENT);*/
    }

    public void initViews() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        NavigationView nav = (NavigationView) findViewById(R.id.nav);
        nav.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
