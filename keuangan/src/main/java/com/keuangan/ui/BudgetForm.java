package com.keuangan.ui;

import java.util.ArrayList;
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

import com.keuangan.dao.BudgetDAO;
import com.keuangan.dao.CategoryDAO;
import com.keuangan.model.Budget;
import com.keuangan.model.Category;
import com.keuangan.model.User;

public class BudgetForm extends JFrame {

    private User currentUser;

    private javax.swing.JComboBox<CategoryItem> cmbKategori;
    private JButton btnTambahKategori;
    private JButton btnHapusKategori;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnReset;
    private JTextField txtJumlah;
    private JSpinner dateSpinner;

    private JTable table;
    private DefaultTableModel model;

    private final CategoryDAO categoryDAO;
    private final BudgetDAO budgetDAO;
    private int selectedBudgetId = -1;
    private List<Budget> budgetsCache = new ArrayList<>();

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
        this.budgetDAO = new BudgetDAO();

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
        lblTanggal.setBounds(50, 140, 150, 25);
        add(lblTanggal);

        // DATE PICKER yang kamu minta (dd-MM-yyyy)
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy");
        dateSpinner.setEditor(editor);
        dateSpinner.setBounds(200, 140, 200, 25);
        add(dateSpinner);

        JButton btnSave = new JButton("Tambah Budget");
        btnSave.setBounds(200, 190, 150, 30);
        add(btnSave);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(370, 190, 90, 30);
        add(btnUpdate);

        btnDelete = new JButton("Hapus");
        btnDelete.setBounds(470, 190, 90, 30);
        add(btnDelete);

        btnReset = new JButton("Reset");
        btnReset.setBounds(50, 190, 120, 30);
        add(btnReset);

        btnSave.addActionListener(e -> saveData());
        btnUpdate.addActionListener(e -> updateData());
        btnDelete.addActionListener(e -> deleteData());
        btnReset.addActionListener(e -> resetForm());

        model = new DefaultTableModel(new Object[]{"ID", "Kategori", "Jumlah", "Tanggal"}, 0);
        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(50, 220, 500, 300);
        add(scroll);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedBudgetId = Integer.parseInt(model.getValueAt(row, 0).toString());
                    Budget b = findBudgetById(selectedBudgetId);
                    if (b != null) {
                        String catName = b.getCategoryName();
                        double amount = b.getAmount();
                        Date date = b.getBudgetDate();
                        cmbKategori.setSelectedItem(catName);
                        txtJumlah.setText(String.valueOf(amount));
                        dateSpinner.setValue(date);
                    }
                }
            }
        });

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

    // Public helper to allow refresh from other dialogs if needed
    public void refreshCategories() {
        loadCategories();
    }

    private void deleteCategory() {
        CategoryItem selected = (CategoryItem) cmbKategori.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang akan dihapus.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus kategori \"" + selected.name + "\" ?\nBudget yang terkait dapat ikut terhapus.",
                "Konfirmasi Hapus Kategori",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = categoryDAO.delete(selected.id, currentUser.getId());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus.");
            loadCategories();
            loadData(); // refresh tabel budget jika ada yang terhapus cascade
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menghapus kategori.");
        }
    }

    private boolean validateInput(CategoryItem selected, String amountText, Date selectedDate) {
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Pilih kategori terlebih dahulu.");
            return false;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.");
            return false;
        }
        if (amount <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0.");
            return false;
        }
        if (selectedDate == null) {
            JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong.");
            return false;
        }
        return true;
    }

    private void saveData() {
        CategoryItem selected = (CategoryItem) cmbKategori.getSelectedItem();
        Date selectedDate = (Date) dateSpinner.getValue();
        if (!validateInput(selected, txtJumlah.getText(), selectedDate)) return;

        Budget budget = new Budget(
                currentUser.getId(),
                selected.id,
                Double.parseDouble(txtJumlah.getText()),
                new java.sql.Date(selectedDate.getTime())
        );

        if (budgetDAO.insert(budget)) {
            JOptionPane.showMessageDialog(this, "Budget berhasil ditambahkan!");
            resetForm();
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan budget.");
        }
    }

    private void updateData() {
        if (selectedBudgetId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data di tabel terlebih dahulu.");
            return;
        }

        CategoryItem selected = (CategoryItem) cmbKategori.getSelectedItem();
        Date selectedDate = (Date) dateSpinner.getValue();
        if (!validateInput(selected, txtJumlah.getText(), selectedDate)) return;

        Budget budget = new Budget(
                selectedBudgetId,
                currentUser.getId(),
                selected.id,
                Double.parseDouble(txtJumlah.getText()),
                new java.sql.Date(selectedDate.getTime()),
                null,
                selected.name
        );

        if (budgetDAO.update(budget)) {
            JOptionPane.showMessageDialog(this, "Budget berhasil diupdate!");
            resetForm();
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengupdate budget.");
        }
    }

    private void deleteData() {
        if (selectedBudgetId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data di tabel terlebih dahulu.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus budget ini? Kategori terkait akan ikut dihapus.", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Budget b = findBudgetById(selectedBudgetId);
        Integer categoryId = b != null ? b.getCategoryId() : null;

        boolean budgetDeleted = budgetDAO.delete(selectedBudgetId, currentUser.getId());
        if (!budgetDeleted) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus budget.");
            return;
        }

        if (categoryId != null) {
            categoryDAO.delete(categoryId, currentUser.getId());
        }

        JOptionPane.showMessageDialog(this, "Budget dan kategori terkait berhasil dihapus.");
        resetForm();
        loadCategories();
        loadData();
    }

    private void resetForm() {
        txtJumlah.setText("");
        dateSpinner.setValue(new java.util.Date());
        if (cmbKategori.getItemCount() > 0) {
            cmbKategori.setSelectedIndex(0);
        }
        selectedBudgetId = -1;
    }

    private void loadData() {
        model.setRowCount(0);

        budgetsCache = budgetDAO.getAll(currentUser.getId());
        for (Budget b : budgetsCache) {
            model.addRow(new Object[]{
                    b.getBudgetId(),
                    b.getCategoryName(),
                    b.getAmount(),
                    b.getBudgetDate()
            });
        }
    }

    private Budget findBudgetById(int id) {
        if (budgetsCache == null) return null;
        for (Budget b : budgetsCache) {
            if (b.getBudgetId() == id) return b;
        }
        return null;
    }
}
