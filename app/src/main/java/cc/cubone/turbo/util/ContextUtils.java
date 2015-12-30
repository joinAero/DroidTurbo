package cc.cubone.turbo.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

public class ContextUtils {

    public static void startActivity(Context context, Class<?> activity) {
        startActivity(context, activity, Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    public static void startActivity(Context context, Class<?> activity, int flags) {
        Intent i = new Intent(context, activity);
        startActivity(context, i, flags);
    }

    public static void startActivity(Context context, Intent intent) {
        startActivity(context, intent, Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    public static void startActivity(Context context, Intent intent, int flags) {
        if (context == null) return;
        intent.addFlags(flags);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

}
