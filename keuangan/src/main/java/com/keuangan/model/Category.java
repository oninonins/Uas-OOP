package com.keuangan.model;

public class Category {

    private int categoryId;
    private int userId;
    private String name;
    private String icon;

    // Digunakan saat membaca data lengkap dari DB
    public Category(int categoryId, int userId, String name, String icon) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.name = name;
        this.icon = icon;
    }

    // Untuk insert baru (tanpa icon)
    public Category(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    // Untuk insert baru dengan icon opsional
    public Category(int userId, String name, String icon) {
        this.userId = userId;
        this.name = name;
        this.icon = icon;
    }

    public int getCategoryId() { return categoryId; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getIcon() { return icon; }

    public void setName(String name) { this.name = name; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setUserId(int userId) { this.userId = userId; }
}
