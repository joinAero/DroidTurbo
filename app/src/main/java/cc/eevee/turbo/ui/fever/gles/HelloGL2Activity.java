package cc.eevee.turbo.ui.fever.gles;

import android.os.Bundle;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.eevee.turbo.R;
import cc.eevee.turbo.libgldraw.HelloGL2View;
import cc.eevee.turbo.ui.base.BaseActivity;

public class HelloGL2Activity extends BaseActivity {

    @BindView(R.id.surface) HelloGL2View mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_gl2);
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
