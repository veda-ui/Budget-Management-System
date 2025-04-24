package ui;

import controllers.UserController;
import exceptions.ValidationException;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import utils.UIConstants;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField pincodeField;

    public LoginScreen() {
        setTitle("Login - Budget Management System");
        setSize(2000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        UIConstants.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel welcomePanel = new JPanel(new BorderLayout(0, 20));
        welcomePanel.setBackground(UIConstants.PRIMARY_COLOR);
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel iconLabel = new JLabel("💰", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel("Welcome to Trackit", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        welcomePanel.add(iconLabel, BorderLayout.NORTH);
        welcomePanel.add(titleLabel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        UIConstants.stylePanel(formPanel);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 40, 20, 40),
                UIConstants.PANEL_BORDER));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(UIConstants.SMALL_FONT);
        usernameField = new JTextField(20);
        UIConstants.styleTextField(usernameField);

        JLabel passwordLabel = new JLabel("PIN Code");
        passwordLabel.setFont(UIConstants.SMALL_FONT);
        pincodeField = new JPasswordField(20);
        UIConstants.styleTextField(pincodeField);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setOpaque(false);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        UIConstants.styleButton(loginButton);
        UIConstants.styleButton(registerButton);
        loginButton.setBackground(UIConstants.SUCCESS_COLOR);

        loginButton.addActionListener(this::handleLogin);
        registerButton.addActionListener(e -> {
            new RegisterScreen().setVisible(true);
            dispose();
        });

        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);

        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);
        gbc.gridy = 1;
        formPanel.add(usernameField, gbc);
        gbc.gridy = 2;
        formPanel.add(passwordLabel, gbc);
        gbc.gridy = 3;
        formPanel.add(pincodeField, gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(buttonsPanel, gbc);

        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String pincode = new String(pincodeField.getPassword()).trim();

        try {
            if (username.isEmpty()) {
                throw new ValidationException("Username cannot be empty");
            }
            validatePincode(pincode);

            UserController userController = new UserController();
            if (userController.authenticateUser(username, pincode)) {
                User user = userController.getUserByUsername(username);
                if (user != null) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    MainDashboard dashboard = new MainDashboard(user.getUserId());
                    dashboard.setVisible(true);
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or PIN code.");
            }
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, "Validation Error: " + ex.getMessage());
        }
    }

    private void validatePincode(String pincode) throws ValidationException {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$";
        if (!pincode.matches(passwordRegex)) {
            throw new ValidationException(
                    "PIN code must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a digit, and a special character");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
