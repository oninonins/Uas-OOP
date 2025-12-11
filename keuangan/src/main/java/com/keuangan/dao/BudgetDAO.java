package com.keuangan.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.Budget;
import com.keuangan.model.CategoryTotal;

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

    // Statistik: total budget per kategori dalam rentang tanggal
    public List<CategoryTotal> getBudgetPerCategory(int userId, Date start, Date end) {
        List<CategoryTotal> result = new ArrayList<>();
        String sql = "SELECT c.category_id, c.name AS category_name, COALESCE(SUM(b.amount), 0) AS total_amount "
                   + "FROM budgets b "
                   + "JOIN categories c ON b.category_id = c.category_id "
                   + "WHERE b.user_id = ? AND b.budget_date BETWEEN ? AND ? "
                   + "GROUP BY c.category_id, c.name "
                   + "ORDER BY total_amount DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, start);
            ps.setDate(3, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new CategoryTotal(
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getDouble("total_amount")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getBudgetPerCategory: " + e.getMessage());
        }
        return result;
    }

    // Statistik: map budget per kategori (untuk perbandingan realisasi)
    public Map<Integer, Double> getBudgetByCategoryMap(int userId, Date start, Date end) {
        Map<Integer, Double> result = new LinkedHashMap<>();
        String sql = "SELECT category_id, COALESCE(SUM(amount), 0) AS total_amount "
                   + "FROM budgets "
                   + "WHERE user_id = ? AND budget_date BETWEEN ? AND ? "
                   + "GROUP BY category_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, start);
            ps.setDate(3, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getInt("category_id"), rs.getDouble("total_amount"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getBudgetByCategoryMap: " + e.getMessage());
        }
        return result;
    }
}
