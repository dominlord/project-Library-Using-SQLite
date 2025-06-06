package com.example.doan_sanpham_qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        Toast.makeText(HomeActivity.this, "Xin chao den voi trang chu" , Toast.LENGTH_SHORT).show();

        Button sanpham , user , history , taothongtin ;

        sanpham = findViewById(R.id.sanpham);
        user = findViewById(R.id.User);
        history =  findViewById(R.id.lichsu);
        taothongtin = findViewById(R.id.taothongtin);

        sanpham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(HomeActivity.this, ProjectActivity.class);
                startActivity(intent2);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent40 = new Intent(HomeActivity.this, HistoryActivity.class);
                startActivity(intent40);
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent20 = new Intent(HomeActivity.this, UserActivity.class);
                startActivity(intent20);
            }
        });

        taothongtin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent40 = new Intent(HomeActivity.this, CreateInformActivity.class);
                startActivity(intent40);
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}