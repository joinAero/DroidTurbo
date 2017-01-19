package cc.eevee.turbo.ui.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.ButterKnife;
import cc.eevee.turbo.R;
import cc.eevee.turbo.core.rom.MIUIUtils;
import cc.eevee.turbo.core.util.UIUtils;

public class BaseActivity extends AppCompatActivity {

    @IntDef({STATUS_BAR_CLIP_TO, STATUS_BAR_HOLD_PLACE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StatusBarMode {}

    public static final int STATUS_BAR_CLIP_TO = 0;
    public static final int STATUS_BAR_HOLD_PLACE = 1;

    //private int mStatusBarMode;
    private boolean mStatusBarTinted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Toolbar initToolbar() {
        return initToolbar(R.id.tool_bar, STATUS_BAR_CLIP_TO);
    }

    protected Toolbar initToolbar(@StatusBarMode int statusBarMode) {
        return initToolbar(R.id.tool_bar, statusBarMode);
    }

    protected Toolbar initToolbar(@IdRes int toolbarId, @StatusBarMode int statusBarMode) {
        // Toolbar: http://developer.android.com/reference/android/support/v7/widget/Toolbar.html
        // Adding the App Bar: http://developer.android.com/training/appbar/index.html
        // Using the App ToolBar: https://guides.codepath.com/android/Using-the-App-ToolBar
        Toolbar bar = ButterKnife.findById(this, toolbarId);
        if (bar != null) {
            setSupportActionBar(bar);
            onToolbarCreated(bar, statusBarMode);
        }
        return bar;
    }

    protected void onToolbarCreated(Toolbar toolbar, @StatusBarMode int statusBarMode) {
        initActionBar();
        initStatusBar(toolbar, statusBarMode);
    }

    protected void initActionBar() {
        // Set the back arrow in the toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        }
    }

    protected void initStatusBar(View toolbar, @StatusBarMode int mode) {
        // Ensure `setStatusBarImmersiveMode()`
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            // Ensure content view `fitsSystemWindows` is false.
            ViewGroup contentParent = (ViewGroup) findViewById(android.R.id.content);
            View content = contentParent.getChildAt(0);

            boolean needHold;
            if (content instanceof DrawerLayout
                    || content instanceof SlidingPaneLayout
                    || content instanceof CoordinatorLayout) {
                // Should set `fitsSystemWindows` true in xml if using these layouts. Besides, also
                // set true for each subviews if `SlidingPaneLayout`.
                needHold = true;
            } else if (MIUIUtils.isMIUI()) {
                // Hold the status bar for all layout in some roms
                needHold = true;
            } else {
                needHold = Build.VERSION.SDK_INT < 21; // 21, 5.0, LOLLIPOP
            }

            if (needHold) {
                // Should set `fitsSystemWindows` false here programmatically.
                setFitsSystemWindows(content, false, true);
                // However, must ensure set `fitsSystemWindows` false for its all subviews.
                // Because in some roms, such as MIUI, if using `NavigationView` in `DrawerLayout`
                // or `SlidingPaneLayout`, it will have padding on its top.

                if (mode == STATUS_BAR_CLIP_TO) {
                    // Add padding to hold the status bar place.
                    clipToStatusBar(toolbar);
                } else { // STATUS_BAR_HOLD_PLACE
                    // Add a view to hold the status bar place.
                    // Note: if using appbar_scrolling_view_behavior of CoordinatorLayout, however,
                    // the holder view could be scrolled to outside as it above the app bar.
                    holdStatusBar(toolbar);
                }
            }

            mStatusBarTinted = !needHold;
        }
    }

    protected void setFitsSystemWindows(View view, boolean fitSystemWindows, boolean applyToChildren) {
        if (view == null) return;
        view.setFitsSystemWindows(fitSystemWindows);
        if (applyToChildren && (view instanceof ViewGroup)) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0, n = viewGroup.getChildCount(); i < n; i++) {
                viewGroup.getChildAt(i).setFitsSystemWindows(fitSystemWindows);
            }
        }
    }

    protected void clipToStatusBar(View toolbar) {
        final int statusBarHeight = UIUtils.getStatusBarHeight(this);
        toolbar.getLayoutParams().height += statusBarHeight;
        toolbar.setPadding(0, statusBarHeight, 0, 0);
    }

    protected void holdStatusBar(View toolbar) {
        holdStatusBar(toolbar, R.id.status_bar, R.color.colorPrimary);
    }

    protected void holdStatusBar(View toolbar, @IdRes int statusBarId, @ColorRes int colorId) {
        ViewGroup toolbarParent = (ViewGroup) toolbar.getParent();
        View holderView = toolbarParent.findViewById(statusBarId);
        if (holderView != null) {
            // for hold status bar in AppBarLayout
            ViewGroup.LayoutParams params = holderView.getLayoutParams();
            params.height = UIUtils.getStatusBarHeight(this);
            holderView.setLayoutParams(params);
        } else {
            int i = 0;
            for (int n = toolbarParent.getChildCount(); i < n; i++) {
                if (toolbarParent.getChildAt(i) == toolbar) break;
            }
            holderView = new View(this);
            holderView.setId(statusBarId);
            toolbarParent.addView(holderView, i, new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.getStatusBarHeight(this)));
            if (toolbarParent instanceof RelativeLayout) {
                ((RelativeLayout.LayoutParams) toolbarParent.getLayoutParams())
                        .addRule(RelativeLayout.BELOW, statusBarId);
            }
        }
        holderView.setBackgroundColor(getResources().getColor(colorId));
    }

    /**
     * Initialize system ui.
     *
     * <p>Reference:
     * <ul>
     * <li><a href="http://developer.android.com/training/system-ui/immersive.html">Using Immersive Full-Screen Mode</a>
     * </ul>
     */
    protected void setStatusBarImmersiveMode(@ColorInt int color) {
        Window win = getWindow();

        // StatusBar
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 21) { // 21, 5.0, LOLLIPOP
            win.getAttributes().systemUiVisibility |=
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.setStatusBarColor(color);
        }

        // Setup immersive mode on third-party rom
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            //FlymeUtils.setStatusBarDarkIcon(win, false);
            MIUIUtils.setStatusBar(win, MIUIUtils.StatusBarMode.TRANSPARENT);
        }

        // Reference:
        // * http://stackoverflow.com/questions/29271251/put-navigation-drawer-under-status-bar
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        //noinspection ResourceAsColor
        setStatusBarImmersiveMode(mStatusBarTinted
                ? ContextCompat.getColor(this, R.color.colorPrimary)
                : Color.TRANSPARENT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        onFinish();
    }

    protected void onFinish() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
