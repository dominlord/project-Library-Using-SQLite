package com.example.doan_sanpham_qr;

public class User {
    private int id;
    private String hovaten;
    private String diachi;
    private int sdt;
    private int maDinhDanh;

    public User(int id, String hovaten, String diachi, int sdt, int maDinhDanh) {
        this.id = id;
        this.hovaten = hovaten;
        this.diachi = diachi;
        this.sdt = sdt;
        this.maDinhDanh = maDinhDanh;
    }



    // Getters và Setters
    public int getId() { return id; }
    public String getHovaten() { return hovaten; }
    public String getDiachi() { return diachi; }
    public int getSdt() { return sdt; }
    public int getMaDinhDanh() { return maDinhDanh; }

    public void setId(int id) { this.id = id; }
    public void setHovaten(String hovaten) { this.hovaten = hovaten; }
    public void setDiachi(String diachi) { this.diachi = diachi; }
    public void setSdt(int sdt) { this.sdt = sdt; }
    public void setMaDinhDanh(int maDinhDanh) { this.maDinhDanh = maDinhDanh; }
}
