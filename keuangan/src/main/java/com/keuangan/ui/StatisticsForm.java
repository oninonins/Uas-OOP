package com.keuangan.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.keuangan.dao.BudgetDAO;
import com.keuangan.dao.CategoryDAO;
import com.keuangan.dao.TransactionDAO;
import com.keuangan.model.Category;
import com.keuangan.model.CategoryTotal;
import com.keuangan.model.IncomeExpense;
import com.keuangan.model.MonthlyTotal;
import com.keuangan.model.User;

public class StatisticsForm extends JFrame {

    private final User currentUser;
    private final TransactionDAO transactionDAO;
    private final BudgetDAO budgetDAO;
    private final CategoryDAO categoryDAO;

    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;

    private ChartPanel piePanel;
    private ChartPanel incomeExpensePanel;
    private ChartPanel trendPanel;
    private ChartPanel budgetPanel;

    public StatisticsForm(User user) {
        this.currentUser = user;
        this.transactionDAO = new TransactionDAO();
        this.budgetDAO = new BudgetDAO();
        this.categoryDAO = new CategoryDAO();

        initUI();
        refreshCharts();
    }

    private void initUI() {
        setTitle("Statistik Keuangan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel filterPanel = buildFilterPanel();
        JPanel chartsPanel = buildChartsPanel();

        add(filterPanel, BorderLayout.NORTH);
        add(chartsPanel, BorderLayout.CENTER);
    }

    private JPanel buildFilterPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblStart = new JLabel("Tanggal Mulai:");
        JLabel lblEnd = new JLabel("Tanggal Akhir:");

        startDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner = new JSpinner(new SpinnerDateModel());

        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "dd-MM-yyyy");
        startDateSpinner.setEditor(startEditor);
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "dd-MM-yyyy");
        endDateSpinner.setEditor(endEditor);

        // Default periode: awal bulan hingga hari ini
        Calendar cal = Calendar.getInstance();
        endDateSpinner.setValue(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, 1);
        startDateSpinner.setValue(cal.getTime());

        ChangeListener listener = e -> refreshCharts();
        startDateSpinner.addChangeListener(listener);
        endDateSpinner.addChangeListener(listener);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> refreshCharts());

        panel.add(lblStart);
        panel.add(startDateSpinner);
        panel.add(lblEnd);
        panel.add(endDateSpinner);
        panel.add(btnRefresh);

        return panel;
    }

    private JPanel buildChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        piePanel = new ChartPanel(null);
        incomeExpensePanel = new ChartPanel(null);
        trendPanel = new ChartPanel(null);
        budgetPanel = new ChartPanel(null);

        piePanel.setPreferredSize(new Dimension(500, 300));
        incomeExpensePanel.setPreferredSize(new Dimension(500, 300));
        trendPanel.setPreferredSize(new Dimension(500, 300));
        budgetPanel.setPreferredSize(new Dimension(500, 300));

        panel.add(piePanel);
        panel.add(incomeExpensePanel);
        panel.add(trendPanel);
        panel.add(budgetPanel);

        return panel;
    }

    private void refreshCharts() {
        Date start = toSqlDate(startDateSpinner.getValue());
        Date end = toSqlDate(endDateSpinner.getValue());

        if (start.after(end)) {
            JOptionPane.showMessageDialog(this, "Tanggal mulai tidak boleh setelah tanggal akhir.");
            return;
        }

        loadExpensePie(start, end);
        loadIncomeExpenseBar(start, end);
        loadMonthlyTrend(start, end);
        loadBudgetVsRealization(start, end);
    }

    private Date toSqlDate(Object spinnerValue) {
        if (spinnerValue instanceof java.util.Date) {
            return new Date(((java.util.Date) spinnerValue).getTime());
        }
        return new Date(System.currentTimeMillis());
    }

    private void loadExpensePie(Date start, Date end) {
        List<CategoryTotal> totals = transactionDAO.getExpensePerCategory(currentUser.getId(), start, end);

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

    private void loadIncomeExpenseBar(Date start, Date end) {
        IncomeExpense totals = transactionDAO.getIncomeExpense(currentUser.getId(), start, end);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(totals.getIncome(), "Pemasukan", "Periode");
        dataset.addValue(totals.getExpense(), "Pengeluaran", "Periode");

        JFreeChart chart = ChartFactory.createBarChart(
                "Pemasukan vs Pengeluaran",
                "Jenis",
                "Jumlah",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        incomeExpensePanel.setChart(chart);
    }

    private void loadMonthlyTrend(Date start, Date end) {
        List<MonthlyTotal> monthly = transactionDAO.getMonthlyExpenseTrend(currentUser.getId(), start, end);

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

        trendPanel.setChart(chart);
    }

    private void loadBudgetVsRealization(Date start, Date end) {
        Map<Integer, Double> budgetMap = budgetDAO.getBudgetByCategoryMap(currentUser.getId(), start, end);
        Map<Integer, Double> expenseMap = transactionDAO.getExpenseByCategoryMap(currentUser.getId(), start, end);

        // Ambil nama kategori untuk label
        Map<Integer, String> categoryNames = new HashMap<>();
        List<Category> categories = categoryDAO.getAllByUser(currentUser.getId());
        for (Category c : categories) {
            categoryNames.put(c.getCategoryId(), c.getName());
        }

        Set<Integer> categoryIds = new HashSet<>();
        categoryIds.addAll(budgetMap.keySet());
        categoryIds.addAll(expenseMap.keySet());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (categoryIds.isEmpty()) {
            dataset.addValue(0, "Budget", "Tidak ada data");
        } else {
            for (Integer catId : categoryIds) {
                String label = categoryNames.getOrDefault(catId, "Kategori " + catId);
                double budget = budgetMap.getOrDefault(catId, 0.0);
                double realization = expenseMap.getOrDefault(catId, 0.0);
                dataset.addValue(budget, "Budget", label);
                dataset.addValue(realization, "Pengeluaran", label);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Realisasi Anggaran vs Target",
                "Kategori",
                "Jumlah",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        budgetPanel.setChart(chart);
    }
}

