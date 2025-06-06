package com.example.doan_sanpham_qr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

public class HistoryActivity extends AppCompatActivity {

    private ListView historyListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> historyList;
    private static final String HISTORY_PREF = "HistoryPreferences";
    private static final String HISTORY_KEY = "HistoryList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyListView = findViewById(R.id.historyListView);
        FloatingActionButton close = findViewById(R.id.close50);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(HISTORY_PREF, Context.MODE_PRIVATE);
        String savedHistory = sharedPreferences.getString(HISTORY_KEY, "");

        historyList = new ArrayList<>(Arrays.asList(savedHistory.split(";")));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        historyListView.setAdapter(adapter);

        historyListView.setOnItemClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(HistoryActivity.this)
                    .setTitle("Xóa mục này?")
                    .setMessage("Bạn có chắc chắn muốn xóa mục này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        historyList.remove(position);
                        saveUpdatedHistory();
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void saveUpdatedHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences(HISTORY_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder updatedHistory = new StringBuilder();
        for (String record : historyList) {
            updatedHistory.append(record).append(";");
        }

        editor.putString(HISTORY_KEY, updatedHistory.toString());
        editor.apply();
    }
}
