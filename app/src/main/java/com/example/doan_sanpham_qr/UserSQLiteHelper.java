package com.example.doan_sanpham_qr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UserSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bang_gia_tri_user";
    private static final int DATABASE_VERSION = 1;

    public UserSQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String bangnguoidung = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY, " +
                "hovaten TEXT, " +
                "diachi TEXT, " +
                "sdt INTEGER, " +
                "MaDinhDanh INTEGER)";
        sqLiteDatabase.execSQL(bangnguoidung);

        String bangLichSu = "CREATE TABLE user_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "field_changed TEXT, " +
                "old_value TEXT, " +
                "new_value TEXT, " +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
        sqLiteDatabase.execSQL(bangLichSu);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            sqLiteDatabase.execSQL("ALTER TABLE users ADD COLUMN MaDinhDanh INTEGER");
        }
    }

    public boolean dangKy(String hovaten, String diachi, int sdt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", getNextAvailableId());
        values.put("MaDinhDanh", getNextAvailableMaDinhDanh());
        values.put("hovaten", hovaten);
        values.put("diachi", diachi);
        values.put("sdt", sdt);
        long result = db.insert("users", null, values);
        return result != -1;
    }

    public int getNextAvailableMaDinhDanh() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(MaDinhDanh) + 1 FROM users", null);
        int nextMaDinhDanh = cursor.moveToFirst() ? cursor.getInt(0) : 1;
        cursor.close();
        return nextMaDinhDanh;
    }

    public boolean kiemtraTonTaiHoVaTen(String hovaten) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE hovaten = ?", new String[]{hovaten});
        boolean ketqua = cursor.getCount() > 0;
        cursor.close();
        return ketqua;
    }

    public List<User> layDanhSachNguoiDung() {
        List<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String hovaten = cursor.getString(cursor.getColumnIndex("hovaten"));
                String diachi = cursor.getString(cursor.getColumnIndex("diachi"));
                int sdt = cursor.getInt(cursor.getColumnIndex("sdt"));
                int maDinhDanh = cursor.getInt(cursor.getColumnIndex("MaDinhDanh"));

                userList.add(new User(id, hovaten, diachi, sdt, maDinhDanh));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userList;
    }

    public User getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String hovaten = cursor.getString(cursor.getColumnIndex("hovaten"));
            String diachi = cursor.getString(cursor.getColumnIndex("diachi"));
            int sdt = cursor.getInt(cursor.getColumnIndex("sdt"));
            int maDinhDanh = cursor.getInt(cursor.getColumnIndex("MaDinhDanh"));

            cursor.close();
            return new User(id, hovaten, diachi, sdt, maDinhDanh);
        }
        cursor.close();
        return null;
    }

    public boolean updateUser(int id, String name, String address, int phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String oldName = cursor.getString(cursor.getColumnIndex("hovaten"));
            String oldAddress = cursor.getString(cursor.getColumnIndex("diachi"));
            int oldPhone = cursor.getInt(cursor.getColumnIndex("sdt"));

            if (!oldName.equals(name)) {
                logChange(id, "Tên", oldName, name);
            }
            if (!oldAddress.equals(address)) {
                logChange(id, "Địa chỉ", oldAddress, address);
            }
            if (oldPhone != phone) {
                logChange(id, "Số điện thoại", String.valueOf(oldPhone), String.valueOf(phone));
            }
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("hovaten", name);
        values.put("diachi", address);
        values.put("sdt", phone);
        return db.update("users", values, "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public boolean deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            // Lưu lịch sử trước khi xóa
            String name = cursor.getString(cursor.getColumnIndex("hovaten"));
            String address = cursor.getString(cursor.getColumnIndex("diachi"));
            int phone = cursor.getInt(cursor.getColumnIndex("sdt"));
            logChange(id, "delete", "Name: " + name + ", Address: " + address + ", Phone: " + phone, "Deleted");
        }
        cursor.close();
        return db.delete("users", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    private void logChange(int userId, String fieldChanged, String oldValue, String newValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("field_changed", fieldChanged);
        values.put("old_value", oldValue);
        values.put("new_value", newValue);
        db.insert("user_history", null, values);
    }

    public void resetUserIds() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<User> users = layDanhSachNguoiDung();

        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM users");

            ContentValues values = new ContentValues();
            int newId = 1;
            for (User user : users) {
                values.put("id", newId++);
                values.put("hovaten", user.getHovaten());
                values.put("diachi", user.getDiachi());
                values.put("sdt", user.getSdt());
                values.put("MaDinhDanh", user.getMaDinhDanh());  // Giữ nguyên MaDinhDanh
                db.insert("users", null, values);
                values.clear();
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public int getNextAvailableId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id + 1 FROM users AS t1 WHERE NOT EXISTS (SELECT 1 FROM users AS t2 WHERE t2.id = t1.id + 1) ORDER BY id LIMIT 1", null);
        int nextId = cursor.moveToFirst() ? cursor.getInt(0) : 1;
        cursor.close();
        return nextId;
    }

    public User timKiemNguoiDungTheoMaDinhDanh(int maDinhDanh) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE MaDinhDanh = ?", new String[]{String.valueOf(maDinhDanh)});
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String hovaten = cursor.getString(cursor.getColumnIndex("hovaten"));
            String diachi = cursor.getString(cursor.getColumnIndex("diachi"));
            int sdt = cursor.getInt(cursor.getColumnIndex("sdt"));
            cursor.close();
            return new User(id, hovaten, diachi, sdt, maDinhDanh);
        }
        cursor.close();
        return null;
    }

    public List<String> layLichSuThayDoi(int userId) {
        List<String> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM user_history WHERE user_id = ? ORDER BY timestamp DESC", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                String field = cursor.getString(cursor.getColumnIndex("field_changed"));
                String oldValue = cursor.getString(cursor.getColumnIndex("old_value"));
                String newValue = cursor.getString(cursor.getColumnIndex("new_value"));
                String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));

                String log = "Thay đổi " + field + " từ '" + oldValue + "' thành '" + newValue + "' vào " + timestamp;
                historyList.add(log);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return historyList;
    }

}
