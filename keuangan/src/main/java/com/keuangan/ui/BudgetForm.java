package com.keuangan.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.dao.CategoryDAO;
import com.keuangan.model.Category;
import com.keuangan.model.User;

public class BudgetForm extends JFrame {

    private User currentUser;

    private javax.swing.JComboBox<CategoryItem> cmbKategori;
    private JButton btnTambahKategori;
    private JTextField txtJumlah;
    private JSpinner dateSpinner;

    private JTable table;
    private DefaultTableModel model;

    private final CategoryDAO categoryDAO;

    // Item untuk menyimpan ID kategori asli di ComboBox
    private static class CategoryItem {
        final int id;
        final String name;

        CategoryItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public BudgetForm(User user) {
        this.currentUser = user;
        this.categoryDAO = new CategoryDAO();

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

        cmbKategori = new javax.swing.JComboBox<>();
        cmbKategori.setBounds(200, 70, 200, 25);
        add(cmbKategori);

        btnTambahKategori = new JButton("Tambah Kategori");
        btnTambahKategori.setBounds(420, 70, 150, 25);
        btnTambahKategori.addActionListener(e -> openCategoryForm());
        add(btnTambahKategori);

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

        loadCategories();
    }

    private void openCategoryForm() {
        CategoryForm form = new CategoryForm(this, currentUser, this::loadCategories);
        form.setVisible(true);
    }

    private void loadCategories() {
        cmbKategori.removeAllItems();
        List<Category> categories = categoryDAO.getAllByUser(currentUser.getId());
        if (categories.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kategori belum tersedia di database. Tambahkan kategori terlebih dahulu.");
            return;
        }
        for (Category c : categories) {
            cmbKategori.addItem(new CategoryItem(c.getCategoryId(), c.getName()));
        }
    }

    private void saveData() {

        Date selectedDate = (Date) dateSpinner.getValue();
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());

        try (Connection conn = DatabaseConnection.getConnection()) {

            String sql = "INSERT INTO budgets (user_id, category_id, amount, created_at) VALUES (?, ?, ?, ?)";

            PreparedStatement stmt = conn.prepareStatement(sql);

            CategoryItem selected = (CategoryItem) cmbKategori.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Pilih kategori yang valid.");
                return;
            }

            stmt.setInt(1, currentUser.getId());
            stmt.setInt(2, selected.id);
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
