package com.keuangan.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.keuangan.dao.TransactionDAO;
import com.keuangan.model.CategoryTotal;
import com.keuangan.model.MonthlyTotal;
import com.keuangan.model.Transaction;
import com.keuangan.model.User;

/**
 * Panel ringkasan statistik:
 * 1) Pie: Pengeluaran per Kategori
 * 2) Line: Tren Pengeluaran Bulanan
 * 3) Horizontal Bar: Top 5 Kategori Terboros
 * 4) Table: Top 10 Transaksi Bulan Berjalan
 */
public class StatisticsPanel extends JPanel {

    private final User currentUser;
    private final TransactionDAO transactionDAO;

    private ChartPanel piePanel;
    private ChartPanel linePanel;
    private ChartPanel topCategoryPanel;
    private JTable topSpendingTable;
    private DefaultTableModel tableModel;

    public StatisticsPanel(User user) {
        this.currentUser = user;
        this.transactionDAO = new TransactionDAO();
        initUI();
        reloadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Header
        JLabel title = new JLabel("Dashboard Statistik", SwingConstants.LEFT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Chart panels
        JPanel chartsTop = new JPanel(new GridLayout(1, 2, 10, 10));
        chartsTop.setOpaque(false);
        JPanel chartsBottom = new JPanel(new GridLayout(1, 2, 10, 10));
        chartsBottom.setOpaque(false);

        piePanel = createEmptyChartPanel("Pengeluaran per Kategori");
        linePanel = createEmptyChartPanel("Tren Pengeluaran Bulanan");
        topCategoryPanel = createEmptyChartPanel("Kategori Terboros");

        chartsTop.add(piePanel);
        chartsTop.add(linePanel);
        chartsBottom.add(topCategoryPanel);
        chartsBottom.add(buildTablePanel());

        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
        center.setOpaque(false);
        center.add(chartsTop);
        center.add(chartsBottom);

        add(center, BorderLayout.CENTER);
    }

    private ChartPanel createEmptyChartPanel(String title) {
        JFreeChart placeholder = ChartFactory.createPieChart(title, new DefaultPieDataset<>(), true, true, false);
        ChartPanel panel = new ChartPanel(placeholder);
        panel.setPreferredSize(new Dimension(520, 300));
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    private JPanel buildTablePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        wrapper.setBackground(Color.WHITE);

        JLabel label = new JLabel("Top 10 Transaksi Bulan Berjalan", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        wrapper.add(label, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Deskripsi", "Nominal", "Kategori", "Tanggal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        topSpendingTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(topSpendingTable);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    public void reloadData() {
        loadPieExpensePerCategory();
        loadLineMonthlyTrend();
        loadTopCategories();
        loadTopTransactions();
    }

    // 2. Pie Chart Pengeluaran per Kategori
    private void loadPieExpensePerCategory() {
        List<CategoryTotal> totals = transactionDAO.getExpensePerCategoryAll(currentUser.getId());
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        if (totals.isEmpty()) {
            dataset.setValue("Tidak ada data", 1);
        } else {
            for (CategoryTotal ct : totals) {
                dataset.setValue(ct.getCategoryName(), ct.getTotal());
            }
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "Pengeluaran per Kategori",
                dataset,
                true,
                true,
                false
        );
        piePanel.setChart(chart);
    }

    // 3. Tren Pengeluaran Bulanan (Line Chart)
    private void loadLineMonthlyTrend() {
        List<MonthlyTotal> monthly = transactionDAO.getMonthlyExpenseTrendAll(currentUser.getId());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (monthly.isEmpty()) {
            dataset.addValue(0, "Pengeluaran", "Tidak ada data");
        } else {
            for (MonthlyTotal mt : monthly) {
                dataset.addValue(mt.getTotal(), "Pengeluaran", mt.getMonthLabel());
            }
        }
        JFreeChart chart = ChartFactory.createLineChart(
                "Tren Pengeluaran Bulanan",
                "Bulan",
                "Jumlah",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        linePanel.setChart(chart);
    }

    // 6. Kategori Terboros (Top 5 Highest Expense Category)
    private void loadTopCategories() {
        List<CategoryTotal> tops = transactionDAO.getTopExpenseCategories(currentUser.getId(), 5);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (tops.isEmpty()) {
            dataset.addValue(0, "Pengeluaran", "Tidak ada data");
        } else {
            for (CategoryTotal ct : tops) {
                dataset.addValue(ct.getTotal(), "Pengeluaran", ct.getCategoryName());
            }
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Kategori Terboros (Top 5)",
                "Kategori",
                "Jumlah",
                dataset,
                PlotOrientation.HORIZONTAL,
                false,
                true,
                false
        );
        topCategoryPanel.setChart(chart);
    }

    // 7. Transaksi Terbesar Bulanan (Top Spending)
    private void loadTopTransactions() {
        List<Transaction> list = transactionDAO.getTopTransactionsCurrentMonth(currentUser.getId(), 10);
        tableModel.setRowCount(0);
        if (list.isEmpty()) {
            tableModel.addRow(new Object[]{"Tidak ada data", "-", "-", "-"});
            return;
        }
        for (Transaction t : list) {
            tableModel.addRow(new Object[]{
                    t.getDescription(),
                    t.getAmount(),
                    t.getCategoryName() != null ? t.getCategoryName() : "-",
                    t.getTransactionDate()
            });
        }
    }

    /**
     * Utility helper untuk membuka panel ini langsung di frame.
     */
    public static void showInFrame(User user) {
        JFrame frame = new JFrame("Dashboard Statistik");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1150, 780);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new StatisticsPanel(user));
        frame.setVisible(true);
    }
}

