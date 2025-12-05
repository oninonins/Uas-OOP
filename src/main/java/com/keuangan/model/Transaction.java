package com.keuangan.model;

import java.sql.Date;

public class Transaction {
    private int id;
    private int userId;
    private int budgetId;      // TAMBAHAN: ID Sumber Dana
    private String budgetName; // TAMBAHAN: Nama Budget (Gaji, Bonus) untuk ditampilkan
    private double amount;
    private String category;   // Jenis Pengeluaran (Makan, Bensin)
    private String description;
    private Date transactionDate;
    private String type; 

    public Transaction() {}

    // Constructor Baru
    public Transaction(int id, int userId, int budgetId, String budgetName, double amount, String category, String description, Date transactionDate, String type) {
        this.id = id;
        this.userId = userId;
        this.budgetId = budgetId;
        this.budgetName = budgetName;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.transactionDate = transactionDate;
        this.type = type;
    }

    // Getter & Setter Baru
    public int getBudgetId() { return budgetId; }
    public void setBudgetId(int budgetId) { this.budgetId = budgetId; }

    public String getBudgetName() { return budgetName; }
    public void setBudgetName(String budgetName) { this.budgetName = budgetName; }

    // ... (Getter Setter lama biarkan tetap ada) ...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getTransactionDate() { return transactionDate; }
    public void setTransactionDate(Date transactionDate) { this.transactionDate = transactionDate; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}