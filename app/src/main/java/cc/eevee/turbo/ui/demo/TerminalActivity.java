package cc.eevee.turbo.ui.demo;

import android.os.Bundle;

import butterknife.ButterKnife;
import cc.eevee.turbo.R;
import cc.eevee.turbo.ui.base.BaseActivity;

public class TerminalActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        ButterKnife.bind(this);
        initToolbar();
    }
}
