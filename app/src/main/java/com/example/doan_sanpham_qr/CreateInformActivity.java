package com.example.doan_sanpham_qr;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.SharedPreferences;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreateInformActivity extends AppCompatActivity {

    private WebSocket webSocket;
    private OkHttpClient client;
    private SharedPreferences sharedPreferences;
    private static final String HISTORY_PREF = "HistoryPreferences";
    private static final String HISTORY_KEY = "HistoryList";
    private boolean keepConnected = true;
    private UserSQLiteHelper userSQLiteHelper;
    private ProjectSQLiteHelper projectSQLiteHelper;
    private TextView thongtinten, thongtinnhom, thongtinchucnang, thongtintientrinh, thongtinnoide, hienthi;
    private Button selectButton;
    private String currentProductName = "";
    private String selectedUserName = "";
    private boolean isConnected = false;
    private Handler pingHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_inform);

        userSQLiteHelper = new UserSQLiteHelper(this);
        projectSQLiteHelper = new ProjectSQLiteHelper(this); // Khởi tạo ProjectSQLiteHelper

        thongtinten = findViewById(R.id.thongtinsanpham);
        thongtinnhom = findViewById(R.id.thongtinnhom);
        thongtinchucnang = findViewById(R.id.thongtinchucnang);
        thongtintientrinh = findViewById(R.id.thongtintientrinh);
        thongtinnoide = findViewById(R.id.thongtinnoide);
        hienthi = findViewById(R.id.hienthi); // TextView để hiển thị dữ liệu từ WebSocket
        selectButton = findViewById(R.id.selectButton);

        connectToWebSocket();

        FloatingActionButton close = findViewById(R.id.close61);
        close.setOnClickListener(view -> {
            Toast.makeText(CreateInformActivity.this, "Đóng giao diện nhưng vẫn giữ kết nối WebSocket", Toast.LENGTH_SHORT).show();
            keepConnected = true; // Đánh dấu để giữ kết nối
            finish();
        });

        sharedPreferences = getSharedPreferences(HISTORY_PREF, Context.MODE_PRIVATE);

        selectButton.setOnClickListener(view -> {
            if (!currentProductName.isEmpty()) {
                showUserSelectionDialog();
            } else {
                Toast.makeText(CreateInformActivity.this, "Không có sản phẩm nào đang hiển thị", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserSelectionDialog() {
        List<User> userList = userSQLiteHelper.layDanhSachNguoiDung();
        List<String> userNames = new ArrayList<>();
        for (User user : userList) {
            userNames.add(user.getHovaten());
        }

        String[] userArray = userNames.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn người dùng");
        builder.setItems(userArray, (dialog, which) -> {
            selectedUserName = userArray[which];
            Toast.makeText(CreateInformActivity.this, selectedUserName + " mượn " + currentProductName, Toast.LENGTH_SHORT).show();
            String currentTime = new SimpleDateFormat("hh:mm:ss a").format(new Date());
            String record = selectedUserName + " mượn " + currentProductName + " lúc " + currentTime;

            saveHistory(record);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Hàm kết nối WebSocket
    private void connectToWebSocket() {
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url("ws://172.20.10.3:8765")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                runOnUiThread(() -> {
                    isConnected = true;
                    Toast.makeText(CreateInformActivity.this, "Đã kết nối WebSocket", Toast.LENGTH_SHORT).show();
                    startPinging();
                });
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                runOnUiThread(() -> {
                    Log.d("WebSocket", "Dữ liệu nhận được: " + text);
                    if (!"ping".equals(text)) {
                        hienthi.setText(text);
                        displayProductInfo(text.trim());
                    }
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                runOnUiThread(() -> {
                    isConnected = false;
                    Toast.makeText(CreateInformActivity.this, "Kết nối WebSocket thất bại", Toast.LENGTH_SHORT).show();
                    Log.e("WebSocket", "Lỗi kết nối WebSocket", t);
                });
                reconnectWebSocket();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                runOnUiThread(() -> {
                    isConnected = false;
                    Toast.makeText(CreateInformActivity.this, "Kết nối WebSocket đã đóng", Toast.LENGTH_SHORT).show();
                });
                stopPinging();
            }
        });
    }

    private void saveHistory(String record) {
        String existingHistory = sharedPreferences.getString(HISTORY_KEY, "");
        String updatedHistory = existingHistory + record + ";";

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HISTORY_KEY, updatedHistory);
        editor.apply();
    }

    private void displayProductInfo(String barcode) {
        Cursor cursor = projectSQLiteHelper.getProductByBarcode(barcode);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex("TenSanPham");
            int groupIndex = cursor.getColumnIndex("NhomTaoRa");
            int functionIndex = cursor.getColumnIndex("ChucNang");
            int progressIndex = cursor.getColumnIndex("TienTrinh");
            int locationIndex = cursor.getColumnIndex("Noide");

            if (nameIndex != -1) {
                String productName = cursor.getString(nameIndex);
                thongtinten.setText(productName);
                currentProductName = productName;
            }
            if (groupIndex != -1) {
                thongtinnhom.setText(cursor.getString(groupIndex));
            }
            if (functionIndex != -1) {
                thongtinchucnang.setText(cursor.getString(functionIndex));
            }
            if (progressIndex != -1) {
                thongtintientrinh.setText(cursor.getString(progressIndex));
            }
            if (locationIndex != -1) {
                thongtinnoide.setText(cursor.getString(locationIndex));
            }

            Toast.makeText(this, "Sản phẩm được tìm thấy", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm với mã vạch: " + barcode, Toast.LENGTH_SHORT).show();
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void startPinging() {
        if (pingHandler == null) {
            pingHandler = new Handler();
        }
        pingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (webSocket != null && isConnected) {
                    webSocket.send("ping");
                    Log.d("WebSocket", "Gửi tín hiệu ping đến server");
                }
                pingHandler.postDelayed(this, 10000);
            }
        }, 10000);
    }

    private void stopPinging() {
        if (pingHandler != null) {
            pingHandler.removeCallbacksAndMessages(null);
        }
    }

    private void reconnectWebSocket() {
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .build();

        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(this::connectToWebSocket, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!keepConnected && webSocket != null) {
            webSocket.close(1000, null);
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
        stopPinging();
    }
}
