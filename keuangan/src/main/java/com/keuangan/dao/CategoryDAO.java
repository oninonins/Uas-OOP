package com.keuangan.dao;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, name, icon FROM categories ORDER BY name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getString("icon")
                ));
            }

        } catch (Exception e) {
            System.out.println("Error load categories: " + e.getMessage());
        }

        return list;
    }
}
