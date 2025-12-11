package com.keuangan.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.Budget;

public class BudgetDAO {

    // INSERT Budget
    public boolean insert(Budget budget) {
        String sql = "INSERT INTO budgets (user_id, category_id, amount, budget_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, budget.getUserId());
            pstmt.setInt(2, budget.getCategoryId());
            pstmt.setDouble(3, budget.getAmount());
            pstmt.setDate(4, budget.getBudgetDate());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error insert budget: " + e.getMessage());
            return false;
        }
    }

    // SELECT semua budget user (dengan nama kategori)
    public List<Budget> getAll(int userId) {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT b.budget_id, b.user_id, b.category_id, b.amount, b.budget_date, b.created_at, " +
                     "       c.name AS category_name " +
                     "FROM budgets b " +
                     "JOIN categories c ON b.category_id = c.category_id " +
                     "WHERE b.user_id = ? " +
                     "ORDER BY b.budget_date DESC, b.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Budget budget = new Budget(
                        rs.getInt("budget_id"),
                        rs.getInt("user_id"),
                        rs.getInt("category_id"),
                        rs.getDouble("amount"),
                        rs.getDate("budget_date"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getString("category_name")
                );
                list.add(budget);
            }

        } catch (SQLException e) {
            System.out.println("Error getAll budget: " + e.getMessage());
        }

        return list;
    }

    // UPDATE Budget (kategori, jumlah, tanggal)
    public boolean update(Budget budget) {
        String sql = "UPDATE budgets SET category_id = ?, amount = ?, budget_date = ? WHERE budget_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, budget.getCategoryId());
            pstmt.setDouble(2, budget.getAmount());
            pstmt.setDate(3, budget.getBudgetDate());
            pstmt.setInt(4, budget.getBudgetId());
            pstmt.setInt(5, budget.getUserId());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error update budget: " + e.getMessage());
            return false;
        }
    }

    // DELETE Budget
    public boolean delete(int budgetId, int userId) {
        String sql = "DELETE FROM budgets WHERE budget_id = ? AND user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, budgetId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error delete budget: " + e.getMessage());
            return false;
        }
    }
}
