package com.keuangan.model;

public class MonthlyTotal {
    private final String monthLabel;
    private final double total;

    public MonthlyTotal(String monthLabel, double total) {
        this.monthLabel = monthLabel;
        this.total = total;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public double getTotal() {
        return total;
    }
}

