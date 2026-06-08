package com.example.stocktrade.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "StockTrade.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_RECORDS = "trade_records";
    private static final String COL_ID = "_id";
    private static final String COL_TYPE = "type";
    private static final String COL_STOCK_NAME = "stock_name";
    private static final String COL_CONTENT = "content";
    private static final String COL_FILL_TIME = "fill_time";
    private static final String COL_STATUS = "status";
    private static final String COL_CONCLUSION = "conclusion";
    private static final String COL_VOLATILITY_VALUE = "volatility_value";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_RECORDS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TYPE + " TEXT NOT NULL, " +
                COL_STOCK_NAME + " TEXT, " +
                COL_CONTENT + " TEXT NOT NULL, " +
                COL_FILL_TIME + " INTEGER NOT NULL, " +
                COL_STATUS + " TEXT DEFAULT 'pending', " +
                COL_CONCLUSION + " TEXT, " +
                COL_VOLATILITY_VALUE + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }

    public long insertRecord(TradeRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TYPE, record.getType());
        values.put(COL_STOCK_NAME, record.getStockName());
        values.put(COL_CONTENT, record.getContent());
        values.put(COL_FILL_TIME, record.getFillTime().getTime());
        values.put(COL_STATUS, record.getStatus() != null ? record.getStatus() : "pending");
        values.put(COL_CONCLUSION, record.getConclusion());
        values.put(COL_VOLATILITY_VALUE, record.getVolatilityValue());
        long id = db.insert(TABLE_RECORDS, null, values);
        db.close();
        return id;
    }

    public int updateRecord(TradeRecord record) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TYPE, record.getType());
        values.put(COL_STOCK_NAME, record.getStockName());
        values.put(COL_CONTENT, record.getContent());
        values.put(COL_FILL_TIME, record.getFillTime().getTime());
        values.put(COL_STATUS, record.getStatus());
        values.put(COL_CONCLUSION, record.getConclusion());
        values.put(COL_VOLATILITY_VALUE, record.getVolatilityValue());
        int rows = db.update(TABLE_RECORDS, values, COL_ID + " = ?",
                new String[]{String.valueOf(record.getId())});
        db.close();
        return rows;
    }

    public void deleteRecord(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDS, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<TradeRecord> getAllRecords() {
        List<TradeRecord> records = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECORDS, null, null, null, null, null,
                COL_FILL_TIME + " DESC");

        if (cursor.moveToFirst()) {
            do {
                TradeRecord record = new TradeRecord();
                record.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                record.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
                record.setStockName(cursor.getString(cursor.getColumnIndexOrThrow(COL_STOCK_NAME)));
                record.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
                record.setFillTime(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COL_FILL_TIME))));
                record.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
                record.setConclusion(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONCLUSION)));
                record.setVolatilityValue(cursor.getString(cursor.getColumnIndexOrThrow(COL_VOLATILITY_VALUE)));
                records.add(record);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return records;
    }

    public TradeRecord getRecordById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_RECORDS, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        TradeRecord record = null;
        if (cursor.moveToFirst()) {
            record = new TradeRecord();
            record.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
            record.setType(cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
            record.setStockName(cursor.getString(cursor.getColumnIndexOrThrow(COL_STOCK_NAME)));
            record.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
            record.setFillTime(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COL_FILL_TIME))));
            record.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)));
            record.setConclusion(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONCLUSION)));
            record.setVolatilityValue(cursor.getString(cursor.getColumnIndexOrThrow(COL_VOLATILITY_VALUE)));
        }
        cursor.close();
        db.close();
        return record;
    }
}
