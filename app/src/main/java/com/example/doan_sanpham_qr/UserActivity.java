package com.example.doan_sanpham_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserActivity extends AppCompatActivity {

    private UserSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        dbHelper = new UserSQLiteHelper(this);

        FloatingActionButton close;
        EditText hovaten, sodienthoai, diachi;
        Button dangkis , danhsachhv;

        hovaten = findViewById(R.id.hovaten);
        sodienthoai = findViewById(R.id.sdt);
        danhsachhv = findViewById(R.id.danhsachhv);
        diachi = findViewById(R.id.diachi);
        dangkis = findViewById(R.id.dangkis);
        close = findViewById(R.id.close63);

        Button timkiemUserbtn = findViewById(R.id.timkiemUser);
        timkiemUserbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent80 = new Intent(UserActivity.this, SearchUserActivity.class);
                startActivity(intent80);
            }
        });

        danhsachhv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent24 = new Intent(UserActivity.this, ShowUserActivity.class);
                startActivity(intent24);
            }
        });

        dangkis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = hovaten.getText().toString().trim();
                String diachimoi = diachi.getText().toString().trim();
                String sdt = sodienthoai.getText().toString().trim();

                if (name.isEmpty() || diachimoi.isEmpty() || sdt.isEmpty()) {
                    Toast.makeText(UserActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean duochen = dbHelper.dangKy(name, diachimoi, Integer.parseInt(sdt));
                if (duochen) {
                    Toast.makeText(UserActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
