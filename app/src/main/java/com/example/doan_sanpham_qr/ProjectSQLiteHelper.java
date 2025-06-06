package com.example.doan_sanpham_qr;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProjectSQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bang_thong_tin_sp.db";
    private static final int DATABASE_VERSION = 6;
    public static final String TABLE_NAME = "project_library";
    private Context context;

    ProjectSQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String bangthongtin = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "TenSanPham TEXT UNIQUE, " +
                "NhomTaoRa TEXT, " +
                "ChucNang TEXT, " +
                "TienTrinh TEXT, " +
                "Noide TEXT, " +
                "MaSo INTEGER UNIQUE, " +
                "MaVach TEXT)";
        sqLiteDatabase.execSQL(bangthongtin);

        String createHistoryTable = "CREATE TABLE IF NOT EXISTS history_table (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "action TEXT, " +
                "productName TEXT, " +
                "timestamp TEXT, " +
                "details TEXT)";
        sqLiteDatabase.execSQL(createHistoryTable);
        
    }

    void logHistory(String action, String productName, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("action", action);
        cv.put("productName", productName);
        cv.put("timestamp", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
        cv.put("details", details);

        db.insert("history_table", null, cv);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN Noide TEXT");
        }
        if (oldVersion < 3) {
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN MaVach TEXT");
        }
        if (oldVersion < 5) {
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN MaSo INTEGER");
        }
        if (oldVersion < 6) {
            String createHistoryTable = "CREATE TABLE IF NOT EXISTS history_table (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "action TEXT, " +
                    "productName TEXT, " +
                    "timestamp TEXT, " +
                    "details TEXT)";
            sqLiteDatabase.execSQL(createHistoryTable);
        }
    }

    int getNextMaSo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(MaSo) FROM " + TABLE_NAME, null);
        int nextMaSo = 1;
        if (cursor.moveToFirst()) {
            nextMaSo = cursor.getInt(0) + 1;
        }
        cursor.close();
        return nextMaSo;
    }


    String generateBarcode(int MaSo) {
        return String.format("%04d", MaSo);
    }

    void addProject(String TenSanPham, String NhomTaoRa, String ChucNang, String TienTrinh, String Noide) {
        if (isProductNameExists(TenSanPham)) {
            Toast.makeText(context, "Tên sản phẩm đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            ContentValues cv = new ContentValues();

            cv.put("TenSanPham", TenSanPham);
            cv.put("NhomTaoRa", NhomTaoRa);
            cv.put("ChucNang", ChucNang);
            cv.put("TienTrinh", TienTrinh);
            cv.put("Noide", Noide);

            int maSo = getNextMaSo();
            cv.put("MaSo", maSo);

            String barcode = generateBarcode(maSo);
            cv.put("MaVach", barcode);

            long ketqua = db.insert(TABLE_NAME, null, cv);
            if (ketqua == -1) {
                Toast.makeText(context, "Thêm thất bại", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Thêm thành công", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    }

    private boolean isProductNameExists(String tenSanPham) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_NAME + " WHERE TenSanPham = ?", new String[]{tenSanPham});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    Cursor readAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null) {
            String query = "SELECT * FROM " + TABLE_NAME;
            return db.rawQuery(query, null);
        }
        return null;
    }

    void updateData(String row_id, String TenSanPham, String NhomTaoRa, String ChucNang, String TienTrinh, String Noide) {
        if (isProductNameExists(TenSanPham)) {
            Log.d("UpdateData", "Updating: " + row_id + ", ChucNang: " + ChucNang + ", TienTrinh: " + TienTrinh + ", Noide: " + Noide);

            Toast.makeText(context, "Tên sản phẩm đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            ContentValues cv = new ContentValues();
            cv.put("TenSanPham", TenSanPham);
            cv.put("NhomTaoRa", NhomTaoRa);
            cv.put("ChucNang", ChucNang);
            cv.put("TienTrinh", TienTrinh);
            cv.put("Noide", Noide);

            Cursor cursor = db.rawQuery("SELECT MaSo, TenSanPham, ChucNang, TienTrinh, Noide FROM " + TABLE_NAME + " WHERE id=?", new String[]{row_id});
            String oldName = "";
            String oldChucNang = "";
            String oldTienTrinh = "";
            String oldNoide = "";

            if (cursor.moveToFirst()) {
                int maSo = cursor.getInt(0);
                oldName = cursor.getString(1);
                oldChucNang = cursor.getString(2);
                oldTienTrinh = cursor.getString(3);
                oldNoide = cursor.getString(4);

                String barcode = generateBarcode(maSo);
                cv.put("MaVach", barcode);
            }
            cursor.close();


            long result = db.update(TABLE_NAME, cv, "id=?", new String[]{row_id});
            if (result == -1) {
                Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            } else {
                StringBuilder details = new StringBuilder();
                details.append("Tên cũ: ").append(oldName);

                if (!oldChucNang.equals(ChucNang)) {
                    details.append(", Chức năng: ").append(oldChucNang).append(" thành ").append(ChucNang);
                }
                if (!oldTienTrinh.equals(TienTrinh)) {
                    details.append(", Tiến trình: ").append(oldTienTrinh).append(" thành ").append(TienTrinh);
                }
                if (!oldNoide.equals(Noide)) {
                    details.append(", Nơi để: ").append(oldNoide).append(" thành ").append(Noide);
                }

                logHistory("Updated", TenSanPham, details.toString() + " | " + TenSanPham);
                Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }
    }


    void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null) {
            Cursor cursor = db.rawQuery("SELECT TenSanPham FROM " + TABLE_NAME + " WHERE id=?", new String[]{row_id});
            if (cursor.moveToFirst()) {
                String productName = cursor.getString(0);
                long result = db.delete(TABLE_NAME, "id=?", new String[]{row_id});
                if (result == -1) {
                    Toast.makeText(context, "Xóa không thành công", Toast.LENGTH_SHORT).show();
                } else {
                    logHistory("Deleted", productName, "Product was deleted");
                    Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    resetIds();
                }
            }
            cursor.close();
            db.close();
        }
    }

    @SuppressLint("Range")
    private void resetIds() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            List<ContentValues> rows = new ArrayList<>();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY id", null);
            while (cursor.moveToNext()) {
                ContentValues cv = new ContentValues();
                int tenSanPhamIndex = cursor.getColumnIndex("TenSanPham");
                int nhomTaoRaIndex = cursor.getColumnIndex("NhomTaoRa");
                int chucNangIndex = cursor.getColumnIndex("ChucNang");
                int tienTrinhIndex = cursor.getColumnIndex("TienTrinh");
                int noideIndex = cursor.getColumnIndex("Noide");
                int maSoIndex = cursor.getColumnIndex("MaSo");
                int maVachIndex = cursor.getColumnIndex("MaVach");

                if (tenSanPhamIndex != -1) cv.put("TenSanPham", cursor.getString(tenSanPhamIndex));
                if (nhomTaoRaIndex != -1) cv.put("NhomTaoRa", cursor.getString(nhomTaoRaIndex));
                if (chucNangIndex != -1) cv.put("ChucNang", cursor.getString(chucNangIndex));
                if (tienTrinhIndex != -1) cv.put("TienTrinh", cursor.getString(tienTrinhIndex));
                if (noideIndex != -1) cv.put("Noide", cursor.getString(noideIndex));
                if (maSoIndex != -1) cv.put("MaSo", cursor.getInt(maSoIndex));
                if (maVachIndex != -1) cv.put("MaVach", cursor.getString(maVachIndex));

                rows.add(cv);
            }
            cursor.close();
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
            for (ContentValues cv : rows) {
                db.insert(TABLE_NAME, null, cv);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Cursor getProductByBarcode(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT TenSanPham, NhomTaoRa, ChucNang, TienTrinh, Noide FROM project_library WHERE MaVach = ?", new String[]{barcode});
        Log.d("DBHelper", "Query result count: " + cursor.getCount());
        return cursor;
    }

    public String searchMaSo(int maSo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = "Sản phẩm không tồn tại";

        Cursor cursor = db.rawQuery("SELECT TenSanPham, TienTrinh FROM " + TABLE_NAME + " WHERE MaSo = ?", new String[]{String.valueOf(maSo)});

        if (cursor.moveToFirst()) {
            int tenSanPhamIndex = cursor.getColumnIndex("TenSanPham");
            int tientrinhIndex = cursor.getColumnIndex("TienTrinh");

            String tenSanPham = cursor.getString(tenSanPhamIndex);
            String tientrinh = cursor.getString(tientrinhIndex);

            Log.d("DatabaseDebug", "TenSanPham: " + tenSanPham);
            Log.d("DatabaseDebug", "TienTrinh: " + tientrinh);

            if (tenSanPham != null && tientrinh != null) {
                result = "Tên sản phẩm: " + tenSanPham + " - Vẫn còn ở: " + tientrinh;
            } else {
                result = "Sản phẩm với mã sản phẩm " + maSo + " không tồn tại";
            }
        } else {
            Log.d("DatabaseDebug", "Không tìm thấy sản phẩm với mã sản phẩm: " + maSo);
        }

        cursor.close();
        db.close();
        return result;
    }

}
