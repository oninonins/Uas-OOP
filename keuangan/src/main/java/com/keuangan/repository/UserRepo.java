package com.keuangan.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.User;
public class UserRepo {
    
    public User login(String username, String password) {
        User user = null;
        String sql = "SELECT user_id, username, password_hash FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password_hash"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // cek apakah username sudah ada
    public boolean cekUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); 
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Daftar user baru
    public boolean register(User user) {
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            
            int terupdate = pstmt.executeUpdate();
            return terupdate > 0;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
