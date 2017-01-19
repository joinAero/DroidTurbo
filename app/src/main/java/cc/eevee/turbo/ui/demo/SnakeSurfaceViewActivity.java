package cc.eevee.turbo.ui.demo;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.transition.Transition;

import butterknife.BindView;
import butterknife.ButterKnife;
import cc.eevee.turbo.R;
import cc.eevee.turbo.ui.base.BaseActivity;
import cc.eevee.turbo.ui.demo.snake.SnakeSurfaceView;

public class SnakeSurfaceViewActivity extends BaseActivity {

    @BindView(R.id.surface) SnakeSurfaceView mSnakeSurfaceView;

    private boolean mResumeSkipped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake_surface_view);
        ButterKnife.bind(this);
        initToolbar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().addListener(new TransitionListenerAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    mSnakeSurfaceView.onResume();
                }
            });
            mResumeSkipped = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mResumeSkipped) {
            mResumeSkipped = false;
        } else {
            mSnakeSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSnakeSurfaceView.onPause();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static class TransitionListenerAdapter implements Transition.TransitionListener {
        @Override
        public void onTransitionStart(Transition transition) {
        }
        @Override
        public void onTransitionEnd(Transition transition) {
        }
        @Override
        public void onTransitionCancel(Transition transition) {
        }
        @Override
        public void onTransitionPause(Transition transition) {
        }
        @Override
        public void onTransitionResume(Transition transition) {
        }
    }

}
