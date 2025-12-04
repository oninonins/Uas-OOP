package com.keuangan.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.Transaction;

public class TransactionDAO {

    public boolean addTransaction(Transaction trx) {
        Connection conn = null;
        // QUERY UPDATE: Masukkan budget_id
        String sqlTrx = "INSERT INTO transactions (user_id, budget_id, amount, category, description, transaction_date, type, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        // QUERY UPDATE: Kurangi saldo berdasarkan ID Budget (Sumber Dana)
        String sqlBudget = "UPDATE budgets SET limit_amount = limit_amount - ? WHERE id = ?";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Mulai Transaksi

            // 1. Simpan Transaksi
            try (PreparedStatement ps = conn.prepareStatement(sqlTrx)) {
                ps.setInt(1, trx.getUserId());
                ps.setInt(2, trx.getBudgetId()); // Simpan ID Sumber Dana
                ps.setDouble(3, trx.getAmount());
                ps.setString(4, trx.getCategory()); 
                ps.setString(5, trx.getDescription());
                ps.setDate(6, trx.getTransactionDate());
                ps.setString(7, "PENGELUARAN");
                ps.executeUpdate();
            }

            // 2. Potong Saldo Budget
            try (PreparedStatement ps = conn.prepareStatement(sqlBudget)) {
                ps.setDouble(1, trx.getAmount());
                ps.setInt(2, trx.getBudgetId()); // Potong dari ID ini
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public boolean deleteTransaction(int trxId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Ambil data lama (termasuk budget_id)
            String sqlGet = "SELECT amount, budget_id FROM transactions WHERE id = ?";
            double amount = 0; int budgetId = 0;
            
            try (PreparedStatement ps = conn.prepareStatement(sqlGet)) {
                ps.setInt(1, trxId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    amount = rs.getDouble("amount");
                    budgetId = rs.getInt("budget_id");
                } else return false;
            }

            // 2. Refund Saldo ke Budget Asal
            String sqlRefund = "UPDATE budgets SET limit_amount = limit_amount + ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRefund)) {
                ps.setDouble(1, amount);
                ps.setInt(2, budgetId);
                ps.executeUpdate();
            }

            // 3. Hapus Transaksi
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM transactions WHERE id = ?")) {
                ps.setInt(1, trxId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }

    public List<Transaction> getAllTransactions(int userId) {
        List<Transaction> list = new ArrayList<>();
        // JOIN agar kita bisa lihat nama Budget (Sumber Dana) di tabel
        String sql = "SELECT t.*, b.category as nama_sumber_dana " +
                     "FROM transactions t " +
                     "LEFT JOIN budgets b ON t.budget_id = b.id " +
                     "WHERE t.user_id = ? ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Transaction t = new Transaction(
                    rs.getInt("id"), 
                    rs.getInt("user_id"), 
                    rs.getInt("budget_id"),
                    rs.getString("nama_sumber_dana"), // Gaji, Bonus, dll
                    rs.getDouble("amount"),
                    rs.getString("category"),         // Makan, Bensin, dll
                    rs.getString("description"),
                    rs.getDate("transaction_date"), 
                    rs.getString("type")
                );
                list.add(t);
            }
        } catch(SQLException e){ e.printStackTrace(); }
        return list;
    }
}