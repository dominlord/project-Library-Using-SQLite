package com.example.doan_sanpham_qr;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SearchUserActivity extends AppCompatActivity {
    private UserSQLiteHelper dbHelper;
    private EditText edtMaDinhDanh;
    private TextView tvThongTinNguoiDung;
    private ListView lvLichSuThayDoi;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        dbHelper = new UserSQLiteHelper(this);
        edtMaDinhDanh = findViewById(R.id.edtMaDinhDanh);
        tvThongTinNguoiDung = findViewById(R.id.tvThongTinNguoiDung);
        lvLichSuThayDoi = findViewById(R.id.lvLichSuThayDoi);
        Button btnTimKiem = findViewById(R.id.btnTimKiem);

        FloatingActionButton closen = findViewById(R.id.closen);
        closen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnTimKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timKiemNguoiDung();
            }
        });
    }

    private void timKiemNguoiDung() {
        String maDinhDanhStr = edtMaDinhDanh.getText().toString();
        if (maDinhDanhStr.isEmpty()) {
            tvThongTinNguoiDung.setText("Vui lòng nhập Mã Định Danh.");
            return;
        }

        int maDinhDanh = Integer.parseInt(maDinhDanhStr);
        User user = dbHelper.timKiemNguoiDungTheoMaDinhDanh(maDinhDanh);

        if (user != null) {
            userId = user.getId();
            tvThongTinNguoiDung.setText("ID: " + user.getId() + "\nHọ và Tên: " + user.getHovaten() +
                    "\nĐịa chỉ: " + user.getDiachi() + "\nSĐT: " + user.getSdt() +
                    "\nMã Định Danh: " + user.getMaDinhDanh());

            // Tự động tải lịch sử thay đổi cho userId này
            loadChangeHistory();
        } else {
            tvThongTinNguoiDung.setText("Không tìm thấy người dùng với Mã Định Danh này.");
            lvLichSuThayDoi.setVisibility(View.GONE);
        }
    }

    private void loadChangeHistory() {
        if (userId == -1) {
            lvLichSuThayDoi.setVisibility(View.GONE);
            return;
        }

        List<String> historyList = dbHelper.layLichSuThayDoi(userId);

        if (!historyList.isEmpty()) {
            lvLichSuThayDoi.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
            lvLichSuThayDoi.setAdapter(adapter);
        } else {
            lvLichSuThayDoi.setVisibility(View.GONE);
            tvThongTinNguoiDung.setText("Không có lịch sử thay đổi cho người dùng này.");
        }
    }
}
