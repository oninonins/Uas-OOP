package com.keuangan.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.Budget;

public class BudgetDAO {

    // INSERT Budget
    public boolean addBudget(Budget budget) {
    String sql = "INSERT INTO budgets (user_id, category_id, amount, created_at) VALUES (?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, budget.getUserId());
        pstmt.setInt(2, budget.getCategoryId());
        pstmt.setDouble(3, budget.getAmount());
        pstmt.setTimestamp(4, Timestamp.valueOf(budget.getCreatedAt())); // penting!

        pstmt.executeUpdate();
        return true;

    } catch (SQLException e) {
        System.out.println("Error addBudget: " + e.getMessage());
        return false;
    }
}


    // SELECT Semua budget milik user tertentu
    public List<Budget> getAllBudget(int userId) {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT budget_id, category_id, amount, created_at FROM budgets WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Budget budget = new Budget(
                        rs.getInt("budget_id"),
                        userId,
                        rs.getInt("category_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                );
                list.add(budget);
            }

        } catch (SQLException e) {
            System.out.println("Error getAllBudget: " + e.getMessage());
        }

        return list;
    }

    // UPDATE Budget (kategori & jumlah)
    public boolean updateBudget(Budget budget) {
        String sql = "UPDATE budgets SET category_id=?, amount=? WHERE budget_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, budget.getCategoryId());
            pstmt.setDouble(2, budget.getAmount());
            pstmt.setInt(3, budget.getBudgetId());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error updateBudget: " + e.getMessage());
            return false;
        }
    }

    // DELETE Budget
    public boolean deleteBudget(int budgetId) {
        String sql = "DELETE FROM budgets WHERE budget_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, budgetId);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error deleteBudget: " + e.getMessage());
            return false;
        }
    }
}
