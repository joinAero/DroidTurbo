package cc.cubone.turbo.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Utility class to deal with directory.
 */
public class DirUtils {

    /**
     * Makes a directory named by this path, including missing parent directories if necessary.
     *
     * @param dirPath The directory path.
     * @return true if succeed. Otherwise, failure, already existed or not a directory
     */
    public static boolean mkDir(String dirPath) {
        return mkDir(new File(dirPath));
    }

    /**
     * Makes a directory named by this file, including missing parent directories if necessary.
     *
     * @param file The directory file.
     * @return true if succeed. Otherwise, failure, already existed or not a directory
     */
    public static boolean mkDir(File file) {
        if (file.exists()) {
            return file.isDirectory();
        }
        return file.mkdirs();
    }

    /**
     * Gets the size, in bytes, of the file system path.
     *
     * @param dirPath The file system path.
     */
    @SuppressWarnings("deprecation")
    public static long sizeOfDir(String dirPath) {
        StatFs sf = new StatFs(dirPath);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return sf.getBlockSize() * sf.getAvailableBlocks();
        } else {  // >= 18
            return sf.getBlockSizeLong() * sf.getAvailableBlocksLong();
        }
    }

    /**
     * Returns the human readable size.
     */
    public static String readableSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB", "PB", "EB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    //------------------------------------------------------------------------------

    /**
     * <p>Example path: /data
     */
    public static File getDataDirectory() {
        return Environment.getDataDirectory();
    }

    /**
     * <p>Example path: /cache
     */
    public static File getDownloadCacheDirectory() {
        return Environment.getDownloadCacheDirectory();
    }

    /**
     * <p>Example path: /storage/emulated/0
     */
    public static File getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * <p>Example path: /system
     */
    public static File getRootDirectory() {
        return Environment.getRootDirectory();
    }

    /**
     * <p>Example path: /data/data/cc.cubone.example/app_*
     */
    public static File getDir(Context context, String name, int mode) {
        return context.getDir(name, mode);
    }

    /**
     * <p>Example path: /data/data/cc.cubone.example/cache
     */
    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    /**
     * <p>Example path: /storage/emulated/0/Android/data/cc.cubone.example/cache
     */
    public static File getExternalCacheDir(Context context) {
        return context.getExternalCacheDir();
    }

    /**
     * <p>Example path: /storage/emulated/0/Android/data/cc.cubone.example/files/Movies
     */
    public static File getExternalFilesDir(Context context, String type) {
        return context.getExternalFilesDir(type);
    }

    /**
     * <p>Example path: /data/data/cc.cubone.example/files
     */
    public static File getFilesDir(Context context) {
        return context.getFilesDir();
    }

    /**
     * <p>Example path: /storage/emulated/0/Android/obb/cc.cubone.example
     */
    public static File getObbDir(Context context) {
        return context.getObbDir();
    }

    /**
     * <p>Example path: /storage/emulated/0/.cc.cubone.example
     */
    public static File getExternalPackageDir(Context context) {
        return new File(Environment.getExternalStorageDirectory(), '.' + context.getPackageName());
    }

}
