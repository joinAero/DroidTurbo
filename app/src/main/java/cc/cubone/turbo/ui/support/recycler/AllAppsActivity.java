package cc.cubone.turbo.ui.support.recycler;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cc.cubone.turbo.R;
import cc.cubone.turbo.ui.base.BaseActivity;

public class AllAppsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_apps);
        initToolbar();
        initViews();
    }

    @Override
    protected void onToolbarCreated(Toolbar toolbar) {
        super.onToolbarCreated(toolbar);
    }

    private void initViews() {
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        recycler.setAdapter(new RecyclerViewAdapter());
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public static class RecyclerViewAdapter extends
            RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView v = new TextView(parent.getContext());
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TextView v = (TextView) holder.itemView;
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                    v.getResources().getDisplayMetrics());
            v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
            v.setGravity(Gravity.CENTER_VERTICAL);
            v.setText("Item " + position);
        }

        @Override
        public int getItemCount() {
            return 20;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public ViewHolder(View itemView) {
                super(itemView);
            }

        }

    }

}