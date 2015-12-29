package cc.cubone.turbo.util;

import android.content.Context;
import android.content.Intent;

public class ContextUtils {

    public static void startActivity(Context context, Class<?> activity) {
        startActivity(context, activity, Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    public static void startActivity(Context context, Class<?> activity, int flags) {
        if (context == null) return;
        Intent i = new Intent(context, activity);
        i.setFlags(flags);
        context.startActivity(i);
    }

}
