package com.example.doan_sanpham_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ShowUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private UserSQLiteHelper dbHelper;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        dbHelper = new UserSQLiteHelper(this);
        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = dbHelper.layDanhSachNguoiDung();
        adapter = new UserAdapter(userList, user -> {
            Intent intent = new Intent(ShowUserActivity.this, UserDetailActivity.class);
            intent.putExtra("USER_ID", user.getId());
            startActivityForResult(intent, 1);
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton close6 = findViewById(R.id.close6);
        close6.setOnClickListener(view -> finish());

        FloatingActionButton btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(v -> {
            dbHelper.resetUserIds();
            loadData();
            adapter.notifyDataSetChanged();
        });
    }

    // Hàm tải dữ liệu từ cơ sở dữ liệu
    private void loadData() {
        userList.clear();
        userList.addAll(dbHelper.layDanhSachNguoiDung());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadData();
            adapter.notifyDataSetChanged();
        }
    }
}

