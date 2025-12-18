package com.example.electricitybill;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "electricitybill.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "bill";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // CHECK IF MONTH EXISTS
    public boolean monthExists(String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT id FROM " + TABLE_NAME + " WHERE month = ?",
                new String[]{month}
        );
        boolean exists = c.moveToFirst();
        c.close();
        return exists;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "month TEXT, " +
                "units INTEGER, " +
                "rebate REAL, " +
                "total REAL, " +
                "final REAL);";

        Log.d("DataHelper", "onCreate: " + sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // INSERT BILL
    public boolean insertBill(String month, int units, double rebate,
                              double total, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("month", month);
        cv.put("units", units);
        cv.put("rebate", rebate);
        cv.put("total", total);
        cv.put("final", finalCost);

        long result = db.insert(TABLE_NAME, null, cv);
        return result != -1; // return true if insert succeeded
    }

    // GET ALL BILLS
    public Cursor getAllBills() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // GET BILL BY ID
    public Cursor getBillById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?",
                new String[]{String.valueOf(id)});
    }

    // UPDATE BILL
    public void updateBill(int id, String month, int units,
                           double rebate, double total, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("month", month);
        cv.put("units", units);
        cv.put("rebate", rebate);
        cv.put("total", total);
        cv.put("final", finalCost);
        db.update(TABLE_NAME, cv, "id=?", new String[]{String.valueOf(id)});
    }

    // DELETE BILL
    public void deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }
}
