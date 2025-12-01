package com.keuangan.model;

public class Budget {
    private int id;
    private String namaBudget;
    private double jumlah;
    private String bulan;

    public Budget() {}

    public Budget(int id, String namaBudget, double jumlah, String bulan) {
        this.id = id;
        this.namaBudget = namaBudget;
        this.jumlah = jumlah;
        this.bulan = bulan;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNamaBudget() { return namaBudget; }
    public void setNamaBudget(String namaBudget) { this.namaBudget = namaBudget; }

    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }

    public String getBulan() { return bulan; }
    public void setBulan(String bulan) { this.bulan = bulan; }
}
