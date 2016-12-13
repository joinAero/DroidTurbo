package cc.eevee.turbo.core.util;

import android.content.Context;
import android.content.res.TypedArray;

public class UIUtils {

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getActionBarHeight(Context context) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        return a.getDimensionPixelSize(0, 0);
    }

    public static int getActionBarCompatHeight(Context context) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                new int[]{android.support.v7.appcompat.R.attr.actionBarSize});
        return a.getDimensionPixelSize(0, 0);
    }

}
