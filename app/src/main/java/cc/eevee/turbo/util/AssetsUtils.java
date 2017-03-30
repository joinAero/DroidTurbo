package cc.eevee.turbo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayOutputStream;
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

    public static Mat loadMat(Context context, String fileName, int flags) {
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            is = context.getAssets().open(fileName);
            os = new ByteArrayOutputStream(is.available());

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            is = null;

            Mat encoded = new Mat(1, os.size(), CvType.CV_8U);
            encoded.put(0, 0, os.toByteArray());
            os.close();
            os = null;

            Mat decoded = Imgcodecs.imdecode(encoded, flags);
            encoded.release();

            return decoded;
        } catch (IOException ignored) {
        } finally {
            try {
                if (is != null) is.close();
                if (os != null) os.close();
            } catch (IOException ignored) {
            }
        }
        return null;
    }

}
