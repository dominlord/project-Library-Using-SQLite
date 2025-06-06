package com.example.doan_sanpham_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SearchProjectActivity extends AppCompatActivity {

    private EditText etSearchMaSo;
    private Button btnSearch, timkiembtn;
    private ListView lvSearchResults;
    private ProjectSQLiteHelper dbHelper;
    private FloatingActionButton closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_project);

        dbHelper = new ProjectSQLiteHelper(this);

        etSearchMaSo = findViewById(R.id.et_search_maso);
        btnSearch = findViewById(R.id.timkiem);
        timkiembtn = findViewById(R.id.lichsu); // Button mở lịch sử
        closeBtn = findViewById(R.id.closeBtn);
        lvSearchResults = findViewById(R.id.lv_search_results);

        timkiembtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchProjectActivity.this, LoadSearchHistoryActivity.class);
                startActivity(intent);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maSoText = etSearchMaSo.getText().toString().trim();
                if (!maSoText.isEmpty()) {
                    int maSo = Integer.parseInt(maSoText);
                    searchProject(maSo);
                } else {
                    Toast.makeText(SearchProjectActivity.this, "Vui lòng nhập mã sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchProject(int maSo) {
        String result = dbHelper.searchMaSo(maSo);
        List<String> resultsList = new ArrayList<>();
        resultsList.add(result);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultsList);
        lvSearchResults.setAdapter(adapter);
    }
}
