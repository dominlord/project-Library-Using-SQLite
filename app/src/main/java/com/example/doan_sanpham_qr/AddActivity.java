package com.example.doan_sanpham_qr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);

        ImageButton close1;
        Button add_Button;
        EditText title_input, author_input, activity_input, progress_input, place_input;

        title_input = findViewById(R.id.title_input);
        place_input = findViewById(R.id.place_input);
        author_input = findViewById(R.id.author_input);
        activity_input = findViewById(R.id.activity_input);
        progress_input = findViewById(R.id.progress_input);
        add_Button = findViewById(R.id.add_button);
        close1 = findViewById(R.id.close10);

        add_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProjectSQLiteHelper PSQL = new ProjectSQLiteHelper(AddActivity.this);
                String tenSanPham = title_input.getText().toString().trim();
                String nhomTaoRa = author_input.getText().toString().trim();
                String chucNang = activity_input.getText().toString().trim();
                String tienTrinh = place_input.getText().toString().trim();
                String noide = progress_input.getText().toString().trim();

                PSQL.addProject(tenSanPham, nhomTaoRa, chucNang, tienTrinh, noide);


                int maSo = PSQL.getNextMaSo() - 1;

                // Tạo mã QR từ MaSo
                Bitmap barcodeBitmap = generateBarcode(String.format("%04d", maSo));

                if (barcodeBitmap != null) {
                    saveBarcodeToStorage(barcodeBitmap, "MaSo_" + maSo);
                    Toast.makeText(AddActivity.this, "Mã vạch đã được lưu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddActivity.this, "Không thể tạo mã vạch", Toast.LENGTH_SHORT).show();
                }
            }
        });

        close1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent5 = new Intent();
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(this, "Lưu thành công vào: " + file.getPath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
