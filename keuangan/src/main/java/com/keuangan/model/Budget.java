package com.keuangan.model;

import java.time.LocalDateTime;

public class Budget {

    private int budgetId;
    private int userId;
    private int categoryId;
    private double amount;
    private LocalDateTime createdAt;

    public Budget(int budgetId, int userId, int categoryId, double amount, LocalDateTime createdAt) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Budget(int userId, int categoryId, double amount) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
    }

    public int getBudgetId() { return budgetId; }
    public int getUserId() { return userId; }
    public int getCategoryId() { return categoryId; }
    public double getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setAmount(double amount) { this.amount = amount; }
}
