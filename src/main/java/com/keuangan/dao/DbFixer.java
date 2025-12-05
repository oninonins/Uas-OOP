package com.keuangan.dao;

import java.sql.Connection;
import java.sql.Statement;

import com.keuangan.config.DatabaseConnection;

public class DbFixer {
    public static void main(String[] args) {
        System.out.println("Memulai perbaikan database...");
        
        try (Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement()) {
            
            String sql1 = "ALTER TABLE transactions ADD COLUMN IF NOT EXISTS budget_id INT";
            stmt.executeUpdate(sql1);
            System.out.println("Sukses: Kolom budget_id berhasil ditambahkan.");

            String sql2 = "ALTER TABLE transactions ADD CONSTRAINT fk_budget_trx FOREIGN KEY (budget_id) REFERENCES budgets(id) ON DELETE SET NULL";
            stmt.executeUpdate(sql2);
            System.out.println("Sukses: Relasi Foreign Key berhasil dibuat.");
            
        } catch (Exception e) {
            System.out.println("Info/Error: " + e.getMessage());
            System.out.println("Jika errornya 'column already exists', berarti database sudah benar. Abaikan saja.");
        }
    }
}