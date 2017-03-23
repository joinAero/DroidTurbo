package cc.eevee.turbo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

public class AssetsUtils {

    public static Bitmap loadBitmap(Context context, String fileName) {
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            return BitmapFactory.decodeStream(is);
        } catch (IOException ignored) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    public static Drawable loadDrawable(Context context, String fileName) {
        InputStream is = null;
        try {
            is = context.getAssets().open(fileName);
            return Drawable.createFromStream(is, null);
        } catch (IOException ignored) {
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }

}
