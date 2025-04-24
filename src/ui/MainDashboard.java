package ui;

import controllers.*;
import models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import utils.UIConstants;

public class MainDashboard extends BaseScreen {
    private int userId;

    private JLabel welcomeLabel;
    private JLabel incomeLabel, expenseLabel, savingsLabel;
    private JLabel disposableLabel;

    public MainDashboard(int userId) {
        this.userId = userId;
        setTitle("Budget Management System - Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeComponents();
    }

    private String getUserName() {
        UserController userController = new UserController();
        User user = userController.getUserById(userId);
        return user != null ? user.getUsername() : "User";
    }

    private void initializeComponents() {
        UIConstants.styleFrame(this);

        incomeLabel = new JLabel("$0.00", SwingConstants.RIGHT);
        expenseLabel = new JLabel("$0.00", SwingConstants.RIGHT);
        savingsLabel = new JLabel("$0.00", SwingConstants.RIGHT);
        disposableLabel = new JLabel("$0.00", SwingConstants.RIGHT);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel summaryCardsPanel = createSummaryCardsPanel();
        contentPanel.add(summaryCardsPanel, BorderLayout.NORTH);

        JPanel quickActionsPanel = createQuickActionsPanel();
        contentPanel.add(quickActionsPanel, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel sidePanel = createSidePanel();
        mainPanel.add(sidePanel, BorderLayout.WEST);

        add(mainPanel);
        updateSummary();
    }

    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Quick Actions"));

        JButton addIncomeBtn = createActionButton("Add Income", "Add your monthly income", e -> openIncomeManager());
        JButton addExpenseBtn = createActionButton("Add Expense", "Record new expense", e -> openExpenseTracker());
        JButton setBudgetBtn = createActionButton("Set Budget", "Set category budgets", e -> openCategoryManager());
        JButton viewReportsBtn = createActionButton("View Reports", "See your financial summary", e -> openReports());

        panel.add(addIncomeBtn);
        panel.add(addExpenseBtn);
        panel.add(setBudgetBtn);
        panel.add(viewReportsBtn);

        return panel;
    }

    private JButton createActionButton(String title, String tooltip, ActionListener action) {
        JButton button = new JButton(title);
        button.setToolTipText(tooltip);
        button.addActionListener(action);
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        welcomeLabel = new JLabel("Welcome back, " + getUserName() + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(UIConstants.TITLE_FONT);
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createSummaryCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);

        incomeLabel = new JLabel("$0.00", SwingConstants.CENTER);
        expenseLabel = new JLabel("$0.00", SwingConstants.CENTER);
        savingsLabel = new JLabel("$0.00", SwingConstants.CENTER);
        disposableLabel = new JLabel("$0.00", SwingConstants.CENTER);

        panel.add(createSummaryCard("Monthly Income", incomeLabel, UIConstants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Total Expenses", expenseLabel, UIConstants.ERROR_COLOR));
        panel.add(createSummaryCard("Current Savings", savingsLabel, UIConstants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Disposable", disposableLabel, UIConstants.SECONDARY_COLOR));

        return panel;
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.PANEL_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.SMALL_FONT);
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(UIConstants.HEADER_FONT);
        valueLabel.setForeground(new Color(51, 51, 51));
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);

        return card;
    }

    private void updateSummary() {
        SwingUtilities.invokeLater(() -> {
            try {
                IncomeController incomeController = new IncomeController();
                ExpenseController expenseController = new ExpenseController();

                // Get current month's values
                double currentMonthIncome = incomeController.getCurrentMonthIncome(userId);
                double currentMonthSavings = incomeController.getCurrentMonthSavings(userId);
                double currentMonthExpenses = expenseController.getTotalExpensesByUserId(userId);

                // Calculate disposable income (Income - Expenses - Savings)
                // Ensure savings are not reduced by expenses
                double disposable = Math.max(0, currentMonthIncome - currentMonthExpenses - currentMonthSavings);

                // Update labels
                incomeLabel.setText(String.format("$%.2f", currentMonthIncome));
                savingsLabel.setText(String.format("$%.2f", currentMonthSavings));
                expenseLabel.setText(String.format("$%.2f", currentMonthExpenses));
                disposableLabel.setText(String.format("$%.2f", disposable));

                revalidate();
                repaint();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            updateSummary();
        }
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel(new GridLayout(6, 1, 10, 10));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.setPreferredSize(new Dimension(200, 0));
        sidePanel.setBackground(new Color(41, 128, 185));

        addNavigationButton(sidePanel, "Overview", e -> refreshDashboard());
        addNavigationButton(sidePanel, "Income", e -> openIncomeManager());
        addNavigationButton(sidePanel, "Categories", e -> openCategoryManager());
        addNavigationButton(sidePanel, "Expenses", e -> openExpenseTracker());
        addNavigationButton(sidePanel, "Goals", e -> openSavingGoals());
        addNavigationButton(sidePanel, "Logout", e -> logout());

        return sidePanel;
    }

    private void addNavigationButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.addActionListener(listener);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
        panel.add(button);
    }

    public void refreshDashboard() {
        SwingUtilities.invokeLater(() -> {
            updateSummary();
            revalidate();
            repaint();
        });
    }

    private void openIncomeManager() {
        ManageIncomeScreen screen = new ManageIncomeScreen(userId);
        screen.setVisible(true);
    }

    private void openCategoryManager() {
        ManageCategoriesScreen screen = new ManageCategoriesScreen(userId);
        screen.setVisible(true);
    }

    private void openExpenseTracker() {
        ExpenseTrackerScreen tracker = new ExpenseTrackerScreen(userId);
        tracker.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                refreshDashboard();
            }
        });
        tracker.setVisible(true);
    }

    private void openSavingGoals() {
        ManageSavingGoalsScreen screen = new ManageSavingGoalsScreen(userId);
        screen.setVisible(true);
    }

    private void openReports() {
        ReportsScreen screen = new ReportsScreen(userId);
        screen.setVisible(true);
    }

    private void logout() {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setVisible(true);
        dispose();
    }
}
