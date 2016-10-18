package cc.cubone.turbo.ui.widget.support;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.core.rom.RomCompat;
import cc.cubone.turbo.core.util.Log;
import cc.cubone.turbo.core.util.SysUtils;
import cc.cubone.turbo.function.Action;
import cc.cubone.turbo.model.AppInfo;
import cc.cubone.turbo.model.DataInfo;
import cc.cubone.turbo.persistence.PrefAllApps;
import cc.cubone.turbo.receiver.PackageBroadcast;
import cc.cubone.turbo.ui.ActionDialogFragment;
import cc.cubone.turbo.ui.base.BaseActivity;
import cc.cubone.turbo.util.ContextUtils;
import cc.cubone.turbo.util.TintUtils;
import cc.cubone.turbo.util.ToastUtils;
import cc.cubone.turbo.view.InfoRecyclerViewAdapter;

import static cc.cubone.turbo.persistence.PrefAllApps.FLAG_DISPLAY_RUNNING;
import static cc.cubone.turbo.persistence.PrefAllApps.FLAG_DISPLAY_STOPPED;
import static cc.cubone.turbo.persistence.PrefAllApps.FLAG_DISPLAY_SYSTEM;
import static cc.cubone.turbo.persistence.PrefAllApps.FLAG_DISPLAY_USER;
import static cc.cubone.turbo.persistence.PrefAllApps.LAYOUT_GRID;
import static cc.cubone.turbo.persistence.PrefAllApps.LAYOUT_LIST;
import static cc.cubone.turbo.persistence.PrefAllApps.SORT_NAME;
import static cc.cubone.turbo.persistence.PrefAllApps.SORT_PACKAGE;

public class AllAppsActivity extends BaseActivity implements PackageBroadcast.Callback,
        InfoRecyclerViewAdapter.OnItemViewClickListener<AppInfo> {

    static final String TAG = "AllAppsActivity";

    static final SparseIntArray ACTION_INDEXES;
    static {
        ACTION_INDEXES = new SparseIntArray(10);
        ACTION_INDEXES.append(0, R.string.launch);
        ACTION_INDEXES.append(1, R.string.details);
        ACTION_INDEXES.append(2, R.string.uninstall);
        ACTION_INDEXES.append(3, R.string.copy_app_name);
        ACTION_INDEXES.append(4, R.string.copy_package_name);
        ACTION_INDEXES.append(5, R.string.permission_manager);
        ACTION_INDEXES.append(6, R.string.permission_autostart);
        ACTION_INDEXES.append(7, R.string.permission_floatwindow);
        ACTION_INDEXES.append(8, R.string.permission_root);
    }

    private final int SPAN_COUNT = 3;

    @BindView(R.id.recycler) RecyclerView mRecyclerView;

    private PrefAllApps mPrefAllApps;

    private PackageBroadcast.Receiver mPackageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);
        ButterKnife.bind(this);
        mPrefAllApps = new PrefAllApps(this);
        initViews();
    }

    private void initViews() {
        initToolbar();
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
        updateAdapter(layout, mPrefAllApps.getDisplayFlags(), mPrefAllApps.getSort());
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
        updateAdapter(mPrefAllApps.getLayout(), displayFlags, mPrefAllApps.getSort());
    }

    private void updateSort(int sort) {
        Log.i(TAG, "updateSort: " + sort);
        int sortNow = mPrefAllApps.getSort();
        if (sort == sortNow) {
            return; // same sort
        }
        mPrefAllApps.setSort(sort);
        updateAdapter(mPrefAllApps.getLayout(), mPrefAllApps.getDisplayFlags(), sort);
    }

    private void updateAdapter() {
        updateAdapter(mPrefAllApps.getLayout(), mPrefAllApps.getDisplayFlags(), mPrefAllApps.getSort());
    }

    private void updateAdapter(int layout, int displayFlags, int sort) {
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

        InfoRecyclerViewAdapter<AppInfo, InfoRecyclerViewAdapter.ViewHolder2> adapter =
                InfoRecyclerViewAdapter.create(createAppInfos(displayFlags, sort), layoutId,
                        (view, appInfo) -> view.setText(appInfo.getType().name().toLowerCase()
                                + ", " + appInfo.getState().name().toLowerCase()));
        adapter.setOnItemViewClickListener(this);
        mRecyclerView.setAdapter(adapter);

        // update title appended with number of apps
        updateTitle(String.format("%s (%d)", getString(R.string.all_apps), adapter.getItemCount()));

        // async load apps with rx
        //loadApps(mRecyclerView, displayFlags, layoutId);
    }

    private List<AppInfo> createAppInfos(int displayFlags, int sort) {
        List<AppInfo> infos = new ArrayList<>();
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        AppInfo appInfo;
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
            appInfo = new AppInfo(
                    info.loadLabel(pm).toString(),
                    info.packageName,
                    info.loadIcon(pm),
                    info);
            appInfo.setType(isSystem ? AppInfo.Type.SYSTEM : AppInfo.Type.USER);
            appInfo.setState(isStopped ? AppInfo.State.STOPPED : AppInfo.State.RUNNING);
            infos.add(appInfo);
        }
        sort(infos, sort);
        return infos;
    }

    private void sort(List<AppInfo> infos, int sort) {
        if (sort == SORT_PACKAGE) {
            Collections.sort(infos, new Comparator<DataInfo<ApplicationInfo>>() {
                @Override
                public int compare(DataInfo<ApplicationInfo> lhs, DataInfo<ApplicationInfo> rhs) {
                    return lhs.getDescription().compareTo(rhs.getDescription());
                }
            });
        } else { // SORT_NAME
            Collections.sort(infos, new Comparator<DataInfo<ApplicationInfo>>() {
                @Override
                public int compare(DataInfo<ApplicationInfo> lhs, DataInfo<ApplicationInfo> rhs) {
                    return lhs.getTitle().compareTo(rhs.getTitle());
                }
            });
        }
    }

    @Override
    public void onItemViewClick(View view, int position, AppInfo data) {
        final SparseArray<Action> actions = createActions(data);
        final int size = actions.size();
        if (size <= 0) return;

        final Integer[] keys = new Integer[size];
        for (int i = 0; i < size; i++) {
            keys[i] = actions.keyAt(i);
        }
        Arrays.sort(keys, (lhs, rhs) -> ACTION_INDEXES.indexOfValue(lhs) - ACTION_INDEXES.indexOfValue(rhs));
        final int[] sortedKeys = new int[size];
        for (int i = 0; i < size; i++) {
            sortedKeys[i] = keys[i];
        }

        ActionDialogFragment.make(data.getTitle(), sortedKeys).setOnActionSelectListener(
                (dialog, action) -> {
                    Action act = actions.get(action);
                    if (act != null) act.exec();
                    dialog.dismiss();
                }).show(getSupportFragmentManager());
    }

    private SparseArray<Action> createActions(AppInfo data) {
        SparseArray<Action> actions = new SparseArray<>();

        String appName = data.getTitle();
        ApplicationInfo info = data.getData();

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(info.packageName);
        Intent detailsIntent = RomCompat.toAppDetails(this, info.packageName);

        if (launchIntent != null) {
            actions.put(R.string.launch, () -> ContextUtils.startActivity(this, launchIntent));
        }
        if (detailsIntent != null) {
            actions.put(R.string.details, () -> ContextUtils.startActivity(this, detailsIntent));
        }
        if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            actions.put(R.string.uninstall, () -> uninstall(info.packageName));
        }
        actions.put(R.string.copy_app_name, () -> copy(appName));
        actions.put(R.string.copy_package_name, () -> copy(info.packageName));

        // permission intents
        Intent permsIntent = RomCompat.toPermissionManager(this, info.packageName);
        Intent permAutoStartIntent = RomCompat.toAutoStartPermission(this, info.packageName);
        Intent permFloatWindowIntent = RomCompat.toFloatWindowPermission(this, info.packageName);
        Intent permRootIntent = RomCompat.toRootPermission(this, info.packageName);

        if (permsIntent != null) {
            actions.put(R.string.permission_manager, () -> ContextUtils.startActivity(this, permsIntent));
        }
        if (permAutoStartIntent != null) {
            actions.put(R.string.permission_autostart, () -> ContextUtils.startActivity(this, permAutoStartIntent));
        }
        if (permFloatWindowIntent != null) {
            actions.put(R.string.permission_floatwindow, () -> ContextUtils.startActivity(this, permFloatWindowIntent));
        }
        if (permRootIntent != null && SysUtils.isRooted()) {
            actions.put(R.string.permission_root, () -> ContextUtils.startActivity(this, permRootIntent));
        }

        return actions;
    }

    private void uninstall(String packageName) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        ContextUtils.startActivity(this, intent);
    }

    private void copy(String text) {
        // Copy and Paste: http://developer.android.com/guide/topics/text/copy-paste.html
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(text, text);
        clipboard.setPrimaryClip(clip);
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

        final int sort = mPrefAllApps.getSort();
        switch (sort) {
            case SORT_NAME: menu.findItem(R.id.sort_name).setChecked(true); break;
            case SORT_PACKAGE: menu.findItem(R.id.sort_package).setChecked(true); break;
        }

        TintUtils.tintList(this, menu.findItem(R.id.refresh), R.color.bar_icon_color);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        if (itemId == R.id.refresh) {
            updateAdapter();
            return true;
        } else {
            toggleChecked(item);
            switch (itemId) {
                case R.id.layout_list:
                    updateLayout(LAYOUT_LIST);
                    return true;
                case R.id.layout_grid:
                    updateLayout(LAYOUT_GRID);
                    return true;
                case R.id.display_user:
                    updateDisplayFlag(FLAG_DISPLAY_USER, item.isChecked());
                    return true;
                case R.id.display_system:
                    updateDisplayFlag(FLAG_DISPLAY_SYSTEM, item.isChecked());
                    return true;
                case R.id.display_running:
                    updateDisplayFlag(FLAG_DISPLAY_RUNNING, item.isChecked());
                    return true;
                case R.id.display_stopped:
                    updateDisplayFlag(FLAG_DISPLAY_STOPPED, item.isChecked());
                    return true;
                case R.id.sort_name:
                    updateSort(SORT_NAME);
                    return true;
                case R.id.sort_package:
                    updateSort(SORT_PACKAGE);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    }

    private void toggleChecked(MenuItem item) {
        item.setChecked(!item.isChecked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAdapter();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPackageReceiver = new PackageBroadcast.Receiver(this);
        mPackageReceiver.register(this);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPackageReceiver.unregister();
        mPackageReceiver = null;
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

    /*private void loadApps(final RecyclerView recyclerView,
                          final int displayFlags,
                          @LayoutRes final int itemResId) {
        Observable.create(new Observable.OnSubscribe<List<AppInfo>>() {
            @Override
            public void call(Subscriber<? super List<AppInfo>> subscriber) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                subscriber.onNext(createAppInfos(displayFlags));
            }
        })
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<AppInfo>>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(List<AppInfo> dataList) {
                InfoRecyclerViewAdapter<AppInfo, InfoRecyclerViewAdapter.ViewHolder2> adapter =
                        InfoRecyclerViewAdapter.create(dataList, itemResId,
                                (view, appInfo) -> {
                                    view.setText(appInfo.getType().name().toLowerCase()
                                            + ", " + appInfo.getState().name().toLowerCase());
                                });
                adapter.setOnItemViewClickListener(AllAppsActivity.this);
                recyclerView.setAdapter(adapter);

                // update title appended with number of apps
                updateTitle(String.format("%s (%d)", getString(R.string.all_apps), adapter.getItemCount()));
            }
        });
    }*/
}
