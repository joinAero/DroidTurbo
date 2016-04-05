package cc.cubone.turbo.core.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class Table {

    private Db mDb;
    private String mName;

    public Table(Db db, String name) {
        mDb = db;
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public boolean insert(ContentValues values) {
        final SQLiteDatabase db = mDb.getDatabase();
        try {
            final long id = db.insert(mName, null, values);
            return id != -1;
        } finally {
            mDb.closeDatabase();
        }
    }

    public boolean replace(ContentValues values) {
        final SQLiteDatabase db = mDb.getDatabase();
        try {
            // INSERT OR REPLACE
            final long id = db.replace(mName, null, values);
            return id != -1;
        } finally {
            mDb.closeDatabase();
        }
    }

    public int update(ContentValues values, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mDb.getDatabase();
        try {
            return db.update(mName, values, whereClause, whereArgs);
        } finally {
            mDb.closeDatabase();
        }
    }

    public int delete(String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mDb.getDatabase();
        try {
            return db.delete(mName, whereClause, whereArgs);
        } finally {
            mDb.closeDatabase();
        }
    }

    public int deleteAll() {
        return delete(null, null);
    }

    public <T> T queryFirst(String sql, Cursor2Object<T> cur2Obj) {
        final SQLiteDatabase db = mDb.getDatabase();
        T object = null;
        try {
            final Cursor cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                object = cur2Obj.cursor2Object(cursor);
            }
            cursor.close();
        } finally {
            mDb.closeDatabase();
        }
        return object;
    }

    public <T> List<T> queryList(String sql, Cursor2Object<T> cur2Obj) {
        final SQLiteDatabase db = mDb.getDatabase();
        final List<T> objects = new ArrayList<T>();
        try {
            final Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                objects.add(cur2Obj.cursor2Object(cursor));
            }
            cursor.close();
        } finally {
            mDb.closeDatabase();
        }
        return objects;
    }

    public interface Cursor2Object<T> {
        T cursor2Object(Cursor cursor);
    }

}
