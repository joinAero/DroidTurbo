package cc.cubone.turbo.persistence;

import android.content.Context;

public class PrefAllApps extends Pref {

    public static final int LAYOUT_LIST = 0;
    public static final int LAYOUT_GRID = 1;

    public static final int FLAG_DISPLAY_SYSTEM = 0x00000001;
    public static final int FLAG_DISPLAY_USER = FLAG_DISPLAY_SYSTEM<<1;

    public static final int FLAG_DISPLAY_STOPPED = 0x00000100;
    public static final int FLAG_DISPLAY_RUNNING = FLAG_DISPLAY_STOPPED<<1;

    static final String KEY_LAYOUT = "layout";
    static final String KEY_DISPLAY_FLAGS = "display_flags";

    public PrefAllApps(Context context) {
        super(context, "all_apps");
    }

    public int getLayout() {
        return mSp.getInt(KEY_LAYOUT, LAYOUT_LIST);
    }

    public void setLayout(int layout) {
        mSp.edit().putInt(KEY_LAYOUT, layout).apply();
    }

    public int getDisplayFlags() {
        return mSp.getInt(KEY_DISPLAY_FLAGS, FLAG_DISPLAY_USER
                | FLAG_DISPLAY_STOPPED
                | FLAG_DISPLAY_RUNNING);
    }

    public void setDisplayFlags(int displayFlags) {
        mSp.edit().putInt(KEY_DISPLAY_FLAGS, displayFlags).apply();
    }
}
