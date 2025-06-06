package com.example.doan_sanpham_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;

public class DangKiActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ki);

        EditText taikhoanmoi, matkhaumoi, cccdmoi, ten;
        Button b3, hienthiMK; //dang ki thanh cong
        SQLiteHelper sqLiteHelper;

        taikhoanmoi = findViewById(R.id.taikhoan);
        matkhaumoi = findViewById(R.id.matkhau);
        cccdmoi = findViewById(R.id.cccd);
        ten = findViewById(R.id.ten);
        b3 = findViewById(R.id.b3);
        hienthiMK = findViewById(R.id.hienthiMK);

        sqLiteHelper = new SQLiteHelper(this);

        hienthiMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible) {
                    matkhaumoi.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isPasswordVisible = false;
                } else {
                    matkhaumoi.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isPasswordVisible = true;
                }
                matkhaumoi.setSelection(matkhaumoi.getText().length());
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = ten.getText().toString().trim();
                String tkmoi = taikhoanmoi.getText().toString().trim();
                String mkmoi = matkhaumoi.getText().toString().trim();

                int cccd = 0;
                try {
                    cccd = Integer.parseInt(cccdmoi.getText().toString().trim());
                } catch (NumberFormatException e) {
                    Toast.makeText(DangKiActivity.this, "CCCD phai la so", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean duochen = sqLiteHelper.dangki(tkmoi, mkmoi, cccd, name);
                if (duochen) {
                    Toast.makeText(DangKiActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(DangKiActivity.this, MainActivity.class);
                    startActivity(intent3);
                } else {
                    Toast.makeText(DangKiActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}