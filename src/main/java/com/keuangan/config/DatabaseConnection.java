package com.keuangan.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class DatabaseConnection {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/db_keuangan";
    private static final String USER = "admin";
    private static final String PASSWORD = "password123";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Koneksi Berhasil!");
        } catch (SQLException e) {
            System.out.println("Koneksi Gagal: " + e.getMessage());
        }
        return connection;
    }
}

