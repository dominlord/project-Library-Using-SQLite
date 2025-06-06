package com.example.doan_sanpham_qr;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProjectActivity extends AppCompatActivity {

    FloatingActionButton close;
    RecyclerView recyclerView;
    FloatingActionButton addButton , refreshButton;
    ProjectSQLiteHelper PSQL;
    ArrayList<String> project_id, project_title, project_author, project_activity, project_progress, project_place;
    CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project);

        recyclerView = findViewById(R.id.recyclerView);
        addButton = findViewById(R.id.addButton);
        FloatingActionButton searchBtn = findViewById(R.id.searchBtn);
        refreshButton = findViewById(R.id.refreshButton);
        close = findViewById(R.id.close60);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent70 = new Intent(ProjectActivity.this, SearchProjectActivity.class);
                startActivity(intent70);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                project_id.clear();
                project_author.clear();
                project_title.clear();
                project_place.clear();
                project_activity.clear();
                project_progress.clear();
                storeDataInArray();
                customAdapter.notifyDataSetChanged();
                Toast.makeText(ProjectActivity.this,"Refresh thanh cong", Toast.LENGTH_SHORT).show();

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent4 = new Intent(ProjectActivity.this, AddActivity.class);
                startActivity(intent4);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent();
                finish();
            }
        });

        // Set insets cho layout chính
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo SQLiteHelper và ArrayList
        PSQL = new ProjectSQLiteHelper(ProjectActivity.this);
        project_id = new ArrayList<>();
        project_author = new ArrayList<>();
        project_place = new ArrayList<>();
        project_title = new ArrayList<>();
        project_activity = new ArrayList<>();
        project_progress = new ArrayList<>();
        storeDataInArray();

        customAdapter = new CustomAdapter(ProjectActivity.this, this, project_id, project_title, project_author, project_activity, project_progress, project_place);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ProjectActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            recreate();
        }
    }

    void storeDataInArray() {
        Cursor cursor = PSQL.readAllData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Không có dữ liệu", Toast.LENGTH_LONG).show();
        } else {
            while (cursor.moveToNext()) {
                project_id.add(cursor.getString(0));
                project_author.add(cursor.getString(2));
                project_title.add(cursor.getString(1));
                project_activity.add(cursor.getString(3));
                project_progress.add(cursor.getString(4));
                project_place.add(cursor.getString(5));
            }
        }
    }
}
