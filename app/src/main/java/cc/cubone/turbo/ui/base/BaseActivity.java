package cc.cubone.turbo.ui.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;
import cc.cubone.turbo.R;
import cc.cubone.turbo.core.rom.MIUIUtils;
import cc.cubone.turbo.core.util.UIUtils;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected Toolbar initToolbar() {
        return initToolbar(R.id.bar);
    }

    protected Toolbar initToolbar(@IdRes int id) {
        // Toolbar: http://developer.android.com/reference/android/support/v7/widget/Toolbar.html
        // Adding the App Bar: http://developer.android.com/training/appbar/index.html
        // Using the App ToolBar: https://guides.codepath.com/android/Using-the-App-ToolBar
        Toolbar bar = ButterKnife.findById(this, id);
        if (bar != null) {
            setSupportActionBar(bar);
            onToolbarCreated(bar);
        }
        return bar;
    }

    protected void onToolbarCreated(Toolbar toolbar) {
        // Set the back arrow in the toolbar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(false);
        }
        clipToStatusBar(toolbar);
    }

    protected void clipToStatusBar(Toolbar toolbar) {
        ViewGroup contentParent = ButterKnife.findById(this, android.R.id.content);
        View content = contentParent.getChildAt(0);
        if (content != null) {
            content.setFitsSystemWindows(false);
        }

        final int statusBarHeight = UIUtils.getStatusBarHeight(this);
        toolbar.getLayoutParams().height += statusBarHeight;
        toolbar.setPadding(0, statusBarHeight, 0, 0);
    }

    protected void holdStatusBar(Toolbar toolbar, @ColorRes int resid) {
        ViewGroup toolbarParent = (ViewGroup) toolbar.getParent();
        int i = 0;
        for (int n = toolbarParent.getChildCount(); i < n; i++) {
            if (toolbarParent.getChildAt(i) == toolbar) break;
        }
        View holderView = new View(this);
        holderView.setId(R.id.status_bar);
        holderView.setBackgroundColor(getResources().getColor(resid));
        toolbarParent.addView(holderView, i, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.getStatusBarHeight(this)));
        if (toolbarParent instanceof RelativeLayout) {
            ((RelativeLayout.LayoutParams) toolbarParent.getLayoutParams())
                    .addRule(RelativeLayout.BELOW, R.id.status_bar);
        }
    }

    /**
     * Initialize system ui.
     *
     * <p>Reference:
     * <ul>
     * <li><a href="http://developer.android.com/training/system-ui/immersive.html">Using Immersive Full-Screen Mode</a>
     * </ul>
     */
    protected void initSystemUI(@ColorInt int color) {
        Window win = getWindow();

        // StatusBar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 19, 4.4
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 21, 5.0
            win.getAttributes().systemUiVisibility |=
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.setStatusBarColor(color);
        }

        // Setup immersive mode on third-party rom
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 19, 4.4
            //FlymeUtils.setStatusBarDarkIcon(win, false);
            MIUIUtils.setStatusBar(win, MIUIUtils.StatusBarMode.TRANSPARENT);
        }

        // StatusBar & NavigationBar
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 19, 4.4
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 21, 5.0
            win.getAttributes().systemUiVisibility |=
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.setStatusBarColor(Color.TRANSPARENT);
            win.setNavigationBarColor(Color.TRANSPARENT);
        }*/

        // Reference:
        // * http://stackoverflow.com/questions/29271251/put-navigation-drawer-under-status-bar
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        initSystemUI(Color.TRANSPARENT);
        //initSystemUI(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
