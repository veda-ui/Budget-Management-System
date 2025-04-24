package controllers;

import models.Expense;
import java.sql.*;
import db.DatabaseConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseController {
    public boolean addExpense(Expense expense) {
        String query = "INSERT INTO Expenditure (user_id, category_id, expense_amt, description, date_of_expense) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, expense.getUserId());
                stmt.setInt(2, expense.getCategoryId());
                stmt.setDouble(3, expense.getExpenseAmt());
                stmt.setString(4, expense.getDescription());
                stmt.setDate(5, new java.sql.Date(expense.getDateOfExpense().getTime()));

                int result = stmt.executeUpdate();
                if (result > 0) {
                    conn.commit();
                    return true;
                }
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateExpense(Expense expense) {
        String query = "UPDATE Expenditure SET category_id = ?, expense_amt = ?, description = ?, date_of_expense = ? WHERE expenditure_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, expense.getCategoryId());
            stmt.setDouble(2, expense.getExpenseAmt());
            stmt.setString(3, expense.getDescription());
            stmt.setDate(4, new java.sql.Date(expense.getDateOfExpense().getTime()));
            stmt.setInt(5, expense.getExpenseId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteExpense(int expenseId) {
        String query = "DELETE FROM Expenditure WHERE expenditure_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, expenseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Expense> getExpensesByUserId(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT * FROM Expenditure WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Expense expense = new Expense();
                    expense.setExpenseId(rs.getInt("expenditure_id"));
                    expense.setUserId(rs.getInt("user_id"));
                    expense.setCategoryId(rs.getInt("category_id"));
                    expense.setExpenseAmt(rs.getDouble("expense_amt"));
                    expense.setDescription(rs.getString("description"));
                    expense.setDateOfExpense(rs.getDate("date_of_expense"));
                    expenses.add(expense);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public ResultSet getExpensesByCategoryId(int categoryId) {
        String query = "SELECT * FROM Expenditure WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getTotalExpensesByUserId(int userId) {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);

        String query = "SELECT SUM(expense_amt) as total FROM Expenditure " +
                "WHERE user_id = ? AND MONTH(date_of_expense) = ? AND YEAR(date_of_expense) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, currentMonth);
            stmt.setInt(3, currentYear);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Double total = rs.getDouble("total");
                return total != null ? total : 0.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public ResultSet getExpensesByCategory(int userId) {
        String query = "SELECT c.category_name, SUM(e.expense_amt) as total " +
                "FROM Expenditure e " +
                "JOIN Categories c ON e.category_id = c.category_id " +
                "WHERE e.user_id = ? " +
                "GROUP BY c.category_id";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getMonthlyExpenses(int userId) {
        String query = "SELECT MONTH(date_of_expense) as month, SUM(expense_amt) as total " +
                "FROM Expenditure " +
                "WHERE user_id = ? " +
                "GROUP BY MONTH(date_of_expense)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getTotalExpensesByCategory(int categoryId) {
        String query = "SELECT SUM(expense_amt) as total FROM Expenditure WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public ResultSet getCategoryExpenseSummary(int categoryId) {
        String query = "SELECT e.*, c.category_name, c.budget " +
                "FROM Expenditure e " +
                "JOIN Categories c ON e.category_id = c.category_id " +
                "WHERE e.category_id = ? " +
                "ORDER BY e.date_of_expense DESC";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, categoryId);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
