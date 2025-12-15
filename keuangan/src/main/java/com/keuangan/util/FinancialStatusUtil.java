package com.keuangan.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FinancialStatusUtil {

    public static final String STATUS_BOROS = "Boros";
    public static final String STATUS_NETRAL = "Netral";
    public static final String STATUS_STABIL = "Stabil";

    private FinancialStatusUtil() {
        // utility class
    }

    public static String hitungStatusKeuangan(BigDecimal totalPengeluaran, BigDecimal totalBudget) {
        if (totalPengeluaran == null) {
            totalPengeluaran = BigDecimal.ZERO;
        }
        if (totalBudget == null || totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            return STATUS_NETRAL;
        }

        // Hitung persentase: (pengeluaran / budget) * 100
        BigDecimal persentase = totalPengeluaran
                .multiply(new BigDecimal("100"))
                .divide(totalBudget, 2, RoundingMode.HALF_UP);

        // Kategori berdasarkan persentase
        if (persentase.compareTo(new BigDecimal("80")) > 0) {
            return STATUS_BOROS;  // > 80%
        } else if (persentase.compareTo(new BigDecimal("50")) > 0) {
            return STATUS_NETRAL; // 51% - 80%
        } else {
            return STATUS_STABIL; // 0% - 50%
        }
    }
}

