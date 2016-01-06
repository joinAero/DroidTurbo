package cc.cubone.turbo.ui.support.recycler;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.model.AppCard;
import cc.cubone.turbo.model.DataCard;
import cc.cubone.turbo.persistence.PrefAllApps;
import cc.cubone.turbo.receiver.PackageCallback;
import cc.cubone.turbo.receiver.PackageListener;
import cc.cubone.turbo.ui.ActionDialogFragment;
import cc.cubone.turbo.ui.base.BaseActivity;
import cc.cubone.turbo.util.ContextUtils;
import cc.cubone.turbo.util.ToastUtils;
import cc.cubone.turbo.view.AppCardRecyclerViewAdapter;

import static cc.cubone.turbo.persistence.PrefAllApps.DISPLAY_ALL;
import static cc.cubone.turbo.persistence.PrefAllApps.DISPLAY_USER;
import static cc.cubone.turbo.persistence.PrefAllApps.LAYOUT_GRID;
import static cc.cubone.turbo.persistence.PrefAllApps.LAYOUT_LIST;

public class AllAppsActivity extends BaseActivity implements PackageCallback,
        AppCardRecyclerViewAdapter.OnItemViewClickListener<AppCard> {

    private final int SPAN_COUNT = 3;

    private RecyclerView mRecyclerView;
    private PrefAllApps mPrefAllApps;

    private PackageListener mPackageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);
        mPrefAllApps = new PrefAllApps(this);
        initToolbar();
        initViews();
        mPackageListener = new PackageListener(this);
        mPackageListener.register(this);
    }

    private void initViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        updateAdapter();
    }

    private void updateTitle(CharSequence title) {
        ActionBar ab = getSupportActionBar();
        if (ab == null) return;
        ab.setTitle(title);
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

    private void updateAdapter() {
        updateAdapter(mPrefAllApps.getLayout(), mPrefAllApps.getDisplay());
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
        AppCardRecyclerViewAdapter adapter = new AppCardRecyclerViewAdapter(
                createCards(onlyUser), layoutId);
        adapter.setOnItemViewClickListener(this);
        mRecyclerView.setAdapter(adapter);

        // update title appended with number of apps
        updateTitle(String.format("%s (%d)", getString(R.string.all_apps), adapter.getItemCount()));
    }

    private List<AppCard> createCards(boolean onlyUser) {
        List<AppCard> cards = new ArrayList<>();
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        for (ApplicationInfo info : packages) {
            if (onlyUser) {
                if (isSystemApp(info)) {
                    continue; // System apps
                }
            }
            cards.add(new AppCard(
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

    private boolean isSystemApp(ApplicationInfo info) {
        return (info.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
    }

    @Override
    public void onItemViewClick(View view, int position, AppCard data) {
        final String appName = data.getTitle();
        final ApplicationInfo info = data.getData();

        final Intent launchIntent = getPackageManager()
                .getLaunchIntentForPackage(info.packageName);
        final Intent detailsIntent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + info.packageName));

        ArrayList<Integer> actions = new ArrayList<>();
        if (launchIntent != null) {
            actions.add(R.string.launch);
        }
        actions.add(R.string.details);
        if (!isSystemApp(info)) {
            actions.add(R.string.uninstall);
        }
        actions.add(R.string.copy_app_name);
        actions.add(R.string.copy_package_name);

        final int size = actions.size();
        int[] ints = new int[size];
        for (int i = 0; i < size; i++) {
            ints[i] = actions.get(i);
        }

        ActionDialogFragment.make(data.getTitle(), ints).setOnActionSelectListener(
                new ActionDialogFragment.OnActionSelectListener() {
                    AllAppsActivity context = AllAppsActivity.this;

                    @Override
                    public void onActionSelect(ActionDialogFragment dialog, int action) {
                        switch (action) {
                            case R.string.launch:
                                launch();
                                break;
                            case R.string.details:
                                details();
                                break;
                            case R.string.uninstall:
                                uninstall();
                                break;
                            case R.string.copy_app_name:
                                copy(appName);
                                break;
                            case R.string.copy_package_name:
                                copy(info.packageName);
                                break;
                        }
                        dialog.dismiss();
                    }

                    private void launch() {
                        ContextUtils.startActivity(context, launchIntent);
                    }

                    private void details() {
                        ContextUtils.startActivity(context, detailsIntent);
                    }

                    private void uninstall() {
                        Uri uri = Uri.fromParts("package", info.packageName, null);
                        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                        ContextUtils.startActivity(context, intent);
                    }

                    private void copy(String text) {
                        // Copy and Paste: http://developer.android.com/guide/topics/text/copy-paste.html
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText(text, text);
                        clipboard.setPrimaryClip(clip);
                    }
                }).show(getSupportFragmentManager());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_apps, menu);

        final int layout = mPrefAllApps.getLayout();
        switch (layout) {
            case LAYOUT_GRID:
                menu.findItem(R.id.layout_grid).setChecked(true);
                break;
            case LAYOUT_LIST:
                menu.findItem(R.id.layout_list).setChecked(true);
                break;
        }
        // Issue: `setChecked(false)` still check the item :(
        //menu.findItem(R.id.layout_grid).setChecked(layout == LAYOUT_GRID);
        //menu.findItem(R.id.layout_list).setChecked(layout == LAYOUT_LIST);

        final int display = mPrefAllApps.getDisplay();
        switch (display) {
            case DISPLAY_USER:
                menu.findItem(R.id.display_user).setChecked(true);
                break;
            case DISPLAY_ALL:
                menu.findItem(R.id.display_all).setChecked(true);
                break;
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
    protected void onDestroy() {
        super.onDestroy();
        mPackageListener.unregister();
        mPackageListener = null;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onPackageAdded(String packageName) {
        ToastUtils.show(this, "add " + packageName);
        updateAdapter();
    }

    @Override
    public void onPackageChanged(String packageName) {
        ToastUtils.show(this, "change " + packageName);
        updateAdapter();
    }

    @Override
    public void onPackageRemoved(String packageName) {
        ToastUtils.show(this, "remove " + packageName);
        updateAdapter();
    }

    @Override
    public void onPackagesAvailable(String[] packages, boolean replacing) {
        updateAdapter();
    }

    @Override
    public void onPackagesUnavailable(String[] packages, boolean replacing) {
        updateAdapter();
    }

}
