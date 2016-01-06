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
import cc.cubone.turbo.core.util.Log;
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

import static cc.cubone.turbo.persistence.PrefAllApps.FLAG_DISPLAY_RUNNING;
import static cc.cubone.turbo.persistence.PrefAllApps.FLAG_DISPLAY_STOPPED;
import static cc.cubone.turbo.persistence.PrefAllApps.FLAG_DISPLAY_SYSTEM;
import static cc.cubone.turbo.persistence.PrefAllApps.FLAG_DISPLAY_USER;
import static cc.cubone.turbo.persistence.PrefAllApps.LAYOUT_GRID;
import static cc.cubone.turbo.persistence.PrefAllApps.LAYOUT_LIST;

public class AllAppsActivity extends BaseActivity implements PackageCallback,
        AppCardRecyclerViewAdapter.OnItemViewClickListener<AppCard> {

    static final String TAG = "AllAppsActivity";

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
        Log.i(TAG, "updateLayout: " + layout);
        int layoutNow = mPrefAllApps.getLayout();
        if (layout == layoutNow) {
            return; // same layout
        }
        mPrefAllApps.setLayout(layout);
        updateAdapter(layout, mPrefAllApps.getDisplayFlags());
    }

    private void updateDisplayFlag(int displayFlag, boolean checked) {
        Log.i(TAG, "updateDisplayFlag: 0x" + Integer.toHexString(displayFlag) + ", " + checked);
        int displayFlags = mPrefAllApps.getDisplayFlags();
        if (mRecyclerView.getAdapter() != null) {
            boolean included = (displayFlags & displayFlag) > 0;
            if (checked) {
                if (included) return;
            } else {
                if (!included) return;
            }
        }
        if (checked) {
            displayFlags = displayFlags | displayFlag;
        } else {
            displayFlags = displayFlags & (~displayFlag);
        }
        mPrefAllApps.setDisplayFlags(displayFlags);
        updateAdapter(mPrefAllApps.getLayout(), displayFlags);
    }

    private void updateAdapter() {
        updateAdapter(mPrefAllApps.getLayout(), mPrefAllApps.getDisplayFlags());
    }

    private void updateAdapter(int layout, int displayFlags) {
        Log.i(TAG, "updateAdapter: " + layout + ", 0x" + Integer.toHexString(displayFlags));
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

        AppCardRecyclerViewAdapter adapter = new AppCardRecyclerViewAdapter(
                createCards(displayFlags), layoutId);
        adapter.setOnItemViewClickListener(this);
        mRecyclerView.setAdapter(adapter);

        // update title appended with number of apps
        updateTitle(String.format("%s (%d)", getString(R.string.all_apps), adapter.getItemCount()));
    }

    private List<AppCard> createCards(int displayFlags) {
        List<AppCard> cards = new ArrayList<>();
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        AppCard appCard;
        boolean isSystem;
        boolean isStopped;
        for (ApplicationInfo info : packages) {
            isSystem = (info.flags & ApplicationInfo.FLAG_SYSTEM) > 0;
            if (isSystem) {
                if ((displayFlags & FLAG_DISPLAY_SYSTEM) == 0) continue;
            } else {
                if ((displayFlags & FLAG_DISPLAY_USER) == 0) continue;
            }
            isStopped = (info.flags & ApplicationInfo.FLAG_STOPPED) > 0;
            if (isStopped) {
                if ((displayFlags & FLAG_DISPLAY_STOPPED) == 0) continue;
            } else {
                if ((displayFlags & FLAG_DISPLAY_RUNNING) == 0) continue;
            }
            appCard = new AppCard(
                    info.loadLabel(pm).toString(),
                    info.packageName,
                    info.loadIcon(pm),
                    info);
            appCard.setType(isSystem ? AppCard.Type.SYSTEM : AppCard.Type.USER);
            appCard.setState(isStopped ? AppCard.State.STOPPED : AppCard.State.RUNNING);
            cards.add(appCard);
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
        if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
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
                            case R.string.launch: launch(); break;
                            case R.string.details: details(); break;
                            case R.string.uninstall: uninstall(); break;
                            case R.string.copy_app_name: copy(appName); break;
                            case R.string.copy_package_name: copy(info.packageName); break;
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
            case LAYOUT_GRID: menu.findItem(R.id.layout_grid).setChecked(true); break;
            case LAYOUT_LIST: menu.findItem(R.id.layout_list).setChecked(true); break;
        }
        // Issue: `setChecked(false)` still check the item :(
        //menu.findItem(R.id.layout_grid).setChecked(layout == LAYOUT_GRID);
        //menu.findItem(R.id.layout_list).setChecked(layout == LAYOUT_LIST);

        final int displayFlags = mPrefAllApps.getDisplayFlags();
        menu.findItem(R.id.display_user).setChecked((displayFlags & FLAG_DISPLAY_USER) > 0);
        menu.findItem(R.id.display_system).setChecked((displayFlags & FLAG_DISPLAY_SYSTEM) > 0);
        menu.findItem(R.id.display_running).setChecked((displayFlags & FLAG_DISPLAY_RUNNING) > 0);
        menu.findItem(R.id.display_stopped).setChecked((displayFlags & FLAG_DISPLAY_STOPPED) > 0);

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
            case R.id.display_user:
                toggleChecked(item);
                updateDisplayFlag(FLAG_DISPLAY_USER, item.isChecked());
                return true;
            case R.id.display_system:
                toggleChecked(item);
                updateDisplayFlag(FLAG_DISPLAY_SYSTEM, item.isChecked());
                return true;
            case R.id.display_running:
                toggleChecked(item);
                updateDisplayFlag(FLAG_DISPLAY_RUNNING, item.isChecked());
                return true;
            case R.id.display_stopped:
                toggleChecked(item);
                updateDisplayFlag(FLAG_DISPLAY_STOPPED, item.isChecked());
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
