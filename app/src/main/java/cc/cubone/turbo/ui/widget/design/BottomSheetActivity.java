package cc.cubone.turbo.ui.widget.design;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

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
    private int mBottomSheetBarLayoutHeight;

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
        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                setViewHeight(mBottomSheetBarLayout,
                        (int) (mBottomSheetBarLayoutHeight * slideOffset));
            }
        });
        mBottomSheetBarLayout = ButterKnife.findById(mBottomSheet, R.id.layout_bar);
        mBottomSheetToolbar = ButterKnife.findById(mBottomSheetBarLayout, R.id.tool_bar);
        mBottomSheetToolbar.setTitle(R.string.bottom_sheet);
        mBottomSheetToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_clear_material);
        mBottomSheetToolbar.setNavigationOnClickListener(v -> mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED));
        holdStatusBar(mBottomSheetBarLayout);
        mBottomSheetBarLayout.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mBottomSheetBarLayoutHeight = mBottomSheetBarLayout.getMeasuredHeight();
        setViewHeight(mBottomSheetBarLayout, 0);
    }

    private void setViewHeight(View v, int h) {
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.height = h;
        v.setLayoutParams(params);
    }
}
