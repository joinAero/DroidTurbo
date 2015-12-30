package cc.cubone.turbo.persistence;

import android.content.Context;

public class PrefAllApps extends Pref {

    public static final int LAYOUT_LIST = 0;
    public static final int LAYOUT_GRID = 1;

    public static final int DISPLAY_ALL = 0;
    public static final int DISPLAY_USER = 1;

    static final String KEY_LAYOUT = "layout";
    static final String KEY_DISPLAY = "display";

    public PrefAllApps(Context context) {
        super(context, "all_apps");
    }

    public int getLayout() {
        return mSp.getInt(KEY_LAYOUT, LAYOUT_LIST);
    }

    public void setLayout(int layout) {
        mSp.edit().putInt(KEY_LAYOUT, layout).apply();
    }

    public int getDisplay() {
        return mSp.getInt(KEY_DISPLAY, DISPLAY_ALL);
    }

    public void setDisplay(int display) {
        mSp.edit().putInt(KEY_DISPLAY, display).apply();
    }
}
