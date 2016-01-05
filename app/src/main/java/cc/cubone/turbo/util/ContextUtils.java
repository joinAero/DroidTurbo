package cc.cubone.turbo.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

public class ContextUtils {

    public static boolean startActivity(Context context, Class<?> activity) {
        return startActivity(context, activity, Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    public static boolean startActivity(Context context, Class<?> activity, int flags) {
        return startActivity(context, new Intent(context, activity), flags);
    }

    public static boolean startActivity(Context context, Intent intent) {
        return startActivity(context, intent, Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }

    public static boolean startActivity(Context context, Intent intent, int flags) {
        if (context == null) {
            return false;
        }
        intent.addFlags(flags);
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

}
