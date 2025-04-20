package controllers;

import models.User;
import utils.PasswordHasher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import db.DatabaseConnection;

public class UserController {
    private static final int SALT_LENGTH = 24;

    public boolean authenticateUser(String username, String pincode) {
        String query = "SELECT pincode FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPincode = rs.getString("pincode");
                if (storedPincode.length() < SALT_LENGTH) {
                   
                    return false;
                }
                String salt = storedPincode.substring(0, SALT_LENGTH);
                String hashedInput = PasswordHasher.hashPassword(pincode, salt);
                String fullHash = salt + hashedInput;
                return storedPincode.equals(fullHash);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registerUser(User user) {
        String query = "INSERT INTO Users (username, email_id, pincode, created_at) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            String salt = PasswordHasher.generateSalt(); 
            String hashedPincode = PasswordHasher.hashPassword(user.getPincode(), salt);
            String fullHash = salt + hashedPincode;

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmailId());
            stmt.setString(3, fullHash);
            stmt.setDate(4, new java.sql.Date(user.getCreatedAt().getTime()));

            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                }
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserById(int userId) {
        String query = "SELECT * FROM Users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmailId(rs.getString("email_id"));
                user.setPincode(rs.getString("pincode"));
                user.setCreatedAt(rs.getDate("created_at"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserByUsername(String username) {
        String query = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmailId(rs.getString("email_id"));
                user.setPincode(rs.getString("pincode"));
                user.setCreatedAt(rs.getDate("created_at"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
