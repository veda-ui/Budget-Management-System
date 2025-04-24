package ui;

import db.DatabaseConnection;
import controllers.SavingGoalController;
import models.SavingGoal;
import org.jfree.data.category.*;
import javax.swing.*;
import java.awt.*;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utils.*;
import exceptions.ValidationException;

public class ManageSavingGoalsScreen extends BaseScreen {
    private int userId;
    private JTextField targetAmtField;
    private JTextField targetYearField;
    private JTextField targetMonthField;
    private JTextField targetDateField;
    private JPanel chartPanel;

    public ManageSavingGoalsScreen(int userId) {
        this.userId = userId;
        setTitle("Manage Saving Goals");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        targetAmtField = new JTextField(10);
        targetYearField = new JTextField(4);
        targetMonthField = new JTextField(2);
        targetDateField = new JTextField(2);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Savings Goal Manager", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JPanel formPanel = createStyledFormPanel();
        gbc.gridy = 0;
        contentPanel.add(formPanel, gbc);

        JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
        progressPanel.setBackground(UIConstants.PANEL_BACKGROUND);
        progressPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UIConstants.PRIMARY_COLOR),
                        "Savings Progress"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(UIConstants.PANEL_BACKGROUND);
        progressPanel.add(chartPanel, BorderLayout.CENTER);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(progressPanel, gbc);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        updateSavingsChart();
        loadExistingGoal();
    }

    private JPanel createStyledFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIConstants.PANEL_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UIConstants.PRIMARY_COLOR),
                        "Set Your Goal"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        targetAmtField = createStyledTextField(targetAmtField);
        addFormRow(formPanel, "Target Amount ($):", targetAmtField, gbc, 0);

        JPanel datePanel = new JPanel(new GridLayout(1, 3, 5, 0));
        targetYearField = createStyledTextField(targetYearField);
        targetMonthField = createStyledTextField(targetMonthField);
        targetDateField = createStyledTextField(targetDateField);

        datePanel.add(createLabeledField("Year", targetYearField));
        datePanel.add(createLabeledField("Month", targetMonthField));
        datePanel.add(createLabeledField("Day", targetDateField));

        addFormRow(formPanel, "Target Date:", datePanel, gbc, 1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton("Save Goal");
        JButton deductButton = new JButton("Deduct Savings");

        UIConstants.styleButton(saveButton);
        UIConstants.styleButton(deductButton);
        saveButton.setBackground(UIConstants.SUCCESS_COLOR);

        saveButton.addActionListener(e -> saveSavingGoal(userId));
        deductButton.addActionListener(e -> deductFromSavings());

        buttonPanel.add(saveButton);
        buttonPanel.add(deductButton);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JTextField createStyledTextField(JTextField field) {
        if (field == null) {
            field = new JTextField(10);
        }
        UIConstants.styleTextField(field);
        return field;
    }

    private JPanel createLabeledField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.SMALL_FONT);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void addFormRow(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.REGULAR_FONT);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 1;
        panel.add(field, gbc);
    }

    private void updateSavingsChart() {
        try {
            SavingGoalController controller = new SavingGoalController();

            SavingGoal goal = controller.getSavingGoalByUserId(userId);
            double totalSavings = calculateTotalSavings();

            if (goal != null) {
                DefaultCategoryDataset dataset = new DefaultCategoryDataset();

                double targetAmount = goal.getTargetAmt();
                double remaining = targetAmount - totalSavings;
                remaining = Math.max(0, remaining);

                dataset.addValue(totalSavings, "Progress", "Current Savings");
                dataset.addValue(remaining, "Remaining", "Target");

                JPanel summaryPanel = new JPanel(new GridLayout(4, 1, 5, 5));
                summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                summaryPanel.add(new JLabel(String.format("Current Savings: $%.2f", totalSavings)));
                summaryPanel.add(new JLabel(String.format("Target Amount: $%.2f", targetAmount)));
                summaryPanel.add(new JLabel(String.format("Remaining Amount: $%.2f", remaining)));

                long monthsToGoal = calculateMonthsToGoal(goal);
                double monthlyTarget = monthsToGoal > 0 ? remaining / monthsToGoal : 0;
                summaryPanel.add(new JLabel(String.format("Required Monthly Savings: $%.2f", monthlyTarget)));

                chartPanel.removeAll();
                chartPanel.add(summaryPanel, BorderLayout.CENTER);
                chartPanel.revalidate();
                chartPanel.repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating savings progress: " + e.getMessage());
        }
    }

    private double calculateTotalSavings() {
        try {
            String query = "SELECT SUM(savings) as total_savings FROM Income WHERE user_id = ?"; 
                                                                                               
            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("total_savings"); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private long calculateMonthsToGoal(SavingGoal goal) {
        Calendar targetDate = Calendar.getInstance();
        targetDate.set(goal.getTargetYear(), goal.getTargetMonth() - 1, goal.getTargetDate());

        Calendar today = Calendar.getInstance();

        return ChronoUnit.MONTHS.between(
                today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1),
                targetDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().withDayOfMonth(1));
    }

    private void deductFromSavings() {
        try {
            String amountStr = JOptionPane.showInputDialog(this, "Enter amount to deduct from savings:");
            if (amountStr != null && !amountStr.trim().isEmpty()) {
                double amount = Double.parseDouble(amountStr.trim());
                if (amount <= 0) {
                    throw new IllegalArgumentException("Amount must be greater than zero");
                }

                double currentSavings = calculateTotalSavings();
                if (amount > currentSavings) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot deduct more than current savings ($" + String.format("%.2f", currentSavings) + ")");
                    return;
                }

                SavingGoalController controller = new SavingGoalController();
                if (controller.deductFromSavings(userId, amount)) {
                    JOptionPane.showMessageDialog(this,
                            String.format("$%.2f deducted successfully!", amount));
                    updateSavingsChart();
                    updateParentDashboard();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to deduct amount. Insufficient savings.");
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void saveSavingGoal(int userId) {
        try {
            double targetAmt = Double.parseDouble(targetAmtField.getText().trim());
            int targetYear = Integer.parseInt(targetYearField.getText().trim());
            int targetMonth = Integer.parseInt(targetMonthField.getText().trim());
            int targetDate = Integer.parseInt(targetDateField.getText().trim());

            if (targetAmt <= 0) {
                throw new ValidationException("Target amount must be greater than zero");
            }

            if (targetYear < Calendar.getInstance().get(Calendar.YEAR)) {
                throw new ValidationException("Target year cannot be in the past");
            }

            if (targetMonth < 1 || targetMonth > 12) {
                throw new ValidationException("Invalid month");
            }

            if (targetDate < 1 || targetDate > 31) {
                throw new ValidationException("Invalid date");
            }

            SavingGoal goal = new SavingGoal();
            goal.setUserId(userId);
            goal.setTargetAmt(targetAmt);
            goal.setTargetYear(targetYear);
            goal.setTargetMonth(targetMonth);
            goal.setTargetDate(targetDate);

            SavingGoalController controller = new SavingGoalController();
            if (controller.addSavingGoal(goal)) {
                JOptionPane.showMessageDialog(this, "Saving goal saved successfully!");
                updateSavingsChart();
                updateParentDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save saving goal");
            }
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, "Validation Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadExistingGoal() {
        try {
            SavingGoalController controller = new SavingGoalController();
            SavingGoal goal = controller.getSavingGoalByUserId(userId);
            if (goal != null) {
                targetAmtField.setText(String.format("%.2f", goal.getTargetAmt()));
                targetYearField.setText(String.valueOf(goal.getTargetYear()));
                targetMonthField.setText(String.valueOf(goal.getTargetMonth()));
                targetDateField.setText(String.valueOf(goal.getTargetDate()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateParentDashboard() {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof MainDashboard) {
                ((MainDashboard) window).refreshDashboard();
            }
        }
    }
}
