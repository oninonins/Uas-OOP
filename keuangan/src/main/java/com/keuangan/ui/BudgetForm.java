package com.keuangan.ui;

import com.keuangan.dao.BudgetDAO;
import com.keuangan.model.Budget;
import com.keuangan.config.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BudgetForm extends JFrame {

    private JTextField txtNama, txtJumlah;
    private JSpinner dateSpinner;   // DIGANTI DARI TEXTFIELD
    private JButton btnTambah, btnUpdate, btnDelete, btnClear;
    private JTable table;
    private DefaultTableModel tableModel;

    private BudgetDAO budgetDAO;
    private int selectedId = -1;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public BudgetForm() {
        setTitle("Manajemen Budget");
        setSize(600, 450);
        setLayout(null);
        setLocationRelativeTo(null);

        try {
            Connection conn = DatabaseConnection.getConnection();
            budgetDAO = new BudgetDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Koneksi gagal: " + e.getMessage());
        }

        // LABEL & INPUT ----------------------------------------------------

        JLabel lblNama = new JLabel("Nama Budget:");
        lblNama.setBounds(30, 20, 140, 25);
        add(lblNama);

        txtNama = new JTextField();
        txtNama.setBounds(170, 20, 200, 25);
        add(txtNama);

        JLabel lblJumlah = new JLabel("Jumlah:");
        lblJumlah.setBounds(30, 60, 140, 25);
        add(lblJumlah);

        txtJumlah = new JTextField();
        txtJumlah.setBounds(170, 60, 200, 25);
        add(txtJumlah);

        JLabel lblTanggal = new JLabel("Tanggal:");
        lblTanggal.setBounds(30, 100, 180, 25);
        add(lblTanggal);

        // --- DATE SPINNER DISINI ---
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setBounds(170, 100, 200, 25);
        add(dateSpinner);

        // BUTTONS ---------------------------------------------------------
        btnTambah = new JButton("Tambah");
        btnTambah.setBounds(30, 150, 100, 30);
        add(btnTambah);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(140, 150, 100, 30);
        add(btnUpdate);

        btnDelete = new JButton("Hapus");
        btnDelete.setBounds(250, 150, 100, 30);
        add(btnDelete);

        btnClear = new JButton("Clear");
        btnClear.setBounds(360, 150, 100, 30);
        add(btnClear);

        // TABLE ------------------------------------------------------------
        tableModel = new DefaultTableModel(new String[]{"ID", "Nama", "Jumlah", "Tanggal"}, 0);
        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 200, 520, 180);
        add(scrollPane);

        // LOAD AWAL
        loadTable();

        // EVENTS ------------------------------------------------------------

        btnTambah.addActionListener(e -> {
            try {
                String tgl = dateFormat.format((Date) dateSpinner.getValue());

                Budget b = new Budget(
                        0,
                        txtNama.getText(),
                        Double.parseDouble(txtJumlah.getText()),
                        tgl
                );

                if (budgetDAO.addBudget(b)) {
                    loadTable();
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal menambah budget.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error tambah: " + ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(null, "Pilih data dulu!");
                return;
            }
            try {
                String tgl = dateFormat.format((Date) dateSpinner.getValue());

                Budget b = new Budget(
                        selectedId,
                        txtNama.getText(),
                        Double.parseDouble(txtJumlah.getText()),
                        tgl
                );

                if (budgetDAO.updateBudget(b)) {
                    loadTable();
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal update budget.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error update: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            if (selectedId == -1) {
                JOptionPane.showMessageDialog(null, "Pilih data dulu!");
                return;
            }
            try {
                if (budgetDAO.deleteBudget(selectedId)) {
                    loadTable();
                    resetForm();
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal hapus budget.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error delete: " + ex.getMessage());
            }
        });

        btnClear.addActionListener(e -> resetForm());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                selectedId = Integer.parseInt(table.getValueAt(row, 0).toString());
                txtNama.setText(table.getValueAt(row, 1).toString());
                txtJumlah.setText(table.getValueAt(row, 2).toString());

                try {
                    Date dt = dateFormat.parse(table.getValueAt(row, 3).toString());
                    dateSpinner.setValue(dt);
                } catch (Exception ex) {
                    dateSpinner.setValue(new Date());
                }
            }
        });

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // LOAD TABLE ------------------------------------------------------------
    private void loadTable() {
        tableModel.setRowCount(0);
        try {
            List<Budget> list = budgetDAO.getAllBudget();
            for (Budget b : list) {
                tableModel.addRow(new Object[]{
                        b.getId(),
                        b.getNamaBudget(),
                        b.getJumlah(),
                        b.getBulan()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal load table: " + e.getMessage());
        }
    }

    // RESET --------------------------------------------------------------
    private void resetForm() {
        txtNama.setText("");
        txtJumlah.setText("");
        dateSpinner.setValue(new Date());
        selectedId = -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BudgetForm());
    }
}
