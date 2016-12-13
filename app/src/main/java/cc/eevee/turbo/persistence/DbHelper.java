package cc.eevee.turbo.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import cc.eevee.turbo.BuildConfig;
import cc.eevee.turbo.core.util.Log;

public class DbHelper extends cc.eevee.turbo.core.persistence.DbHelper {

    static final String TAG = DbHelper.class.getSimpleName();

    public static final String DB_NAME = "turbo.db";
    public static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context);
    }

    static void createAllTables(SQLiteDatabase db) {
        // create tables here
    }

    static void dropAllTables(SQLiteDatabase db) {
        // drop tables here
    }

    @NonNull
    @Override
    protected SQLiteOpenHelper onOpenHelperCreated() {
        if (BuildConfig.DEBUG) {
            return new DevOpenHelper(getContext());
        } else {
            return new UpgradeOpenHelper(getContext());
        }
    }

    /**
     * The basic open helper.
     */
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createAllTables(db);
        }
    }

    /**
     * The open helper for developing.
     *
     * <p/>WARNING: Drops all table on Upgrade! Use only during development.
     */
    public static class DevOpenHelper extends OpenHelper {

        public DevOpenHelper(Context context) {
            super(context);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Upgrading version from " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db);
            onCreate(db);
        }
    }

    /**
     * The open helper for upgrading.
     */
    public static class UpgradeOpenHelper extends OpenHelper {

        public UpgradeOpenHelper(Context context) {
            super(context);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i(TAG, "Upgrading version from " + oldVersion + " to " + newVersion);
        }
    }

}
