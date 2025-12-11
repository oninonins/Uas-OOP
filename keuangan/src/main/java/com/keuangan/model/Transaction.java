package com.keuangan.model;

import java.sql.Date;

public class Transaction {
    private int id;               // transaction_id
    private int userId;
    private int categoryId;
    private String categoryName;    // untuk tampilan join kategori
    private double amount;
    private String description;
    private Date transactionDate;

    public Transaction() {}

    public Transaction(int id, int userId, int categoryId, String categoryName,
                       double amount, String description, Date transactionDate) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }
}