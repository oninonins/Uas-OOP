package com.keuangan.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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

import com.keuangan.dao.BudgetDAO;
import com.keuangan.dao.TransactionDAO;
import com.keuangan.model.Budget;
import com.keuangan.model.Transaction;
import com.keuangan.model.User;

// --- CLASS BANTUAN (Helper) ---
// Agar ComboBox bisa menyimpan ID Budget, Nama, dan Saldonya sekaligus
class BudgetOption {
    int id;
    String name;
    double saldo;

    public BudgetOption(int id, String name, double saldo) {
        this.id = id;
        this.name = name;
        this.saldo = saldo;
    }

    // Ini yang akan tampil di tulisan Dropdown
    @Override
    public String toString() {
        NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));
        return name + " (Sisa: Rp " + nf.format(saldo) + ")";
    }
}

public class TransactionForm extends JFrame {

    // Komponen UI
    private JComboBox<BudgetOption> cmbSumberDana; // Dropdown Sumber Uang (Dari DB Budget)
    private JComboBox<String> cmbKategoriPengeluaran; // Dropdown Buat Apa (Manual/Editable)
    
    private JTextField txtAmount, txtDesc;
    private JSpinner dateSpinner;
    private JLabel lblSisaSaldo; 
    
    private JTable tableTrx;
    private DefaultTableModel tableModel;

    // Logic / DAO
    private TransactionDAO trxDAO;
    private BudgetDAO budgetDAO;
    private User currentUser; 
    private int selectedTrxId = -1;
    
    private NumberFormat currencyFormat;

    public TransactionForm(User user) {
        this.currentUser = user;
        trxDAO = new TransactionDAO();
        budgetDAO = new BudgetDAO();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        initComponents();
        loadSumberDana(); // Load data Budget ke Dropdown 1
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

        // --- 1. SUMBER DANA (Dari Budget Mana?) ---
        JLabel lblSource = new JLabel("Ambil Uang Dari:");
        lblSource.setBounds(50, 70, 150, 25);
        add(lblSource);

        cmbSumberDana = new JComboBox<>();
        cmbSumberDana.setBounds(200, 70, 250, 25);
        // Event: Saat sumber dana dipilih, update label info saldo
        cmbSumberDana.addActionListener(e -> updateInfoSaldo());
        add(cmbSumberDana);

        // Label Info Saldo (Validasi Visual)
        JLabel lblInfo = new JLabel("Sisa:");
        lblInfo.setBounds(470, 70, 50, 25);
        add(lblInfo);

        lblSisaSaldo = new JLabel("Rp 0");
        lblSisaSaldo.setFont(new Font("Arial", Font.BOLD, 14));
        lblSisaSaldo.setForeground(new Color(0, 128, 0));
        lblSisaSaldo.setBounds(510, 70, 200, 25);
        add(lblSisaSaldo);

        // --- 2. TANGGAL ---
        JLabel lblDate = new JLabel("Tanggal:");
        lblDate.setBounds(50, 110, 150, 25);
        add(lblDate);

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(200, 110, 150, 25);
        add(dateSpinner);

        // --- 3. KEPERLUAN (Kategori Pengeluaran) ---
        JLabel lblCat = new JLabel("Untuk Keperluan:");
        lblCat.setBounds(50, 150, 150, 25);
        add(lblCat);

        // Isi default kategori pengeluaran umum
        String[] expenses = {"Makan & Minum", "Bensin / Transport", "Belanja Harian", "Tagihan Listrik/Air", "Pulsa/Internet", "Hiburan", "Sedekah", "Lain-lain"};
        cmbKategoriPengeluaran = new JComboBox<>(expenses);
        cmbKategoriPengeluaran.setEditable(true); // PENTING: User bisa ketik manual jika tidak ada di list
        cmbKategoriPengeluaran.setBounds(200, 150, 250, 25);
        add(cmbKategoriPengeluaran);

        // --- 4. NOMINAL ---
        JLabel lblAmount = new JLabel("Nominal (Rp):");
        lblAmount.setBounds(50, 190, 150, 25);
        add(lblAmount);

        txtAmount = new JTextField();
        txtAmount.setBounds(200, 190, 250, 25);
        add(txtAmount);

        // --- 5. DESKRIPSI ---
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
        // Kolom ditambah untuk menampilkan Sumber Dana dan Keperluan
        String[] cols = {"ID", "Tanggal", "Sumber Dana", "Keperluan", "Nominal", "Ket"};
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
                    
                    // Set Keperluan (Kategori Pengeluaran)
                    String kategoriPengeluaran = tableModel.getValueAt(row, 3).toString();
                    cmbKategoriPengeluaran.setSelectedItem(kategoriPengeluaran);
                    
                    // Set Nominal
                    txtAmount.setText(tableModel.getValueAt(row, 4).toString().replace(".0", ""));
                    
                    // Set Deskripsi
                    txtDesc.setText(tableModel.getValueAt(row, 5).toString());
                    
                    // NOTE: Untuk cmbSumberDana agak tricky set-nya karena object, 
                    // tapi biarkan user memilih ulang sumber dana jika ingin edit demi keamanan saldo.
                }
            }
        });
    }

    // --- LOGIC METHOD ---

    private void loadSumberDana() {
        cmbSumberDana.removeAllItems();
        // Mengambil semua budget user dari database
        List<Budget> budgets = budgetDAO.getAllBudget(currentUser.getId()); 
        
        for (Budget b : budgets) {
            // Masukkan ke dropdown sebagai object BudgetOption
            String budgetName = "Budget #" + b.getBudgetId();
            cmbSumberDana.addItem(new BudgetOption(b.getBudgetId(), budgetName, b.getAmount()));
        }
    }
    
    private void updateInfoSaldo() {
        BudgetOption selected = (BudgetOption) cmbSumberDana.getSelectedItem();
        if (selected != null) {
            lblSisaSaldo.setText(currencyFormat.format(selected.saldo));
            
            if (selected.saldo <= 0) {
                lblSisaSaldo.setForeground(Color.RED);
            } else {
                lblSisaSaldo.setForeground(new Color(0, 128, 0));
            }
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        // Pastikan TransactionDAO sudah diupdate query-nya untuk join tabel budget
        List<Transaction> list = trxDAO.getAllTransactions(currentUser.getId());
        
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                t.getId(), 
                t.getTransactionDate(), 
                t.getBudgetName(), // Nama Sumber Dana (misal: Gaji)
                t.getCategory(),   // Keperluan (misal: Makan)
                t.getAmount(), 
                t.getDescription()
            });
        }
    }

    private void saveTransaction() {
        // 1. Validasi Sumber Dana
        BudgetOption source = (BudgetOption) cmbSumberDana.getSelectedItem();
        if (source == null) {
            JOptionPane.showMessageDialog(this, "Pilih Sumber Dana (Budget) dulu!");
            return;
        }

        try {
            // 2. Ambil Data Form
            double amount = Double.parseDouble(txtAmount.getText());
            
            // Validasi Saldo Cukup
            if (amount > source.saldo) {
                JOptionPane.showMessageDialog(this, "Saldo di budget '" + source.name + "' tidak cukup!\nSisa: " + currencyFormat.format(source.saldo));
                return;
            }

            String expenseCategory = cmbKategoriPengeluaran.getSelectedItem().toString(); // Ambil text keperluan
            String desc = txtDesc.getText();
            java.util.Date spinDate = (java.util.Date) dateSpinner.getValue();
            
            // 3. Buat Object Transaksi
            Transaction t = new Transaction();
            t.setUserId(currentUser.getId());
            t.setBudgetId(source.id);        // PENTING: ID Sumber Dana
            t.setCategory(expenseCategory);  // PENTING: Kategori Pengeluaran (Text)
            t.setAmount(amount);
            t.setDescription(desc);
            t.setTransactionDate(new Date(spinDate.getTime()));
            t.setType("PENGELUARAN");

            // 4. Simpan ke Database
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
        cmbKategoriPengeluaran.setSelectedIndex(0);
        selectedTrxId = -1;
        
        loadSumberDana(); // Refresh saldo di dropdown
        loadTableData();  // Refresh tabel
        updateInfoSaldo();
    }
}