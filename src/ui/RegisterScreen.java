package ui;

import controllers.UserController;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Date;
import utils.UIConstants;

public class RegisterScreen extends BaseScreen {
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField pincodeField;

    public RegisterScreen() {
        setTitle("Register - Budget Management System");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIConstants.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel formPanel = new JPanel(new GridBagLayout());
        UIConstants.stylePanel(formPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        usernameField = createStyledField("Username");
        emailField = createStyledField("Email");
        pincodeField = createStyledField("PIN Code");

        addFormField(formPanel, "Username", usernameField, gbc, 0);
        addFormField(formPanel, "Email", emailField, gbc, 2);
        addFormField(formPanel, "PIN Code", pincodeField, gbc, 4);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back to Login");

        UIConstants.styleButton(registerButton);
        UIConstants.styleButton(backButton);
        registerButton.setBackground(UIConstants.SUCCESS_COLOR);

        registerButton.addActionListener(e -> handleRegister(e));
        backButton.addActionListener(e -> {
            new LoginScreen().setVisible(true);
            dispose();
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        centerPanel.add(formPanel, BorderLayout.CENTER);
        centerPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JTextField createStyledField(String placeholder) {
        JTextField field = new JTextField(20);
        UIConstants.styleTextField(field);
        return field;
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        JLabel lblField = new JLabel(label);
        lblField.setFont(UIConstants.REGULAR_FONT);
        panel.add(lblField, gbc);

        gbc.gridy = row + 1;
        panel.add(field, gbc);
    }

    private void handleRegister(ActionEvent e) {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pincode = pincodeField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || pincode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmailId(email);
        user.setPincode(pincode);
        user.setCreatedAt(new Date());

        UserController userController = new UserController();
        if (userController.registerUser(user)) {
            JOptionPane.showMessageDialog(this, "Registration successful!");
            new LoginScreen().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
        }
    }
}
