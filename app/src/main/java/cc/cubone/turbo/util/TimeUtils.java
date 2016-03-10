package cc.cubone.turbo.util;

public class TimeUtils {

    public static String readableSeconds(long seconds) {
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

}
