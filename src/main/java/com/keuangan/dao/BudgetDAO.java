package com.keuangan.dao;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.Budget;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.Year; 
public class BudgetDAO {
    
    public boolean addBudget(Budget budget) {
        String sql = "INSERT INTO budgets (user_id, category, limit_amount, month, year, created_at) VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, 1); 

            pstmt.setString(2, budget.getNamaBudget());

            pstmt.setDouble(3, budget.getJumlah());
            
            try {
                pstmt.setInt(4, Integer.parseInt(budget.getBulan()));
            } catch (NumberFormatException e) {
                pstmt.setInt(4, 1); 
                System.out.println("Warning: Format bulan salah, set ke 1");
            }

            pstmt.setInt(5, Year.now().getValue());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error addBudget: " + e.getMessage());
            return false;
        }
    }
    
    public List<Budget> getAllBudget() {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT id, category, limit_amount, month FROM budgets ORDER BY id ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                
                Budget budget = new Budget(
                        rs.getInt("id"),
                        rs.getString("category"),      
                        rs.getDouble("limit_amount"),  
                        String.valueOf(rs.getInt("month")) 
                );
                list.add(budget);
            }

        } catch (SQLException e) {
            System.out.println("Error getAllBudget: " + e.getMessage());
        }
        
        return list;
    }

    public boolean updateBudget(Budget budget) {
        String sql = "UPDATE budgets SET category=?, limit_amount=?, month=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, budget.getNamaBudget());
            pstmt.setDouble(2, budget.getJumlah());
            
            try {
                pstmt.setInt(3, Integer.parseInt(budget.getBulan()));
            } catch (NumberFormatException e) {
                pstmt.setInt(3, 1);
            }
            
            pstmt.setInt(4, budget.getId());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error updateBudget: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteBudget(int id) {
        String sql = "DELETE FROM budgets WHERE id=?";
        
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