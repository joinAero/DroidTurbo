package cc.cubone.turbo.ui.widget.design;

import android.os.Bundle;

import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.ui.base.BaseActivity;

/**
 * <ul>See also:
 * <li><a href="https://github.com/rubensousa/BottomSheetExample">BottomSheetExample</a>
 * </ul>
 */
public class BottomSheetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_sheet);
        ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        initToolbar();
    }
}
