package com.keuangan.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.keuangan.config.DatabaseConnection;
import com.keuangan.model.CategoryTotal;
import com.keuangan.model.IncomeExpense;
import com.keuangan.model.MonthlyTotal;
import com.keuangan.model.Transaction;

public class TransactionDAO {

    // INSERT transaksi: user_id, category_id, amount, description, transaction_date
    public boolean addTransaction(Transaction trx) {
        String sql = "INSERT INTO transactions (user_id, category_id, amount, description, transaction_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, trx.getUserId());
            ps.setInt(2, trx.getCategoryId());
            ps.setDouble(3, trx.getAmount());
            ps.setString(4, trx.getDescription());
            ps.setDate(5, trx.getTransactionDate());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error addTransaction: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteTransaction(int trxId) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, trxId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error deleteTransaction: " + e.getMessage());
            return false;
        }
    }

    public List<Transaction> getAllTransactions(int userId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT t.transaction_id, t.user_id, t.category_id, t.amount, t.description, t.transaction_date, " +
                     "       c.name AS category_name " +
                     "FROM transactions t " +
                     "LEFT JOIN categories c ON t.category_id = c.category_id " +
                     "WHERE t.user_id = ? " +
                     "ORDER BY t.transaction_date DESC, t.transaction_id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Transaction t = new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getInt("user_id"),
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getDouble("amount"),
                    rs.getString("description"),
                    rs.getDate("transaction_date")
                );
                list.add(t);
            }
        } catch(SQLException e){
            System.out.println("Error getAllTransactions: " + e.getMessage());
        }
        return list;
    }

    // Hitung total budget per category
    public int getTotalBudgetByCategoryId(int categoryId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_budget FROM budgets WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_budget");
            }
        } catch (SQLException e) {
            System.out.println("Error getTotalBudgetByCategoryId: " + e.getMessage());
        }
        return 0;
    }

    // Hitung total pengeluaran per category
    public int getTotalPengeluaranByCategoryId(int categoryId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total_trx FROM transactions WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total_trx");
            }
        } catch (SQLException e) {
            System.out.println("Error getTotalPengeluaranByCategoryId: " + e.getMessage());
        }
        return 0;
    }

    // Statistik: total pengeluaran per kategori di rentang tanggal
    public List<CategoryTotal> getExpensePerCategory(int userId, Date start, Date end) {
        List<CategoryTotal> result = new ArrayList<>();
        String sql = "SELECT c.category_id, COALESCE(c.name, 'Tanpa Kategori') AS category_name, "
                   + "       COALESCE(SUM(t.amount), 0) AS total_amount "
                   + "FROM categories c "
                   + "LEFT JOIN transactions t ON t.category_id = c.category_id "
                   + "     AND t.user_id = ? "
                   + "     AND t.transaction_date BETWEEN ? AND ? "
                   + "WHERE c.user_id = ? "
                   + "GROUP BY c.category_id, c.name "
                   + "HAVING COALESCE(SUM(t.amount), 0) >= 0 "
                   + "ORDER BY total_amount DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, start);
            ps.setDate(3, end);
            ps.setInt(4, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new CategoryTotal(
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getDouble("total_amount")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getExpensePerCategory: " + e.getMessage());
        }
        return result;
    }

    // Statistik: pemasukan vs pengeluaran (pemasukan diasumsikan amount bernilai negatif)
    public IncomeExpense getIncomeExpense(int userId, Date start, Date end) {
        String sql = "SELECT "
                   + "COALESCE(SUM(CASE WHEN amount < 0 THEN amount ELSE 0 END), 0) AS total_income, "
                   + "COALESCE(SUM(CASE WHEN amount >= 0 THEN amount ELSE 0 END), 0) AS total_expense "
                   + "FROM transactions "
                   + "WHERE user_id = ? AND transaction_date BETWEEN ? AND ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, start);
            ps.setDate(3, end);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // income dikembalikan dalam nilai positif untuk mempermudah chart
                    double income = Math.abs(rs.getDouble("total_income"));
                    double expense = rs.getDouble("total_expense");
                    return new IncomeExpense(income, expense);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getIncomeExpense: " + e.getMessage());
        }
        return new IncomeExpense(0, 0);
    }

    // Statistik: tren pengeluaran bulanan di rentang tanggal
    public List<MonthlyTotal> getMonthlyExpenseTrend(int userId, Date start, Date end) {
        List<MonthlyTotal> result = new ArrayList<>();
        String sql = "SELECT TO_CHAR(transaction_date, 'YYYY-MM') AS month_label, "
                   + "       COALESCE(SUM(amount), 0) AS total_amount "
                   + "FROM transactions "
                   + "WHERE user_id = ? AND transaction_date BETWEEN ? AND ? "
                   + "GROUP BY TO_CHAR(transaction_date, 'YYYY-MM') "
                   + "ORDER BY month_label";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, start);
            ps.setDate(3, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new MonthlyTotal(
                            rs.getString("month_label"),
                            rs.getDouble("total_amount")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getMonthlyExpenseTrend: " + e.getMessage());
        }
        return result;
    }

    // Statistik: total pengeluaran per kategori (hanya transaksi, untuk dibandingkan dengan budget)
    public Map<Integer, Double> getExpenseByCategoryMap(int userId, Date start, Date end) {
        Map<Integer, Double> result = new LinkedHashMap<>();
        String sql = "SELECT category_id, COALESCE(SUM(amount), 0) AS total_amount "
                   + "FROM transactions "
                   + "WHERE user_id = ? AND transaction_date BETWEEN ? AND ? "
                   + "GROUP BY category_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, start);
            ps.setDate(3, end);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getInt("category_id"), rs.getDouble("total_amount"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getExpenseByCategoryMap: " + e.getMessage());
        }
        return result;
    }

    // Statistik (tanpa filter tanggal): pie pengeluaran per kategori
    public List<CategoryTotal> getExpensePerCategoryAll(int userId) {
        List<CategoryTotal> result = new ArrayList<>();
        String sql = "SELECT COALESCE(c.category_id, -1) AS category_id, "
                   + "       COALESCE(c.name, 'Tanpa Kategori') AS category_name, "
                   + "       COALESCE(SUM(t.amount), 0) AS total_amount "
                   + "FROM transactions t "
                   + "LEFT JOIN categories c ON t.category_id = c.category_id "
                   + "WHERE t.user_id = ? AND t.amount > 0 "
                   + "GROUP BY c.category_id, c.name "
                   + "ORDER BY total_amount DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new CategoryTotal(
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getDouble("total_amount")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getExpensePerCategoryAll: " + e.getMessage());
        }
        return result;
    }

    // Statistik: tren pengeluaran bulanan (semua data, ascending)
    public List<MonthlyTotal> getMonthlyExpenseTrendAll(int userId) {
        List<MonthlyTotal> result = new ArrayList<>();
        String sql = "SELECT TO_CHAR(transaction_date, 'YYYY-MM') AS month_label, "
                   + "       COALESCE(SUM(amount), 0) AS total_amount "
                   + "FROM transactions "
                   + "WHERE user_id = ? AND amount > 0 "
                   + "GROUP BY TO_CHAR(transaction_date, 'YYYY-MM') "
                   + "ORDER BY month_label ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new MonthlyTotal(
                            rs.getString("month_label"),
                            rs.getDouble("total_amount")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getMonthlyExpenseTrendAll: " + e.getMessage());
        }
        return result;
    }

    // Statistik: top kategori terboros
    public List<CategoryTotal> getTopExpenseCategories(int userId, int limit) {
        List<CategoryTotal> result = new ArrayList<>();
        String sql = "SELECT COALESCE(c.category_id, -1) AS category_id, "
                   + "       COALESCE(c.name, 'Tanpa Kategori') AS category_name, "
                   + "       COALESCE(SUM(t.amount), 0) AS total_amount "
                   + "FROM transactions t "
                   + "LEFT JOIN categories c ON t.category_id = c.category_id "
                   + "WHERE t.user_id = ? AND t.amount > 0 "
                   + "GROUP BY c.category_id, c.name "
                   + "ORDER BY total_amount DESC "
                   + "LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new CategoryTotal(
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getDouble("total_amount")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getTopExpenseCategories: " + e.getMessage());
        }
        return result;
    }

    // Statistik: top transaksi bulan berjalan
    public List<Transaction> getTopTransactionsCurrentMonth(int userId, int limit) {
        List<Transaction> result = new ArrayList<>();
        String sql = "SELECT t.transaction_id, t.user_id, t.category_id, t.amount, t.description, t.transaction_date, "
                   + "       c.name AS category_name "
                   + "FROM transactions t "
                   + "LEFT JOIN categories c ON t.category_id = c.category_id "
                   + "WHERE t.user_id = ? "
                   + "  AND t.amount > 0 "
                   + "  AND t.transaction_date >= date_trunc('month', CURRENT_DATE) "
                   + "ORDER BY t.amount DESC "
                   + "LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Transaction(
                            rs.getInt("transaction_id"),
                            rs.getInt("user_id"),
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getDouble("amount"),
                            rs.getString("description"),
                            rs.getDate("transaction_date")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getTopTransactionsCurrentMonth: " + e.getMessage());
        }
        return result;
    }
}