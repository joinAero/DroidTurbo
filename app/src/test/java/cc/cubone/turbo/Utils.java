package cc.cubone.turbo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Utils {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static <T> void log(String tag, T msg) {
        print(DATE_FORMAT.format(new Date()));
        print("  ");
        if (isEmpty(tag)) {
            println(msg);
        } else {
            println(tag + ": " + msg);
        }
    }

    public static <T> void print(T t) {
        System.out.print(t);
    }

    public static void println() {
        System.out.println();
    }

    public static <T> void println(T t) {
        System.out.println(t);
    }
}
