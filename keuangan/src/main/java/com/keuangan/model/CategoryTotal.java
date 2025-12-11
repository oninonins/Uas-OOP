package com.keuangan.model;

public class CategoryTotal {
    private final int categoryId;
    private final String categoryName;
    private final double total;

    public CategoryTotal(int categoryId, String categoryName, double total) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.total = total;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getTotal() {
        return total;
    }
}

