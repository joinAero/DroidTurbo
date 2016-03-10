package cc.cubone.turbo.ui.demo;

import android.os.Bundle;

import butterknife.Bind;
import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.ui.base.BaseActivity;
import cc.cubone.turbo.ui.demo.snake.SnakeSurfaceView;

public class SnakeSurfaceViewActivity extends BaseActivity {

    @Bind(R.id.surface) SnakeSurfaceView mSnakeSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake_surface_view);
        ButterKnife.bind(this);
        initToolbar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSnakeSurfaceView.onPause();
    }
}
