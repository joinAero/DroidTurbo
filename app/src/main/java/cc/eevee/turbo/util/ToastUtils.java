package cc.eevee.turbo.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.Toast;

public class ToastUtils {

    public static void show(Context context, @StringRes int resId) {
        if (context == null) return;
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void show(Context context, CharSequence text) {
        if (context == null) return;
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, @StringRes int resId) {
        if (context == null) return;
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, CharSequence text) {
        if (context == null) return;
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

}
