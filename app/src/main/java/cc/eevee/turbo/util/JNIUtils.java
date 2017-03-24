package cc.eevee.turbo.util;

import android.graphics.Bitmap;

public final class JNIUtils {

    static {
        System.loadLibrary("jni_utils");
    }

    public static native String stringFromJNI();
    public static native boolean deviceQuery();

    public static native void grayscale(Bitmap rgba_8888);
    public static native void grayscale_gpu(Bitmap rgba_8888);
}
