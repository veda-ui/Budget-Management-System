package ui;

import controllers.IncomeController;
import models.Income;
import javax.swing.*;
import java.awt.*;
import utils.UIConstants;
import interfaces.IncomeManager;

public class ManageIncomeScreen extends BaseScreen implements IncomeManager {
    private int userId;
    private JTextField incomeField;
    private JTextField savingsField;
    private JLabel currentIncomeLabel;
    private JLabel currentSavingsLabel;

    public ManageIncomeScreen(int userId) {
        this.userId = userId;

        currentIncomeLabel = new JLabel("$0.00");
        currentSavingsLabel = new JLabel("$0.00");
        incomeField = new JTextField(15);
        savingsField = new JTextField(15);

        setTitle("Manage Income");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIConstants.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Income Management", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel summaryPanel = createSummaryPanel();

        JPanel inputPanel = createInputPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        centerPanel.add(summaryPanel, BorderLayout.NORTH);
        centerPanel.add(inputPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
        loadCurrentIncome();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                updateParentDashboard();
            }
        });
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.setBackground(UIConstants.PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                UIConstants.PANEL_BORDER,
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        currentIncomeLabel.setFont(UIConstants.HEADER_FONT);
        currentSavingsLabel.setFont(UIConstants.HEADER_FONT);

        JPanel incomePanel = createInfoPanel("Current Income", currentIncomeLabel, UIConstants.SUCCESS_COLOR);
        JPanel savingsPanel = createInfoPanel("Current Savings", currentSavingsLabel, UIConstants.PRIMARY_COLOR);

        panel.add(incomePanel);
        panel.add(savingsPanel);
        return panel;
    }

    private JPanel createInfoPanel(String title, JLabel valueLabel, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(UIConstants.PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createLineBorder(color, 2));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.SMALL_FONT);
        titleLabel.setForeground(color);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        UIConstants.stylePanel(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel incomeLabel = new JLabel("New Income Amount");
        incomeLabel.setFont(UIConstants.REGULAR_FONT);
        UIConstants.styleTextField(incomeField);

        JLabel savingsLabel = new JLabel("Desired Savings");
        savingsLabel.setFont(UIConstants.REGULAR_FONT);
        UIConstants.styleTextField(savingsField);

        gbc.gridy = 0;
        panel.add(incomeLabel, gbc);
        gbc.gridy = 1;
        panel.add(incomeField, gbc);
        gbc.gridy = 2;
        panel.add(savingsLabel, gbc);
        gbc.gridy = 3;
        panel.add(savingsField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JButton saveButton = new JButton("Save Income");
        JButton clearButton = new JButton("Clear");

        UIConstants.styleButton(saveButton);
        UIConstants.styleButton(clearButton);
        saveButton.setBackground(UIConstants.SUCCESS_COLOR);

        saveButton.addActionListener(e -> saveIncome());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    @Override
    public void loadCurrentIncome() {
       
        IncomeController controller = new IncomeController();
        Income income = controller.getIncomeByUserId(userId);
        if (income != null) {
            currentIncomeLabel.setText(String.format("$%.2f", income.getIncomeAmt()));
            currentSavingsLabel.setText(String.format("$%.2f", income.getSavings()));
            incomeField.setText(String.format("%.2f", income.getIncomeAmt()));
            savingsField.setText(String.format("%.2f", income.getSavings()));
        }
    }

    @Override
    public void saveIncome() {
     
        try {
            double incomeAmt = Double.parseDouble(incomeField.getText().trim());
            double savings = Double.parseDouble(savingsField.getText().trim());

            if (savings < 0) {
                JOptionPane.showMessageDialog(this, "Savings cannot be negative!");
                return;
            }

            if (savings > incomeAmt) {
                JOptionPane.showMessageDialog(this, "Savings cannot be greater than income!");
                return;
            }

            Income income = new Income();
            income.setUserId(userId);
            income.setIncomeAmt(incomeAmt);
            income.setSavings(savings);

            IncomeController controller = new IncomeController();
            if (controller.addIncome(income)) {
                JOptionPane.showMessageDialog(this, "Income and savings updated successfully!");
                loadCurrentIncome();
                updateParentDashboard();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update income.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
        }
    }

    @Override
    public void clearForm() {
       
        incomeField.setText("");
        savingsField.setText("");
        loadCurrentIncome();
    }

    @Override
    public void updateParentDashboard() {
     
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof MainDashboard) {
                ((MainDashboard) window).refreshDashboard();
            }
        }
    }
}
