package cc.eevee.turbo.core.persistence;

import android.database.sqlite.SQLiteDatabase;

public interface Db {

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
    public SQLiteDatabase getDatabase();

    /**
     * Close database.
     */
    public void closeDatabase();

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
    public void beginTransaction();

    /**
     * Marks the current transaction as successful.
     */
    public void setTransactionSuccessful();

    /**
     * End a transaction.
     */
    public void endTransaction();

}
