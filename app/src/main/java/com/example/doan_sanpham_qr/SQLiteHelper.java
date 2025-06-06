package com.example.doan_sanpham_qr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bang_gia_tri";
    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String bangthongtin = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "cccd INTEGER, " +
                "taikhoan TEXT, " +
                "matkhau TEXT)";
        sqLiteDatabase.execSQL(bangthongtin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS users");
        onCreate(sqLiteDatabase);
    }

    public boolean dangki(String taikhoan, String matkhau, int cccd, String name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("taikhoan", taikhoan);
        values.put("matkhau", matkhau);
        values.put("name", name);
        values.put("cccd", cccd);
        long ketqua = sqLiteDatabase.insert("users", null, values);
        return ketqua != -1;
    }

    public boolean kiemtradangnhap(String taikhoan, String matkhau) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM users WHERE taikhoan = ? AND matkhau = ?", new String[]{taikhoan, matkhau});
        boolean ketqua = cursor.getCount() > 0;
        cursor.close();
        return ketqua;
    }
}
