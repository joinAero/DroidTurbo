package cc.eevee.turbo.ui.fever.gles;

import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.eevee.turbo.R;
import cc.eevee.turbo.libgldraw.HelloCubeView;
import cc.eevee.turbo.ui.base.BaseActivity;

public class HelloCubeActivity extends BaseActivity {

    @BindView(R.id.surface) HelloCubeView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_cube);
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
