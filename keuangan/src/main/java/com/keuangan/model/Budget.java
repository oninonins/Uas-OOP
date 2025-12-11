package com.keuangan.model;

import java.sql.Date;
import java.time.LocalDateTime;

public class Budget {

    private int budgetId;
    private int userId;
    private int categoryId;
    private double amount;
    private Date budgetDate;
    private LocalDateTime createdAt;
    private String categoryName; // opsional untuk tampilan join

    public Budget(int budgetId, int userId, int categoryId, double amount, Date budgetDate, LocalDateTime createdAt, String categoryName) {
        this.budgetId = budgetId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.budgetDate = budgetDate;
        this.createdAt = createdAt;
        this.categoryName = categoryName;
    }

    public Budget(int userId, int categoryId, double amount, Date budgetDate) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.budgetDate = budgetDate;
    }

    public int getBudgetId() { return budgetId; }
    public int getUserId() { return userId; }
    public int getCategoryId() { return categoryId; }
    public double getAmount() { return amount; }
    public Date getBudgetDate() { return budgetDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getCategoryName() { return categoryName; }

    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setBudgetDate(Date budgetDate) { this.budgetDate = budgetDate; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}
