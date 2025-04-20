package ui;

import java.text.DateFormatSymbols;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;
import models.ExpenseSummary;
import controllers.*;
import models.*;
import javax.swing.*;
import java.awt.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import java.sql.*;
import utils.UIConstants;
import db.DatabaseConnection;

public class ReportsScreen extends BaseScreen {
    private int userId;
    private JTabbedPane tabbedPane;

    public ReportsScreen(int userId) {
        this.userId = userId;
        setTitle("Financial Reports");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIConstants.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Financial Reports & Analytics", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.REGULAR_FONT);
        tabbedPane.setBackground(UIConstants.PANEL_BACKGROUND);

        tabbedPane.addTab("Overview", createOverviewPanel());
        tabbedPane.addTab("Expenses by Category", createExpensePieChart());
        tabbedPane.addTab("Monthly Summary", createMonthlySummary());
        tabbedPane.addTab("Budget Analysis", createBudgetComparison());
        tabbedPane.addTab("Savings Progress", createSavingsProgressPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ExpenseController expenseController = new ExpenseController();
        IncomeController incomeController = new IncomeController();
        CategoryController categoryController = new CategoryController();

        double totalExpenses = expenseController.getTotalExpensesByUserId(userId);
        double totalSavings = incomeController.getTotalSavings(userId);

        double totalBudget = 0;
        List<Category> categories = categoryController.getCategoriesForUser(userId);
        for (Category cat : categories) {
            totalBudget += cat.getBudget();
        }

        double utilization = totalBudget > 0 ? (totalExpenses / totalBudget) * 100 : 0;
        String progress = utilization <= 75 ? "On Track" : utilization <= 90 ? "Warning" : "Over Budget";

        panel.add(createSummaryCard("Total Expenses", String.format("$%.2f", totalExpenses), UIConstants.ERROR_COLOR));
        panel.add(createSummaryCard("Total Savings", String.format("$%.2f", totalSavings), UIConstants.SUCCESS_COLOR));
        panel.add(createSummaryCard("Budget Utilization", String.format("%.1f%%", utilization),
                UIConstants.PRIMARY_COLOR));
        panel.add(createSummaryCard("Monthly Progress", progress, UIConstants.SECONDARY_COLOR));

        return panel;
    }

    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UIConstants.PANEL_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIConstants.SMALL_FONT);
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(UIConstants.HEADER_FONT);
        valueLabel.setForeground(new Color(51, 51, 51));

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);

        return card;
    }

    private JPanel createExpensePieChart() {
        JPanel panel = new JPanel(new BorderLayout());
        try {
            DefaultPieDataset dataset = new DefaultPieDataset();
            List<ExpenseSummary> expenses = getExpenseSummary();

            for (ExpenseSummary expense : expenses) {
                dataset.setValue(expense.getCategoryName(), expense.getTotalAmount());
            }

            JFreeChart chart = ChartFactory.createPieChart(
                    "Expenses by Category",
                    dataset,
                    true,
                    true,
                    false);

            chart.setBackgroundPaint(Color.white);
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(Color.white);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(600, 400));
            panel.add(chartPanel, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(new JLabel("Error creating chart: " + e.getMessage()));
        }
        return panel;
    }

    private List<ExpenseSummary> getExpenseSummary() {
        List<ExpenseSummary> expenses = new ArrayList<>();
        String query = "SELECT c.category_name, SUM(e.expense_amt) as total " +
                "FROM Expenditure e " +
                "JOIN Categories c ON e.category_id = c.category_id " +
                "WHERE e.user_id = ? " +
                "GROUP BY c.category_id, c.category_name";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(new ExpenseSummary(
                            rs.getString("category_name"),
                            rs.getDouble("total")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    private JPanel createMonthlySummary() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = { "Month", "Total Expenses", "Budget", "Variance" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        try {
            String query = "SELECT MONTH(e.date_of_expense) as month, " +
                    "SUM(e.expense_amt) as total, " +
                    "SUM(c.budget) as budget " +
                    "FROM Expenditure e " +
                    "JOIN Categories c ON e.category_id = c.category_id " +
                    "WHERE e.user_id = ? " +
                    "GROUP BY MONTH(e.date_of_expense)";

            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        double total = rs.getDouble("total");
                        double budget = rs.getDouble("budget");
                        model.addRow(new Object[] {
                                getMonthName(rs.getInt("month")),
                                String.format("$%.2f", total),
                                String.format("$%.2f", budget),
                                String.format("$%.2f", budget - total)
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    private JPanel createBudgetComparison() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            String query = "SELECT c.category_name, c.budget, " +
                    "COALESCE(SUM(e.expense_amt), 0) as actual " +
                    "FROM Categories c " +
                    "LEFT JOIN Expenditure e ON c.category_id = e.category_id " +
                    "WHERE c.user_id = ? " +
                    "GROUP BY c.category_id, c.category_name, c.budget";

            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String category = rs.getString("category_name");
                    double budget = rs.getDouble("budget");
                    double actual = rs.getDouble("actual");

                    dataset.addValue(budget, "Budget", category);
                    dataset.addValue(actual, "Actual", category);
                }
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Budget vs Actual Expenses",
                    "Categories",
                    "Amount ($)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);

            chart.setBackgroundPaint(Color.white);
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(Color.white);
            plot.setRangeGridlinePaint(Color.gray);

            ChartPanel chartPanel = new ChartPanel(chart);
            panel.add(chartPanel, BorderLayout.CENTER);

        } catch (Exception e) {
            e.printStackTrace();
            panel.add(new JLabel("Error creating budget comparison chart: " + e.getMessage()));
        }

        return panel;
    }

    private JPanel createSavingsProgressPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            SavingGoalController goalController = new SavingGoalController();
            IncomeController incomeController = new IncomeController();

            SavingGoal goal = goalController.getSavingGoalByUserId(userId);
            double totalSavings = incomeController.getTotalSavings(userId);

            if (goal != null) {
                dataset.addValue(totalSavings, "Current", "Savings");
                dataset.addValue(goal.getTargetAmt() - totalSavings, "Remaining", "Target");

                JFreeChart chart = ChartFactory.createBarChart(
                        "Savings Progress",
                        "",
                        "Amount ($)",
                        dataset,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false);

                ChartPanel chartPanel = new ChartPanel(chart);
                panel.add(chartPanel, BorderLayout.CENTER);

                JPanel summaryPanel = new JPanel(new GridLayout(3, 1));
                summaryPanel.add(new JLabel(String.format("Target Amount: $%.2f", goal.getTargetAmt())));
                summaryPanel.add(new JLabel(String.format("Current Savings: $%.2f", totalSavings)));
                summaryPanel.add(new JLabel(String.format("Remaining: $%.2f", goal.getTargetAmt() - totalSavings)));

                panel.add(summaryPanel, BorderLayout.SOUTH);
            } else {
                panel.add(new JLabel("No savings goal set"), BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
            panel.add(new JLabel("Error creating savings progress chart: " + e.getMessage()));
        }

        return panel;
    }
}
