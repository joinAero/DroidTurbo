package cc.eevee.turbo.ui.widget.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cc.eevee.turbo.R;
import cc.eevee.turbo.core.app.SightFragment;
import cc.eevee.turbo.core.util.Log;
import cc.eevee.turbo.util.ToastUtils;
import cc.eevee.turbo.widget.ArrowView;
import cc.eevee.turbo.widget.CircleView;
import cc.eevee.turbo.widget.HaloView;
import cc.eevee.turbo.widget.PlateView;

public class CustomFragment extends SightFragment {

    private Animator mPlateAnim;
    private Unbinder mUnbinder;

    public CustomFragment() {
    }

    public static CustomFragment newInstance() {
        return new CustomFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_custom, container, false);
    }

    @Override
    protected void onViewCreatedFirstSight(View view) {
        mUnbinder = ButterKnife.bind(this, view);
    }

    @OnClick(R.id.halo)
    public void onHaloViewClick(HaloView v) {
        //noinspection ConstantConditions
        ToastUtils.show(getContext(), Log.element().getMethodName());
        v.startAnim();
    }

    @OnClick(R.id.plate)
    public void onPlateViewClick(PlateView v) {
        //noinspection ConstantConditions
        ToastUtils.show(getContext(), Log.element().getMethodName());
        if (mPlateAnim != null) {
            mPlateAnim.end();
            mPlateAnim = null;
        }
        final int r = v.getPlateRadius(), s = r * 4;
        ValueAnimator anim = ValueAnimator.ofFloat(0, 2);
        anim.addUpdateListener(animation -> {
            float ratio = (Float) animation.getAnimatedValue();
            if (ratio > 1) ratio = 2 - ratio;
            v.setPlateRadius((int) (r * (1-ratio)));
            v.setPlateBorderBlankSize((int) (s * ratio));
            v.invalidate();
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPlateAnim = null;
            }
        });
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(2000);
        anim.start();
        mPlateAnim = anim;
    }

    @OnClick(R.id.circle)
    public void onCircleViewClick(CircleView v) {
        //noinspection ConstantConditions
        ToastUtils.show(getContext(), Log.element().getMethodName());
        if (v.isAnimated()) {
            v.stopAnim();
        } else {
            v.startAnim();
        }
    }

    @OnClick(R.id.layout_arrows)
    public void onLayoutArrowsClick(ViewGroup v) {
        //noinspection ConstantConditions
        ToastUtils.show(getContext(), Log.element().getMethodName());
        for (int i = 0; i < v.getChildCount(); ++i) {
            View child = v.getChildAt(i);
            if (child instanceof ArrowView) {
                ArrowView arrow = ((ArrowView) child);
                RotateAnimation anim = new RotateAnimation(0, 360,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setFillEnabled(false);
                anim.setDuration(1000);
                arrow.startAnimation(anim);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
