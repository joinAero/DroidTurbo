package cc.cubone.turbo.ui.widget.design;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.ui.base.BaseActivity;

/**
 * <ul>See also:
 * <li><a href="https://github.com/rubensousa/BottomSheetExample">BottomSheetExample</a>
 * <li><a href="https://github.com/miguelhincapie/CustomBottomSheetBehavior">CustomBottomSheetBehavior</a>
 * </ul>
 */
public class BottomSheetActivity extends BaseActivity {

    @Bind(R.id.bottom_sheet) View mBottomSheet;
    AppBarLayout mBottomSheetBarLayout;
    Toolbar mBottomSheetToolbar;

    private BottomSheetBehavior mBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        initToolbar();
        mBehavior = BottomSheetBehavior.from(mBottomSheet);
        mBottomSheetBarLayout = ButterKnife.findById(mBottomSheet, R.id.layout_bar);
        mBottomSheetToolbar = ButterKnife.findById(mBottomSheetBarLayout, R.id.tool_bar);
        holdStatusBar(mBottomSheetBarLayout);
    }

}
