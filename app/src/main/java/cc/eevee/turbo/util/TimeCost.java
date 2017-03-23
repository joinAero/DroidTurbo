package cc.eevee.turbo.util;

import java.util.HashMap;
import java.util.Locale;

import cc.eevee.turbo.MyApplication;
import cc.eevee.turbo.core.util.Log;

public class TimeCost {

    private String mTag;
    private long mTimeBeg = -1;
    private long mTimeEnd = -1;

    public TimeCost(String tag) {
        mTag = tag;
    }

    public long getTimeBeg() {
        return mTimeBeg;
    }

    public long getTimeEnd() {
        return mTimeEnd;
    }

    public long getTimeElapsed() {
        return mTimeEnd - mTimeBeg;
    }

    public TimeCost log() {
        Log.i(MyApplication.LOG_TAG, toString());
        return this;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s\nBEG: %s\nEND: %s\nCOST: %s",
                mTag, HumanReadable.date(mTimeBeg), HumanReadable.date(mTimeEnd),
                elapsed2String(getTimeElapsed()));
    }

    public String toLineString() {
        return String.format(Locale.getDefault(), "%s: %s, %s > %s",
                mTag, elapsed2String(getTimeElapsed()),
                HumanReadable.date("HH:mm:ss.SSS", mTimeBeg),
                HumanReadable.date("HH:mm:ss.SSS", mTimeEnd));
    }

    private static final HashMap<String, TimeCost> mTimeCostMap = new HashMap<>();

    public static TimeCost beg(String tag) {
        TimeCost cost = new TimeCost(tag);
        cost.mTimeBeg = System.currentTimeMillis();
        mTimeCostMap.put(tag, cost);
        return cost;
    }

    public static TimeCost end(String tag) {
        TimeCost cost = mTimeCostMap.remove(tag);
        assert cost != null : "Must beg this tag before end";
        cost.mTimeEnd = System.currentTimeMillis();
        return cost;
    }

    public static String elapsed2String(long elapsed) {
        final long seconds = elapsed / 1000;
        if (seconds > 0) {
            return HumanReadable.seconds(seconds) + " " + (elapsed % 1000) + "ms";
        } else {
            return (elapsed % 1000) + "ms";
        }
    }
}
