package com.example.doan_sanpham_qr;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UpdateActivity extends AppCompatActivity {

    EditText title_input, author_input, activity_input, progress_input, place_input;
    Button update_button, delete_button;
    String title, author, activity, progress, id, place;
    ImageButton close2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        title_input = findViewById(R.id.title_input2);
        place_input = findViewById(R.id.place_input2);
        author_input = findViewById(R.id.author_input2);
        activity_input = findViewById(R.id.activity_input2);
        progress_input = findViewById(R.id.progress_input2);

        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }

        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        close2 = findViewById(R.id.close2);

        close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectSQLiteHelper PSQL = new ProjectSQLiteHelper(UpdateActivity.this);
                title = title_input.getText().toString().trim();
                author = author_input.getText().toString().trim();
                place = place_input.getText().toString().trim();
                activity = activity_input.getText().toString().trim();
                progress = progress_input.getText().toString().trim();

                PSQL.updateData(id, title, author, activity, progress, place);

                int maSo = getMaSoFromDatabase(PSQL, id);

                Bitmap barcodeBitmap = generateBarcode(String.format("%04d", maSo));

                if (barcodeBitmap != null) {
                    saveBarcodeToStorage(barcodeBitmap, "MaSo_" + maSo);
                    Toast.makeText(UpdateActivity.this, "Đã cập nhật và lưu mã vạch mới", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpdateActivity.this, "Không thể tạo mã vạch", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private int getMaSoFromDatabase(ProjectSQLiteHelper dbHelper, String id) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT MaSo FROM " + dbHelper.TABLE_NAME + " WHERE id = ?", new String[]{id});
        int maSo = 0;
        if (cursor.moveToFirst()) {
            maSo = cursor.getInt(0);
        }
        cursor.close();
        return maSo;
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("title")
                && getIntent().hasExtra("author") && getIntent().hasExtra("activity")
                && getIntent().hasExtra("progress") && getIntent().hasExtra("place")) {
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            author = getIntent().getStringExtra("author");
            place = getIntent().getStringExtra("place");
            activity = getIntent().getStringExtra("activity");
            progress = getIntent().getStringExtra("progress");

            title_input.setText(title);
            place_input.setText(place);
            author_input.setText(author);
            activity_input.setText(activity);
            progress_input.setText(progress);
        } else {
            Toast.makeText(UpdateActivity.this, "Chưa có dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + "?");
        builder.setMessage("Bạn có muốn xóa không? " + title + "?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ProjectSQLiteHelper PSQL = new ProjectSQLiteHelper(UpdateActivity.this);
                PSQL.deleteOneRow(id);
                finish();
            }
        });
        builder.setNegativeButton("Không", null);
        builder.create().show();
    }

    private Bitmap generateBarcode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveBarcodeToStorage(Bitmap bitmap, String fileName) {
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/BarcodeImages";
        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName + ".png");
        if (file.exists()) {
            file.delete();
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(this, "Lưu thành công vào: " + file.getPath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
