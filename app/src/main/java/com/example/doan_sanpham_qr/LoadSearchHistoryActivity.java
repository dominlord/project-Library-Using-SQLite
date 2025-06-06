package com.example.doan_sanpham_qr;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class LoadSearchHistoryActivity extends AppCompatActivity {

    private ListView lvHistoryLog;
    private ArrayAdapter<String> historyAdapter;
    private List<String> historyList;
    private ProjectSQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_search_history);

        lvHistoryLog = findViewById(R.id.lv_history_log);
        dbHelper = new ProjectSQLiteHelper(this);
        FloatingActionButton closeBTN = findViewById(R.id.closeBTN);
        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        historyList = new ArrayList<>();
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        lvHistoryLog.setAdapter(historyAdapter);



        loadHistoryLog();
    }

    private void loadHistoryLog() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT action, productName, timestamp, details FROM history_table ORDER BY id DESC", null);

        if (cursor != null) {
            historyList.clear();
            while (cursor.moveToNext()) {
                String action = cursor.getString(0);
                String productName = cursor.getString(1);
                String timestamp = cursor.getString(2);
                String details = cursor.getString(3);

                String logEntry = details + " |" + productName + "|" + timestamp;
                historyList.add(logEntry);
            }
            cursor.close();
            historyAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Không có dữ liệu lịch sử", Toast.LENGTH_SHORT).show();
        }
    }

}
