package com.example.doan_sanpham_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserDetailActivity extends AppCompatActivity {

    private EditText etName, etAddress, etPhone;
    private Button btnUpdate, btnDelete , btnClose;
    private UserSQLiteHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        dbHelper = new UserSQLiteHelper(this);

        etName = findViewById(R.id.etName);
        btnClose = findViewById(R.id.btnClose);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent27 = new Intent();
                finish();
            }
        });

        userId = getIntent().getIntExtra("USER_ID", -1);

        loadUserData();

        btnUpdate.setOnClickListener(v -> updateUser());

        btnDelete.setOnClickListener(v -> deleteUser());
    }

    private void loadUserData() {
        User user = dbHelper.getUserById(userId);
        if (user != null) {
            etName.setText(user.getHovaten());
            etAddress.setText(user.getDiachi());
            etPhone.setText(String.valueOf(user.getSdt()));
        }
    }

    private void updateUser() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        int phone;
        try {
            phone = Integer.parseInt(etPhone.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.updateUser(userId, name, address, phone)) {
            Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUser() {
        if (dbHelper.deleteUser(userId)) {
            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}

