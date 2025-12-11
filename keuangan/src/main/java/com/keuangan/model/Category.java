package com.keuangan.model;

public class Category {

    private int categoryId;
    private String name;
    private String icon;

    public Category(int categoryId, String name, String icon) {
        this.categoryId = categoryId;
        this.name = name;
        this.icon = icon;
    }

    public int getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getIcon() { return icon; }

    public void setName(String name) { this.name = name; }
    public void setIcon(String icon) { this.icon = icon; }
}
