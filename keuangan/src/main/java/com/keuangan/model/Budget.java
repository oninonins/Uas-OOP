package com.keuangan.model;

import java.time.LocalDateTime;

public class Budget {
    private int id;
    private String namaBudget;
    private double jumlah;
    private String bulan;
    private int tahun;
    private LocalDateTime createdAt;

    public Budget() {}

    public Budget(int id, String namaBudget, double jumlah, String bulan) {
        this.id = id;
        this.namaBudget = namaBudget;
        this.jumlah = jumlah;
        this.bulan = bulan;
    }

    public Budget(int id, String namaBudget, double jumlah, String bulan, int tahun, LocalDateTime createdAt) {
        this.id = id;
        this.namaBudget = namaBudget;
        this.jumlah = jumlah;
        this.bulan = bulan;
        this.tahun = tahun;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNamaBudget() { return namaBudget; }
    public void setNamaBudget(String namaBudget) { this.namaBudget = namaBudget; }

    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }

    public String getBulan() { return bulan; }
    public void setBulan(String bulan) { this.bulan = bulan; }

    public int getTahun() { return tahun; }
    public void setTahun(int tahun) { this.tahun = tahun; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
