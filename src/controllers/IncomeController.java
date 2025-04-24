package controllers;

import models.Income;
import java.sql.*;
import db.DatabaseConnection;
import java.util.Calendar;

public class IncomeController {
    public boolean addIncome(Income income) {
        String query = "INSERT INTO Income (user_id, income_amt, savings) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, income.getUserId());
            stmt.setDouble(2, income.getIncomeAmt());
            stmt.setDouble(3, income.getSavings());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addIncome(int userId, double income, double savings) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Income (user_id, income, savings, created_at) VALUES (?, ?, ?, NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setDouble(2, income);
                stmt.setDouble(3, savings);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getCurrentMonthIncome(int userId) {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);

        String query = "SELECT SUM(income_amt) as total FROM Income " +
                "WHERE user_id = ? AND MONTH(created_at) = ? AND YEAR(created_at) = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, currentMonth);
            stmt.setInt(3, currentYear);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getCurrentMonthSavings(int userId) {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);

        String query = "SELECT SUM(savings) as total FROM Income " +
                "WHERE user_id = ? AND MONTH(created_at) = ? AND YEAR(created_at) = ?";
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

    public double getTotalSavings(int userId) {
        String query = "SELECT SUM(savings) as total FROM Income WHERE user_id = ? " +
                "AND MONTH(created_at) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(created_at) = YEAR(CURRENT_DATE())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public Income getIncomeByUserId(int userId) {

        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);

        String query = "SELECT * FROM Income WHERE user_id = ? AND MONTH(created_at) = ? AND YEAR(created_at) = ? " +
                "ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, currentMonth);
            stmt.setInt(3, currentYear);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Income income = new Income();
                income.setIncomeId(rs.getInt("income_id"));
                income.setUserId(rs.getInt("user_id"));
                income.setIncomeAmt(rs.getDouble("income_amt"));
                income.setSavings(rs.getDouble("savings"));
                return income;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public double getTotalIncome(int userId) {
        String query = "SELECT SUM(income_amt) as total FROM Income WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
