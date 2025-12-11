package com.keuangan.model;

public class IncomeExpense {
    private final double income;
    private final double expense;

    public IncomeExpense(double income, double expense) {
        this.income = income;
        this.expense = expense;
    }

    public double getIncome() {
        return income;
    }

    public double getExpense() {
        return expense;
    }
}

