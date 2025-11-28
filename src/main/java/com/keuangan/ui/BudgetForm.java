package com.keuangan.ui;

import com.keuangan.dao.BudgetDAO;
import com.keuangan.model.Budget;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BudgetForm extends JFrame {

    private JTextField txtNamaBudget, txtJumlah;
    private JComboBox<String> cmbBulan;
    private JTable tableBudget;
    private DefaultTableModel tableModel;

    private BudgetDAO budgetDAO;
    private int selectedBudgetId = -1; // Untuk update/delete

    public BudgetForm() {
        budgetDAO = new BudgetDAO();
        initComponents();
        loadBudgetData();
    }

    private void initComponents() {
        setTitle("Manajemen Budget Bulanan");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lblTitle = new JLabel("BUDGET BULANAN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setBounds(200, 20, 300, 30);
        add(lblTitle);

        JLabel lblNama = new JLabel("Nama Budget:");
        lblNama.setBounds(50, 80, 120, 25);
        add(lblNama);

        txtNamaBudget = new JTextField();
        txtNamaBudget.setBounds(180, 80, 200, 25);
        add(txtNamaBudget);

        JLabel lblJumlah = new JLabel("Jumlah (Rp):");
        lblJumlah.setBounds(50, 120, 120, 25);
        add(lblJumlah);

        txtJumlah = new JTextField();
        txtJumlah.setBounds(180, 120, 200, 25);
        add(txtJumlah);

        JLabel lblBulan = new JLabel("Bulan:");
        lblBulan.setBounds(50, 160, 120, 25);
        add(lblBulan);

        String[] bulan = {
                "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        };
        cmbBulan = new JComboBox<>(bulan);
        cmbBulan.setBounds(180, 160, 200, 25);
        add(cmbBulan);

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(400, 80, 120, 30);
        btnSimpan.addActionListener(e -> simpanBudget());
        add(btnSimpan);

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(400, 120, 120, 30);
        btnUpdate.addActionListener(e -> updateBudget());
        add(btnUpdate);

        JButton btnDelete = new JButton("Hapus");
        btnDelete.setBounds(400, 160, 120, 30);
        btnDelete.addActionListener(e -> deleteBudget());
        add(btnDelete);

        // Table
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama Budget", "Jumlah", "Bulan"}, 0);
        tableBudget = new JTable(tableModel);
        tableBudget.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableBudget.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tableBudget.getSelectedRow();
                if (row != -1) {
                    selectedBudgetId = (int) tableModel.getValueAt(row, 0);
                    txtNamaBudget.setText(tableModel.getValueAt(row, 1).toString());
                    txtJumlah.setText(tableModel.getValueAt(row, 2).toString());
                    cmbBulan.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tableBudget);
        scrollPane.setBounds(50, 220, 500, 200);
        add(scrollPane);
    }

    private void loadBudgetData() {
        tableModel.setRowCount(0);
        List<Budget> budgets = budgetDAO.getAllBudget();

        for (Budget b : budgets) {
            tableModel.addRow(new Object[]{b.getId(), b.getNamaBudget(), b.getJumlah(), b.getBulan()});
        }
    }

    private void simpanBudget() {
        String nama = txtNamaBudget.getText().trim();
        String jumlahStr = txtJumlah.getText().trim();
        String bulan = cmbBulan.getSelectedItem().toString();

        if (nama.isEmpty() || jumlahStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input tidak boleh kosong!");
            return;
        }

        double jumlah;
        try {
            jumlah = Double.parseDouble(jumlahStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!");
            return;
        }

        Budget budget = new Budget(0, nama, jumlah, bulan);

        if (budgetDAO.addBudget(budget)) {
            JOptionPane.showMessageDialog(this, "Budget berhasil ditambahkan!");
            loadBudgetData();
            resetForm();
        }
    }

    private void updateBudget() {
        if (selectedBudgetId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data di tabel dulu!");
            return;
        }

        String nama = txtNamaBudget.getText().trim();
        String jumlahStr = txtJumlah.getText().trim();
        String bulan = cmbBulan.getSelectedItem().toString();

        double jumlah = Double.parseDouble(jumlahStr);

        Budget budget = new Budget(selectedBudgetId, nama, jumlah, bulan);

        if (budgetDAO.updateBudget(budget)) {
            JOptionPane.showMessageDialog(this, "Data berhasil diupdate!");
            loadBudgetData();
            resetForm();
        }
    }

    private void deleteBudget() {
        if (selectedBudgetId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data dulu!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus data?", "Konfirmasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (budgetDAO.deleteBudget(selectedBudgetId)) {
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
                loadBudgetData();
                resetForm();
            }
        }
    }

    private void resetForm() {
        txtNamaBudget.setText("");
        txtJumlah.setText("");
        cmbBulan.setSelectedIndex(0);
        selectedBudgetId = -1;
    }
}
