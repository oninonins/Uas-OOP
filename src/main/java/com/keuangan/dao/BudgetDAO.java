package com.keuangan.dao;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.Budget;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {
    
    public boolean addBudget(Budget budget) {
        String sql = "INSERT INTO budget (nama_budget, jumlah, bulan) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, budget.getNamaBudget());
            pstmt.setDouble(2, budget.getJumlah());
            pstmt.setString(3, budget.getBulan());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error addBudget: " + e.getMessage());
            return false;
        }
    }
    
    public List<Budget> getAllBudget() {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT id, nama_budget, jumlah, bulan FROM budget ORDER BY id ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Budget budget = new Budget(
                        rs.getInt("id"),
                        rs.getString("nama_budget"),
                        rs.getDouble("jumlah"),
                        rs.getString("bulan")
                );
                list.add(budget);
            }

        } catch (SQLException e) {
            System.out.println("Error getAllBudget: " + e.getMessage());
        }
        
        return list;
    }

    public boolean updateBudget(Budget budget) {
        String sql = "UPDATE budget SET nama_budget=?, jumlah=?, bulan=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, budget.getNamaBudget());
            pstmt.setDouble(2, budget.getJumlah());
            pstmt.setString(3, budget.getBulan());
            pstmt.setInt(4, budget.getId());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error updateBudget: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBudget(int id) {
        String sql = "DELETE FROM budget WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error deleteBudget: " + e.getMessage());
            return false;
        }
    }
}
