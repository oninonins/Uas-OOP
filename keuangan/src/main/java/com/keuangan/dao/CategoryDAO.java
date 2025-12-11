package com.keuangan.dao;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, user_id, name, icon FROM categories ORDER BY name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("category_id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("icon")
                ));
            }

        } catch (Exception e) {
            System.out.println("Error load categories: " + e.getMessage());
        }

        return list;
    }

    // Alias sesuai permintaan prompt
    public List<Category> getAll() {
        return getAllCategories();
    }

    public boolean delete(int categoryId, int userId) {
        String sql = "DELETE FROM categories WHERE category_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error delete category: " + e.getMessage());
            return false;
        }
    }

    // Ambil kategori khusus user
    public List<Category> getAllByUser(int userId) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, user_id, name, icon FROM categories WHERE user_id = ? ORDER BY name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Category(
                            rs.getInt("category_id"),
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("icon")
                    ));
                }
            }

        } catch (Exception e) {
            System.out.println("Error load categories by user: " + e.getMessage());
        }

        return list;
    }

    // Insert kategori baru (hanya nama, icon null)
    public boolean insert(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            return false;
        }

        String checkSql = "SELECT 1 FROM categories WHERE user_id = ? AND LOWER(name) = LOWER(?)";
        String insertSql = "INSERT INTO categories (user_id, name) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Cek duplikasi per user
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, category.getUserId());
                checkStmt.setString(2, category.getName());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Kategori sudah ada untuk user ini.");
                        return false;
                    }
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, category.getUserId());
                insertStmt.setString(2, category.getName());
                insertStmt.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                System.out.println("Kategori duplikat: " + e.getMessage());
            } else {
                System.out.println("Error insert kategori: " + e.getMessage());
            }
            return false;
        }
    }
}
