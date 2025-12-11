package com.keuangan.util;

import java.math.BigDecimal;

public class FinancialStatusUtil {

    public static final String STATUS_BOROS = "Boros";
    public static final String STATUS_NETRAL = "Netral";
    public static final String STATUS_STABIL = "Stabil";

    public static final BigDecimal BOROS_MULTIPLIER = new BigDecimal("1.20");
    public static final BigDecimal STABIL_MULTIPLIER = new BigDecimal("0.80");

    private FinancialStatusUtil() {
        // utility
    }

    public static String hitungStatusKeuangan(BigDecimal totalPeriode, BigDecimal rataRataHistoris) {
        if (totalPeriode == null) {
            return STATUS_NETRAL;
        }

        if (rataRataHistoris == null || BigDecimal.ZERO.compareTo(rataRataHistoris) == 0) {
            return STATUS_NETRAL;
        }

        BigDecimal batasBoros = rataRataHistoris.multiply(BOROS_MULTIPLIER);
        BigDecimal batasStabil = rataRataHistoris.multiply(STABIL_MULTIPLIER);

        if (totalPeriode.compareTo(batasBoros) > 0) {
            return STATUS_BOROS;
        } else if (totalPeriode.compareTo(batasStabil) < 0) {
            return STATUS_STABIL;
        } else {
            return STATUS_NETRAL;
        }
    }
}

