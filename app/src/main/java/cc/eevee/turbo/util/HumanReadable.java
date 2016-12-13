package cc.eevee.turbo.util;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HumanReadable {

    public static String size(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB", "PB", "EB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) +
                " " + units[digitGroups];
    }

    public static String seconds(long seconds) {
        long m = seconds;
        if (m <= 0) return "0s";

        long n;
        String s = "";

        n = m % 60;
        if (n > 0) s = n + "s"; // second
        m = m / 60;
        if (m <= 0) return s;

        n = (m % 60);
        if (n > 0) s = n + "m" + s; // minute
        m = m / 60;
        if (m <= 0) return s;

        n = (m % 24);
        if (n > 0) s = n + "h" + s; // hour
        m = m / 24;
        if (m <= 0) return s;

        return (m % 24) + "d" + s; // day
    }

    public static String date(long date) {
        return date(new Date(date));
    }

    public static String date(Date date) {
        return date("yyyy-MM-dd HH:mm:ss.SSS", date);
    }

    public static String date(String pattern, long date) {
        return date(pattern, date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String date(String pattern, Date date) {
        return new SimpleDateFormat(pattern).format(date);
    }

}
