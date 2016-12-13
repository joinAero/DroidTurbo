package cc.eevee.turbo.ui.fever.ndk;

import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.eevee.turbo.R;
import cc.eevee.turbo.libsanangeles.SanAngelesGLSurfaceView;
import cc.eevee.turbo.ui.base.BaseActivity;

public class SanAngelesActivity extends BaseActivity {

    @BindView(R.id.surface) SanAngelesGLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_san_angeles);
        ButterKnife.bind(this);
        initToolbar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

}
