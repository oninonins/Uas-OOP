package com.keuangan.dao;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.User;
import java.sql.*;

public class UserDAO {
    
    // Method untuk cek login user
    public User login(String username, String password) {
        User user = null;
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("Error saat login: " + e.getMessage());
        }
        
        return user;
    }
    
    // Method untuk register user baru
    public boolean register(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            int result = ps.executeUpdate();
            return result > 0;
            
        } catch (SQLException e) {
            System.out.println("Error saat register: " + e.getMessage());
            return false;
        }
    }
    
    // Method untuk cek apakah username sudah ada
    public boolean isUsernameExist(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.out.println("Error cek username: " + e.getMessage());
        }
        
        return false;
    }
}

