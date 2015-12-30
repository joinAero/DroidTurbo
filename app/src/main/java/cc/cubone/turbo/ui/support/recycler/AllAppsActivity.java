package cc.cubone.turbo.ui.support.recycler;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.model.DataCard;
import cc.cubone.turbo.persistence.PrefAllApps;
import cc.cubone.turbo.ui.base.BaseActivity;
import cc.cubone.turbo.view.CardRecyclerViewAdapter;

import static cc.cubone.turbo.persistence.PrefAllApps.DISPLAY_ALL;
import static cc.cubone.turbo.persistence.PrefAllApps.DISPLAY_USER;
import static cc.cubone.turbo.persistence.PrefAllApps.LAYOUT_GRID;
import static cc.cubone.turbo.persistence.PrefAllApps.LAYOUT_LIST;

public class AllAppsActivity extends BaseActivity {

    private final int SPAN_COUNT = 3;

    private RecyclerView mRecyclerView;
    private PrefAllApps mPrefAllApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);
        mPrefAllApps = new PrefAllApps(this);
        initToolbar();
        initViews();
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        updateAdapter(mPrefAllApps.getLayout(), mPrefAllApps.getDisplay());
    }

    private void updateLayout(int layout) {
        int layoutNow = mPrefAllApps.getLayout();
        if (layout == layoutNow) {
            return; // same layout
        }
        mPrefAllApps.setLayout(layout);
        updateAdapter(layout, mPrefAllApps.getDisplay());
    }

    private void updateDisplay(int display) {
        if (mRecyclerView.getAdapter() != null) {
            int displayNow = mPrefAllApps.getDisplay();
            if (display == displayNow) {
                return; // same display
            }
        }
        mPrefAllApps.setDisplay(display);
        updateAdapter(mPrefAllApps.getLayout(), display);
    }

    private void updateAdapter(int layout, int display) {
        int layoutId;
        if (layout == LAYOUT_LIST) {
            layoutId = R.layout.item_app;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setHasFixedSize(true);
        } else { // grid
            layoutId = R.layout.item_app_cell;
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                    SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL));
            mRecyclerView.setHasFixedSize(false);
        }

        boolean onlyUser = (display == DISPLAY_USER);
        CardRecyclerViewAdapter<DataCard<ApplicationInfo>> adapter =
                new CardRecyclerViewAdapter<>(createCards(onlyUser), layoutId);
        mRecyclerView.setAdapter(adapter);
    }

    private List<DataCard<ApplicationInfo>> createCards(boolean onlyUser) {
        List<DataCard<ApplicationInfo>> cards = new ArrayList<>();
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        for (ApplicationInfo info : packages) {
            if (onlyUser) {
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    continue; // System apps
                }
            }
            cards.add(new DataCard<>(
                    info.loadLabel(pm).toString(),
                    info.packageName,
                    info.loadIcon(pm),
                    info));
        }
        Collections.sort(cards, new Comparator<DataCard<ApplicationInfo>>() {
            @Override
            public int compare(DataCard<ApplicationInfo> lhs, DataCard<ApplicationInfo> rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
        return cards;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_apps, menu);

        final int layout = mPrefAllApps.getLayout();
        switch (layout) {
            case LAYOUT_GRID: menu.findItem(R.id.layout_grid).setChecked(true); break;
            case LAYOUT_LIST: menu.findItem(R.id.layout_list).setChecked(true); break;
        }
        // Issue: `setChecked(false)` still check the item :(
        //menu.findItem(R.id.layout_grid).setChecked(layout == LAYOUT_GRID);
        //menu.findItem(R.id.layout_list).setChecked(layout == LAYOUT_LIST);

        final int display = mPrefAllApps.getDisplay();
        switch (display) {
            case DISPLAY_USER: menu.findItem(R.id.display_user).setChecked(true); break;
            case DISPLAY_ALL: menu.findItem(R.id.display_all).setChecked(true); break;
        }
        //menu.findItem(R.id.display_user).setChecked(display == DISPLAY_USER);
        //menu.findItem(R.id.display_all).setChecked(display == DISPLAY_ALL);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.layout_list:
                toggleChecked(item);
                updateLayout(LAYOUT_LIST);
                return true;
            case R.id.layout_grid:
                toggleChecked(item);
                updateLayout(LAYOUT_GRID);
                return true;
            case R.id.display_all:
                toggleChecked(item);
                updateDisplay(DISPLAY_ALL);
                return true;
            case R.id.display_user:
                toggleChecked(item);
                updateDisplay(DISPLAY_USER);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleChecked(MenuItem item) {
        item.setChecked(!item.isChecked());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}