package cc.eevee.turbo.core.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public abstract class DbHelper implements Db {

    private Context mContext;
    private SQLiteOpenHelper mOpenHelper;

    private int mReferenceCount = 0;

    public DbHelper(Context context) {
        mContext = context;
        open();
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Open the database that will be called in constructing.
     */
    private void open() {
        if (mOpenHelper == null) {
            mOpenHelper = onOpenHelperCreated();
        }
        // upgrade writable database immediately
        //mOpenHelper.getWritableDatabase();
    }

    @NonNull
    protected abstract SQLiteOpenHelper onOpenHelperCreated();

    private SQLiteDatabase getWritableDatabase() {
        return mOpenHelper.getWritableDatabase();
    }

    /**
     * Get database.
     *
     * <pre>
     *   final SQLiteDatabase db = mDb.getDatabase();
     *   try {
     *     ...
     *   } finally {
     *     mDb.closeDatabase();
     *   }
     * </pre>
     */
    @Override
    public SQLiteDatabase getDatabase() {
        acquireReference();
        return getWritableDatabase();
    }

    /**
     * Close database.
     */
    @Override
    public void closeDatabase() {
        releaseReference();
    }

    /**
     * Begins a transaction.
     *
     * <pre>
     *   mDb.beginTransaction();
     *   try {
     *     ...
     *     mDb.setTransactionSuccessful();
     *   } finally {
     *     mDb.endTransaction();
     *   }
     * </pre>
     */
    @Override
    public void beginTransaction() {
        acquireReference();
        getWritableDatabase().beginTransaction();
    }

    /**
     * Marks the current transaction as successful.
     */
    @Override
    public void setTransactionSuccessful() {
        getWritableDatabase().setTransactionSuccessful();
    }

    /**
     * End a transaction.
     */
    @Override
    public void endTransaction() {
        getWritableDatabase().endTransaction();
        releaseReference();
    }

    /**
     * Close any open database object.
     */
    private void close() {
        if (mOpenHelper != null) {
            mOpenHelper.close();
            mOpenHelper = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    public void acquireReference() {
        synchronized(this) {
            mReferenceCount++;
        }
    }

    public void releaseReference() {
        boolean refCountIsZero;
        synchronized(this) {
            if (mReferenceCount <= 0) {
                throw new IllegalStateException(
                        "attempt to release an not acquired object: " + this);
            }
            refCountIsZero = (--mReferenceCount == 0);
        }
        if (refCountIsZero) {
            onAllReferencesReleased();
        }
    }

    private void onAllReferencesReleased() {
        mOpenHelper.close();
    }

}
