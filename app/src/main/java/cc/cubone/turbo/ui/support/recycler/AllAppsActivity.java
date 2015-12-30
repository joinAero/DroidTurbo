package cc.cubone.turbo.ui.support.recycler;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cc.cubone.turbo.R;
import cc.cubone.turbo.model.DataCard;
import cc.cubone.turbo.ui.base.BaseActivity;
import cc.cubone.turbo.view.CardRecyclerViewAdapter;

public class AllAppsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);
        initToolbar();
        initViews();
    }

    private void initViews() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        CardRecyclerViewAdapter<DataCard<ApplicationInfo>> adapter = new CardRecyclerViewAdapter<>(
                createCards(), R.layout.item_app);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);
    }

    private List<DataCard<ApplicationInfo>> createCards() {
        List<DataCard<ApplicationInfo>> cards = new ArrayList<>();
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        for (ApplicationInfo info : packages) {
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                // System apps
            } else {
                // User apps
                cards.add(new DataCard<>(
                        info.loadLabel(pm).toString(),
                        info.packageName,
                        info.loadIcon(pm),
                        info));
            }
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}