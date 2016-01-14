package cc.cubone.turbo.persistence;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PrefAllApps extends Pref {

    @IntDef({LAYOUT_LIST, LAYOUT_GRID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Layout {}

    @IntDef(flag = true, value = {FLAG_DISPLAY_SYSTEM, FLAG_DISPLAY_USER,
            FLAG_DISPLAY_STOPPED, FLAG_DISPLAY_RUNNING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FlagDisplay {}

    public static final int LAYOUT_LIST = 0;
    public static final int LAYOUT_GRID = 1;

    public static final int FLAG_DISPLAY_SYSTEM = 0x00000001;
    public static final int FLAG_DISPLAY_USER = FLAG_DISPLAY_SYSTEM<<1;

    @SuppressLint("ShiftFlags")
    public static final int FLAG_DISPLAY_STOPPED = 0x00000100;
    public static final int FLAG_DISPLAY_RUNNING = FLAG_DISPLAY_STOPPED<<1;

    static final String KEY_LAYOUT = "layout";
    static final String KEY_DISPLAY_FLAGS = "display_flags";

    public PrefAllApps(Context context) {
        super(context, "all_apps");
    }

    @SuppressWarnings("ResourceType")
    @Layout
    public int getLayout() {
        return mSp.getInt(KEY_LAYOUT, LAYOUT_LIST);
    }

    public void setLayout(@Layout int layout) {
        mSp.edit().putInt(KEY_LAYOUT, layout).apply();
    }

    @SuppressWarnings("ResourceType")
    @FlagDisplay
    public int getDisplayFlags() {
        return mSp.getInt(KEY_DISPLAY_FLAGS, FLAG_DISPLAY_USER
                | FLAG_DISPLAY_STOPPED
                | FLAG_DISPLAY_RUNNING);
    }

    public void setDisplayFlags(@FlagDisplay int displayFlags) {
        mSp.edit().putInt(KEY_DISPLAY_FLAGS, displayFlags).apply();
    }
}
