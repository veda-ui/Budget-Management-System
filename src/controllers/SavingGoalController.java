package controllers;

import models.SavingGoal;
import java.sql.*;
import db.DatabaseConnection;

public class SavingGoalController {
    private double getTotalSavingsAmount(int userId) {
        String query = "SELECT SUM(savings) as total FROM Income WHERE user_id = ? " +
                "AND MONTH(created_at) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(created_at) = YEAR(CURRENT_DATE())";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double total = rs.getDouble("total");
                return total > 0 ? total : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private boolean updateSavedAmount(Connection conn, int userId, double totalSavings) throws SQLException {
        String updateQuery = "UPDATE Saving_Goal SET saved_amt = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setDouble(1, totalSavings);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateSavedAmount(int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            double totalSavings = getTotalSavingsAmount(userId);
            return updateSavedAmount(conn, userId, totalSavings);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addSavingGoal(SavingGoal goal) {
        double totalSavings = getTotalSavingsAmount(goal.getUserId());
        String checkQuery = "SELECT goal_id FROM Saving_Goal WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setInt(1, goal.getUserId());
                    ResultSet rs = checkStmt.executeQuery();

                    boolean success;
                    if (rs.next()) {
                        success = updateExistingGoal(conn, goal, totalSavings);
                    } else {
                        success = insertNewGoal(conn, goal, totalSavings);
                    }

                    if (success) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateExistingGoal(Connection conn, SavingGoal goal, double totalSavings) throws SQLException {
        String updateQuery = "UPDATE Saving_Goal SET target_amt = ?, target_year = ?, " +
                "target_month = ?, target_date = ?, saved_amt = ? WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setDouble(1, goal.getTargetAmt());
            stmt.setInt(2, goal.getTargetYear());
            stmt.setInt(3, goal.getTargetMonth());
            stmt.setInt(4, goal.getTargetDate());
            stmt.setDouble(5, totalSavings);
            stmt.setInt(6, goal.getUserId());
            return stmt.executeUpdate() > 0;
        }
    }

    private boolean insertNewGoal(Connection conn, SavingGoal goal, double totalSavings) throws SQLException {
        String insertQuery = "INSERT INTO Saving_Goal (user_id, target_amt, saved_amt, " +
                "target_year, target_month, target_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setInt(1, goal.getUserId());
            stmt.setDouble(2, goal.getTargetAmt());
            stmt.setDouble(3, totalSavings);
            stmt.setInt(4, goal.getTargetYear());
            stmt.setInt(5, goal.getTargetMonth());
            stmt.setInt(6, goal.getTargetDate());
            return stmt.executeUpdate() > 0;
        }
    }

    public SavingGoal getSavingGoalByUserId(int userId) {
        String query = "SELECT * FROM Saving_Goal WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                SavingGoal goal = new SavingGoal();
                goal.setGoalId(rs.getInt("goal_id"));
                goal.setUserId(rs.getInt("user_id"));
                goal.setTargetAmt(rs.getDouble("target_amt"));
                goal.setSavedAmt(rs.getDouble("saved_amt"));
                goal.setTargetYear(rs.getInt("target_year"));
                goal.setTargetMonth(rs.getInt("target_month"));
                goal.setTargetDate(rs.getInt("target_date"));
                return goal;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateSavedAmount(int goalId, double newSavedAmt) {
        String query = "UPDATE Saving_Goal SET saved_amt = ? WHERE goal_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, newSavedAmt);
            stmt.setInt(2, goalId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSavingGoal(SavingGoal goal) {
        String query = "UPDATE Saving_Goal SET target_amt = ?, saved_amt = ?, target_year = ?, target_month = ?, target_date = ? WHERE goal_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, goal.getTargetAmt());
            stmt.setDouble(2, goal.getSavedAmt());
            stmt.setInt(3, goal.getTargetYear());
            stmt.setInt(4, goal.getTargetMonth());
            stmt.setInt(5, goal.getTargetDate());
            stmt.setInt(6, goal.getGoalId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSavingGoal(int goalId) {
        String query = "DELETE FROM Saving_Goal WHERE goal_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, goalId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deductFromSavings(int userId, double amount) {
     
        double currentSavings = getTotalSavingsAmount(userId);
        if (currentSavings < amount) {
            return false;
        }

        String query = "UPDATE Income SET savings = savings - ? " +
                "WHERE user_id = ? AND created_at = (" +
                "SELECT created_at FROM (" +
                "SELECT created_at FROM Income " +
                "WHERE user_id = ? " +
                "ORDER BY created_at DESC LIMIT 1) AS latest)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
