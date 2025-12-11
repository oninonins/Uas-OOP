package com.keuangan.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;

import com.keuangan.dao.CategoryDAO;
import com.keuangan.dao.TransactionDAO;
import com.keuangan.model.Category;
import com.keuangan.model.Transaction;
import com.keuangan.model.User;

// Helper agar ComboBox kategori menyimpan ID & nama
class CategoryItem {
    int id;
    String name;

    CategoryItem(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

public class TransactionForm extends JFrame {

    // Komponen UI
    private JComboBox<CategoryItem> cmbKategori; // Dropdown kategori dari DB
    
    private JTextField txtSisa, txtAmount, txtDesc;
    private JSpinner dateSpinner;
    
    private JTable tableTrx;
    private DefaultTableModel tableModel;

    // Logic / DAO
    private TransactionDAO trxDAO;
    private CategoryDAO categoryDAO;
    private User currentUser; 
    private int selectedTrxId = -1;
    
    public TransactionForm(User user) {
        this.currentUser = user;
        trxDAO = new TransactionDAO();
        categoryDAO = new CategoryDAO();
        
        initComponents();
        loadCategories();
        loadTableData();  // Load data Transaksi ke Tabel
    }

    private void initComponents() {
        setTitle("Catat Pengeluaran");
        setSize(800, 600); // Ukuran sedikit diperlebar
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lblTitle = new JLabel("INPUT PENGELUARAN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBounds(300, 20, 300, 30);
        add(lblTitle);

        // --- 1. TANGGAL ---
        JLabel lblDate = new JLabel("Tanggal:");
        lblDate.setBounds(50, 70, 150, 25);
        add(lblDate);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(200, 70, 150, 25);
        add(dateSpinner);

        // --- 2. KATEGORI ---
        JLabel lblBudget = new JLabel("Kategori:");
        lblBudget.setBounds(50, 110, 150, 25);
        add(lblBudget);

        cmbKategori = new JComboBox<>();
        cmbKategori.setBounds(200, 110, 250, 25);
        cmbKategori.addActionListener(e -> updateSisaSaldo());
        add(cmbKategori);

        JLabel lblSisa = new JLabel("Sisa Saldo:");
        lblSisa.setBounds(50, 150, 150, 25);
        add(lblSisa);

        txtSisa = new JTextField();
        txtSisa.setBounds(200, 150, 250, 25);
        txtSisa.setEditable(false);
        add(txtSisa);

        // --- 3. NOMINAL ---
        JLabel lblAmount = new JLabel("Nominal (Rp):");
        lblAmount.setBounds(50, 190, 150, 25);
        add(lblAmount);

        txtAmount = new JTextField();
        txtAmount.setBounds(200, 190, 250, 25);
        add(txtAmount);

        // --- 4. DESKRIPSI ---
        JLabel lblDesc = new JLabel("Keterangan:");
        lblDesc.setBounds(50, 230, 150, 25);
        add(lblDesc);

        txtDesc = new JTextField();
        txtDesc.setBounds(200, 230, 250, 25);
        add(txtDesc);

        // --- BUTTONS ---
        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(200, 270, 90, 30);
        btnSimpan.addActionListener(e -> saveTransaction());
        add(btnSimpan);

        JButton btnHapus = new JButton("Hapus");
        btnHapus.setBounds(310, 270, 90, 30);
        btnHapus.setBackground(Color.RED);
        btnHapus.setForeground(Color.WHITE);
        btnHapus.addActionListener(e -> deleteTransaction());
        add(btnHapus);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(420, 270, 90, 30);
        btnReset.addActionListener(e -> resetForm());
        add(btnReset);

        // --- TABLE ---
        String[] cols = {"ID", "Tanggal", "Kategori", "Nominal", "Ket"};
        tableModel = new DefaultTableModel(cols, 0);
        tableTrx = new JTable(tableModel);
        
        JScrollPane scroll = new JScrollPane(tableTrx);
        scroll.setBounds(30, 320, 720, 200);
        add(scroll);

        // Event Klik Tabel
        tableTrx.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableTrx.getSelectedRow();
                if (row != -1) {
                    selectedTrxId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    
                    // Set Tanggal
                    Date sqlDate = (Date) tableModel.getValueAt(row, 1);
                    dateSpinner.setValue(new java.util.Date(sqlDate.getTime()));
                    
                    String catName = tableModel.getValueAt(row, 2).toString();
                    cmbKategori.setSelectedItem(catName);
                    
                    txtAmount.setText(tableModel.getValueAt(row, 3).toString().replace(".0", ""));
                    txtDesc.setText(tableModel.getValueAt(row, 4).toString());
                    updateSisaSaldo();
                }
            }
        });
    }

    // --- LOGIC METHOD ---

    private void loadCategories() {
        cmbKategori.removeAllItems();
        List<Category> categories = categoryDAO.getAllByUser(currentUser.getId());
        for (Category c : categories) {
            cmbKategori.addItem(new CategoryItem(c.getCategoryId(), c.getName()));
        }
        updateSisaSaldo();
    }

    private void updateSisaSaldo() {
        CategoryItem selected = (CategoryItem) cmbKategori.getSelectedItem();
        if (selected == null) {
            txtSisa.setText("");
            return;
        }
        int totalBudget = trxDAO.getTotalBudgetByCategoryId(selected.id);
        int totalPengeluaran = trxDAO.getTotalPengeluaranByCategoryId(selected.id);
        int sisa = totalBudget - totalPengeluaran;
        txtSisa.setText(String.valueOf(sisa));
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<Transaction> list = trxDAO.getAllTransactions(currentUser.getId());
        
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(), 
                t.getTransactionDate(), 
                t.getCategoryName(),   // Nama kategori
                t.getAmount(), 
                t.getDescription()
            });
        }
    }

    private void saveTransaction() {
        try {
            // Ambil Data Form
            double amount = Double.parseDouble(txtAmount.getText());
            CategoryItem selectedCat = (CategoryItem) cmbKategori.getSelectedItem();
            if (selectedCat == null) {
                JOptionPane.showMessageDialog(this, "Pilih kategori dulu.");
                return;
            }
            int sisa = Integer.parseInt(txtSisa.getText().isEmpty() ? "0" : txtSisa.getText());
            if (amount > sisa) {
                JOptionPane.showMessageDialog(this, "Nominal melebihi sisa saldo kategori.");
                return;
            }

            String desc = txtDesc.getText();
            java.util.Date spinDate = (java.util.Date) dateSpinner.getValue();
            
            // Buat Object Transaksi
            Transaction t = new Transaction();
            t.setUserId(currentUser.getId());
            t.setCategoryId(selectedCat.id);
            t.setCategoryName(selectedCat.name);
            t.setAmount(amount);
            t.setDescription(desc);
            t.setTransactionDate(new Date(spinDate.getTime()));

            // Simpan ke Database
            if (trxDAO.addTransaction(t)) {
                JOptionPane.showMessageDialog(this, "Transaksi Berhasil Disimpan!");
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan transaksi.");
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nominal harus berupa angka!");
        }
    }

    private void deleteTransaction() {
        if (selectedTrxId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data di tabel dulu!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Hapus transaksi ini?\nSaldo akan dikembalikan ke budget asal.", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (trxDAO.deleteTransaction(selectedTrxId)) {
                JOptionPane.showMessageDialog(this, "Transaksi dihapus & saldo dikembalikan.");
                resetForm();
            }
        }
    }

    private void resetForm() {
        txtAmount.setText("");
        txtDesc.setText("");
        dateSpinner.setValue(new java.util.Date());
        cmbKategori.setSelectedIndex(0);
        selectedTrxId = -1;
        
        loadTableData();  // Refresh tabel
        updateSisaSaldo(); // Refresh sisa
    }
}