package com.keuangan.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keuangan.config.DatabaseConnection;

public class TransaksiRepo {

    public boolean insertPengeluaran(int userId, double amount, String category,
                                     String description, Date date) {

        String sql = "INSERT INTO transactions (user_id, amount, category, description, transaction_date, type) " +
                     "VALUES (?, ?, ?, ?, ?, 'PENGELUARAN')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setDouble(2, amount);
            stmt.setString(3, category);
            stmt.setString(4, description);
            stmt.setDate(5, date);

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public List<Map<String, Object>> getAllPengeluaran(int userId) {

        List<Map<String, Object>> list = new ArrayList<>();

        String sql = "SELECT transaction_date, category, amount, description " +
                     "FROM transactions WHERE user_id = ? AND type = 'PENGELUARAN' " +
                     "ORDER BY transaction_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("transaction_date", rs.getDate("transaction_date"));
                row.put("category", rs.getString("category"));
                row.put("amount", rs.getDouble("amount"));
                row.put("description", rs.getString("description"));
                list.add(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
