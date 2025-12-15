package com.keuangan.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import com.keuangan.dao.BudgetDAO;
import com.keuangan.dao.TransactionDAO;
import com.keuangan.model.Transaction;
import com.keuangan.model.User;
import com.keuangan.util.FinancialStatusUtil;

public class ReportFrame extends JFrame {

    private final User currentUser;
    private final TransactionDAO transactionDAO;
    private final BudgetDAO budgetDAO;

    private JComboBox<String> cbPeriode;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    private JSpinner spStartDate;
    private JSpinner spEndDate;

    private JComboBox<String> cbBulan;
    private JSpinner spTahunBulanan;

    private JSpinner spTahunTahunan;

    private JTable table;
    private DefaultTableModel tableModel;

    private JLabel lblTotalBudget;
    private JLabel lblTotalPengeluaran;
    private JLabel lblSisaDana;
    private JLabel lblStatusValue;

    public ReportFrame(User user) {
        this.currentUser = user;
        this.transactionDAO = new TransactionDAO();
        this.budgetDAO = new BudgetDAO();
        initComponents();
    }

    private void initComponents() {
        setTitle("Laporan Keuangan");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildFilterPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildSummaryPanel(), BorderLayout.SOUTH);
    }

    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        cbPeriode = new JComboBox<>(new String[]{"Mingguan", "Bulanan", "Tahunan"});
        cbPeriode.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                showCard((String) e.getItem());
            }
        });
        JButton btnTampilkan = new JButton("Tampilkan");
        btnTampilkan.addActionListener(e -> loadReport());

        topRow.add(new JLabel("Periode:"));
        topRow.add(cbPeriode);
        topRow.add(btnTampilkan);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(buildWeeklyPanel(), "Mingguan");
        cardPanel.add(buildMonthlyPanel(), "Bulanan");
        cardPanel.add(buildYearlyPanel(), "Tahunan");
        showCard("Mingguan");

        panel.add(topRow, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildWeeklyPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        spStartDate = new JSpinner(new SpinnerDateModel());
        spEndDate = new JSpinner(new SpinnerDateModel());

        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(spStartDate, "yyyy-MM-dd");
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(spEndDate, "yyyy-MM-dd");
        spStartDate.setEditor(startEditor);
        spEndDate.setEditor(endEditor);

        panel.add(new JLabel("Mulai:"));
        panel.add(spStartDate);
        panel.add(new JLabel("Sampai:"));
        panel.add(spEndDate);
        return panel;
    }

    private JPanel buildMonthlyPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        String[] months = new java.text.DateFormatSymbols().getMonths();
        cbBulan = new JComboBox<>(Arrays.copyOf(months, 12));

        int currentYear = LocalDate.now().getYear();
        spTahunBulanan = new JSpinner(new SpinnerNumberModel(currentYear, currentYear - 10, currentYear + 10, 1));

        panel.add(new JLabel("Bulan:"));
        panel.add(cbBulan);
        panel.add(new JLabel("Tahun:"));
        panel.add(spTahunBulanan);
        return panel;
    }

    private JPanel buildYearlyPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        int currentYear = LocalDate.now().getYear();
        spTahunTahunan = new JSpinner(new SpinnerNumberModel(currentYear, currentYear - 20, currentYear + 20, 1));

        panel.add(new JLabel("Tahun:"));
        panel.add(spTahunTahunan);
        return panel;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"Tanggal", "Kategori", "Deskripsi", "Jenis", "Jumlah"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        return new JScrollPane(table);
    }

    private JPanel buildSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Total Budget
        lblTotalBudget = new JLabel("0");
        lblTotalBudget.setFont(lblTotalBudget.getFont().deriveFont(Font.BOLD, 14f));
        
        // Total Pengeluaran
        lblTotalPengeluaran = new JLabel("0");
        lblTotalPengeluaran.setFont(lblTotalPengeluaran.getFont().deriveFont(Font.BOLD, 14f));
        
        // Sisa Dana
        lblSisaDana = new JLabel("0");
        lblSisaDana.setFont(lblSisaDana.getFont().deriveFont(Font.BOLD, 14f));
        
        // Status
        lblStatusValue = new JLabel(FinancialStatusUtil.STATUS_NETRAL);
        lblStatusValue.setFont(lblStatusValue.getFont().deriveFont(Font.BOLD, 14f));

        panel.add(new JLabel("Total Budget:"));
        panel.add(lblTotalBudget);
        panel.add(new JLabel("Total Pengeluaran:"));
        panel.add(lblTotalPengeluaran);
        panel.add(new JLabel("Sisa Dana:"));
        panel.add(lblSisaDana);
        panel.add(new JLabel("Status:"));
        panel.add(lblStatusValue);
        return panel;
    }

    private void showCard(String key) {
        cardLayout.show(cardPanel, key);
    }

    private void loadReport() {
        String periode = (String) cbPeriode.getSelectedItem();
        if (periode == null) return;

        try {
            List<Transaction> transactions;
            double totalBudget;
            BigDecimal totalPengeluaran;

            switch (periode) {
                case "Mingguan": {
                    Date start = toSqlDate((java.util.Date) spStartDate.getValue());
                    Date end = toSqlDate((java.util.Date) spEndDate.getValue());
                    if (start.after(end)) {
                        JOptionPane.showMessageDialog(this, "Tanggal mulai harus sebelum atau sama dengan tanggal akhir.");
                        return;
                    }
                    transactions = transactionDAO.getTransactionsByDateRange(currentUser.getId(), start, end);
                    totalPengeluaran = hitungTotal(transactions);
                    totalBudget = budgetDAO.getTotalBudgetByDateRange(currentUser.getId(), start, end);
                    break;
                }
                case "Bulanan": {
                    int month = cbBulan.getSelectedIndex() + 1;
                    int year = (int) spTahunBulanan.getValue();
                    transactions = transactionDAO.getTransactionsByMonth(currentUser.getId(), month, year);
                    totalPengeluaran = hitungTotal(transactions);
                    totalBudget = budgetDAO.getTotalBudgetByMonth(currentUser.getId(), month, year);
                    break;
                }
                default: { // Tahunan
                    int year = (int) spTahunTahunan.getValue();
                    transactions = transactionDAO.getTransactionsByYear(currentUser.getId(), year);
                    totalPengeluaran = hitungTotal(transactions);
                    totalBudget = budgetDAO.getTotalBudgetByYear(currentUser.getId(), year);
                    break;
                }
            }

            // Hitung sisa dana
            BigDecimal budgetBigDecimal = BigDecimal.valueOf(totalBudget);
            BigDecimal sisaDana = budgetBigDecimal.subtract(totalPengeluaran);
            
            // Hitung status berdasarkan persentase
            String status = FinancialStatusUtil.hitungStatusKeuangan(totalPengeluaran, budgetBigDecimal);
            
            // Update tabel
            isiTabel(transactions);
            
            // Update summary labels
            lblTotalBudget.setText(formatRupiah(budgetBigDecimal));
            lblTotalPengeluaran.setText(formatRupiah(totalPengeluaran));
            lblSisaDana.setText(formatRupiah(sisaDana));
            lblStatusValue.setText(status);

            if (transactions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tidak ada data transaksi pengeluaran untuk periode ini.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat laporan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private String formatRupiah(BigDecimal amount) {
        return "Rp " + amount.toPlainString();
    }

    private BigDecimal hitungTotal(List<Transaction> list) {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaction t : list) {
            total = total.add(BigDecimal.valueOf(t.getAmount()));
        }
        return total;
    }

    private void isiTabel(List<Transaction> list) {
        tableModel.setRowCount(0);
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                    t.getTransactionDate(),
                    t.getCategoryName() != null ? t.getCategoryName() : "-",
                    t.getDescription() != null ? t.getDescription() : "-",
                    "Keluar",
                    t.getAmount()
            });
        }
    }

    private Date toSqlDate(java.util.Date date) {
        return new Date(date.getTime());
    }

    public static void showForUser(User user) {
        ReportFrame frame = new ReportFrame(user);
        frame.setVisible(true);
    }
}

