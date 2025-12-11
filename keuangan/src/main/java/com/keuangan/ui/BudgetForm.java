package com.keuangan.ui;

import com.keuangan.model.User;
import com.keuangan.config.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.util.Date;

public class BudgetForm extends JFrame {

    private User currentUser;

    private JComboBox<String> cmbKategori;
    private JTextField txtJumlah;
    private JSpinner dateSpinner;

    private JTable table;
    private DefaultTableModel model;

    public BudgetForm(User user) {
        this.currentUser = user;

        setTitle("Manajemen Budget");
        setSize(620, 560);
        setLayout(null);
        setLocationRelativeTo(null);

        initUI();
        loadData();
    }

    private void initUI() {

        JLabel lblJumlah = new JLabel("Jumlah:");
        lblJumlah.setBounds(50, 30, 150, 25);
        add(lblJumlah);

        txtJumlah = new JTextField();
        txtJumlah.setBounds(200, 30, 200, 25);
        add(txtJumlah);

        JLabel lblKategori = new JLabel("Kategori:");
        lblKategori.setBounds(50, 70, 150, 25);
        add(lblKategori);

        cmbKategori = new JComboBox<>(new String[]{
                "Makan & Minum",
                "Transportasi",
                "Belanja",
                "Hiburan",
                "Tagihan",
                "Kesehatan",
                "Lainnya"
        });
        cmbKategori.setBounds(200, 70, 200, 25);
        add(cmbKategori);

        JLabel lblTanggal = new JLabel("Tanggal:");
        lblTanggal.setBounds(50, 110, 150, 25);
        add(lblTanggal);

        // DATE PICKER yang kamu minta (dd-MM-yyyy)
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy");
        dateSpinner.setEditor(editor);
        dateSpinner.setBounds(200, 110, 200, 25);
        add(dateSpinner);

        JButton btnSave = new JButton("Tambah Budget");
        btnSave.setBounds(200, 160, 150, 30);
        add(btnSave);

        btnSave.addActionListener(e -> saveData());

        model = new DefaultTableModel(new Object[]{"ID", "Kategori", "Jumlah", "Tanggal"}, 0);
        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(50, 220, 500, 300);
        add(scroll);
    }

    private int convertKategoriToId(String kategoriName) {
        if (kategoriName == null) return 7;

        switch (kategoriName) {
            case "Makan & Minum": return 1;
            case "Transportasi": return 2;
            case "Belanja": return 3;
            case "Hiburan": return 4;
            case "Tagihan": return 5;
            case "Kesehatan": return 6;
            default: return 7;
        }
    }

    private void saveData() {

        Date selectedDate = (Date) dateSpinner.getValue();
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = "INSERT INTO budgets (user_id, category_id, amount, created_at) VALUES (?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, currentUser.getId());
            stmt.setInt(2, convertKategoriToId(cmbKategori.getSelectedItem().toString()));
            stmt.setDouble(3, Double.parseDouble(txtJumlah.getText()));
            stmt.setDate(4, sqlDate);  // SIMPAN TANGGAL

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Budget berhasil ditambahkan!");
            loadData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error insert: " + e.getMessage());
        }
    }

    private void loadData() {
        model.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql =
                    "SELECT b.budget_id, c.name AS category, b.amount, b.created_at " +
                    "FROM budgets b " +
                    "JOIN categories c ON b.category_id = c.category_id " +
                    "WHERE b.user_id = ? " +
                    "ORDER BY b.created_at DESC";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUser.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("budget_id"),
                        rs.getString("category"),
                        rs.getDouble("amount"),
                        rs.getDate("created_at")  // TAMPILKAN TANGGAL
                });
            }

        } catch (SQLException e) {
            System.out.println("Error load: " + e.getMessage());
        }
    }
}
