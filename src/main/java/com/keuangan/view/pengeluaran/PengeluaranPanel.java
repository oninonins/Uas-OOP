package com.keuangan.view.pengeluaran;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.keuangan.repository.TransaksiRepo;
import com.toedter.calendar.JDateChooser;

public class PengeluaranPanel extends JPanel {

    private JComboBox<String> cbKategori;
    private JTextField tfJumlah;
    private JTextArea taKeterangan;
    private JDateChooser dateChooser;
    private JButton btnSimpan;

    private JTable tableRiwayat;
    private DefaultTableModel tableModel;

    private TransaksiRepo transaksiRepo; 

    public PengeluaranPanel() {
        transaksiRepo = new TransaksiRepo();
        initUI();
        loadRiwayat();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // form untuk inputan
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTanggal = new JLabel("Tanggal:");
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lblTanggal, gbc);

        dateChooser = new JDateChooser();
        dateChooser.setDate(java.sql.Date.valueOf(LocalDate.now()));
        gbc.gridx = 1;
        formPanel.add(dateChooser, gbc);

        JLabel lblKategori = new JLabel("Kategori:");
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lblKategori, gbc);

        cbKategori = new JComboBox<>(new String[]{
                "Makan", "Transport", "Belanja", "Hiburan", "Kesehatan", "Lainnya"
        });
        gbc.gridx = 1;
        formPanel.add(cbKategori, gbc);

        JLabel lblJumlah = new JLabel("Jumlah (Rp):");
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(lblJumlah, gbc);

        tfJumlah = new JTextField();
        gbc.gridx = 1;
        formPanel.add(tfJumlah, gbc);

        JLabel lblKeterangan = new JLabel("Keterangan:");
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(lblKeterangan, gbc);

        taKeterangan = new JTextArea(4, 15);
        JScrollPane scroll = new JScrollPane(taKeterangan);
        gbc.gridx = 1;
        formPanel.add(scroll, gbc);

        btnSimpan = new JButton("Simpan Pengeluaran");
        gbc.gridy = 4;
        formPanel.add(btnSimpan, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Riwayat pengeluaran
        String[] kolom = {"Tanggal", "Kategori", "Jumlah", "Keterangan"};
        tableModel = new DefaultTableModel(kolom, 0);

        tableRiwayat = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(tableRiwayat);

        add(tableScroll, BorderLayout.CENTER);

        
        btnSimpan.addActionListener(e -> simpanPengeluaran());
    }

    private void simpanPengeluaran() {
        try {
            java.util.Date selectedDate = dateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Tanggal tidak boleh kosong");
                return;
            }

            LocalDate localDate = selectedDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            String kategori = cbKategori.getSelectedItem().toString();
            String jumlahText = tfJumlah.getText().trim();

            if (jumlahText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Jumlah tidak boleh kosong");
                return;
            }

            double jumlah = 0;
            try {
                jumlah = Double.parseDouble(jumlahText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Jumlah harus angka!");
                return;
            }

            String keterangan = taKeterangan.getText();
            int userId = 1; // nanti diganti dari user session

            boolean success = transaksiRepo.insertPengeluaran(
                    userId,
                    jumlah,
                    kategori,
                    keterangan,
                    Date.valueOf(localDate)
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Pengeluaran berhasil disimpan!");
                tfJumlah.setText("");
                taKeterangan.setText("");

                loadRiwayat();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan data!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + e.getMessage());
        }
    }

    private void loadRiwayat() {
        tableModel.setRowCount(0);

        int userId = 1; // nanti diganti dari session
        List<Map<String, Object>> data = transaksiRepo.getAllPengeluaran(userId);

        for (Map<String, Object> row : data) {
            tableModel.addRow(new Object[]{
                    row.get("transaction_date"),
                    row.get("category"),
                    row.get("amount"),
                    row.get("description")
            });
        }
    }
}
