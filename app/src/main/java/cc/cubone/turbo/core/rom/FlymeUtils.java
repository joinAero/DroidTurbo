package cc.cubone.turbo.core.rom;

import android.os.Build;

import java.lang.reflect.Method;

public class FlymeUtils {

    public static boolean isFlyme() {
        try {
            // Invoke Build.hasSmartBar()
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (Exception e) {
            return false;
        }
    }
}
