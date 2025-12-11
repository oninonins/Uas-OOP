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

    // INSERT transaksi: user_id, category_id, amount, description, transaction_date
    public boolean addTransaction(Transaction trx) {
        String sql = "INSERT INTO transactions (user_id, category_id, amount, description, transaction_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trx.getUserId());
            ps.setInt(2, trx.getCategoryId());
            ps.setDouble(3, trx.getAmount());
            ps.setString(4, trx.getDescription());
            ps.setDate(5, trx.getTransactionDate());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error addTransaction: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTransaction(int trxId) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trxId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleteTransaction: " + e.getMessage());
            return false;
        }
    }

    public List<Transaction> getAllTransactions(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.transaction_id, t.user_id, t.category_id, t.amount, t.description, t.transaction_date, " +
                     "       c.name AS category_name " +
                     "FROM transactions t " +
                     "LEFT JOIN categories c ON t.category_id = c.category_id " +
                     "WHERE t.user_id = ? " +
                     "ORDER BY t.transaction_date DESC, t.transaction_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Transaction t = new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getInt("user_id"),
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getDouble("amount"),
                    rs.getString("description"),
                    rs.getDate("transaction_date")
                );
                list.add(t);
            }
        } catch(SQLException e){
            System.out.println("Error getAllTransactions: " + e.getMessage());
        }
        return list;
    }

    // Hitung total budget per category
    public int getTotalBudgetByCategoryId(int categoryId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_budget FROM budgets WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_budget");
            }
        } catch (SQLException e) {
            System.out.println("Error getTotalBudgetByCategoryId: " + e.getMessage());
        }
        return 0;
    }

    // Hitung total pengeluaran per category
    public int getTotalPengeluaranByCategoryId(int categoryId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_trx FROM transactions WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_trx");
            }
        } catch (SQLException e) {
            System.out.println("Error getTotalPengeluaranByCategoryId: " + e.getMessage());
        }
        return 0;
    }
}