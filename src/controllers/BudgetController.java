package controllers;

import models.Budget;
import java.sql.*;
import db.DatabaseConnection;

public class BudgetController {
    public boolean addBudget(Budget budget) {
        String query = "INSERT INTO Categories (user_id, category_name, budget) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, budget.getUserId());
            stmt.setString(2, budget.getDescription());
            stmt.setDouble(3, budget.getTotalAmount());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBudget(Budget budget) {
        String query = "UPDATE Categories SET category_name = ?, budget = ? WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, budget.getDescription());
            stmt.setDouble(2, budget.getTotalAmount());
            stmt.setInt(3, budget.getBudgetId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBudget(int budgetId) {
        String query = "DELETE FROM Categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, budgetId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Budget getBudgetById(int budgetId) {
        String query = "SELECT * FROM Categories WHERE category_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, budgetId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Budget budget = new Budget();
                budget.setBudgetId(rs.getInt("category_id"));
                budget.setUserId(rs.getInt("user_id"));
                budget.setDescription(rs.getString("category_name"));
                budget.setTotalAmount(rs.getDouble("budget"));
                return budget;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
