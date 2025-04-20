package ui;

import controllers.*;
import models.*;
import utils.UIConstants;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.KeyEvent;

public class ExpenseTrackerScreen extends BaseScreen {
    private int userId;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JComboBox<Category> categoryCombo;
    private JTextField amountField;
    private JTextField descriptionField;
    private JLabel budgetLabel;
    private List<Integer> expenseIds;
    private JPanel mainPanel;

    public ExpenseTrackerScreen(int userId) {
        this.userId = userId;
        this.expenseIds = new ArrayList<>();

        setTitle("Expense Tracker");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        UIConstants.styleFrame(this);

        initializeComponents();
        loadCategories();
        loadExpenses();
        setupValidation();
    }

    private void initializeComponents() {
        mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        JPanel summaryCards = new JPanel(new GridLayout(1, 3, 15, 0));
        summaryCards.setName("summaryCards");
        summaryCards.setOpaque(false);
        summaryCards.add(createSummaryCard("Total Expenses", "$0.00", UIConstants.ERROR_COLOR));
        summaryCards.add(createSummaryCard("Monthly Budget", "$0.00", UIConstants.SUCCESS_COLOR));
        summaryCards.add(createSummaryCard("Remaining", "$0.00", UIConstants.PRIMARY_COLOR));

        topPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        topPanel.add(summaryCards, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        contentPanel.add(createInputPanel(), BorderLayout.NORTH);
        contentPanel.add(createTablePanel(), BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
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

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Expense Tracker", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        UIConstants.stylePanel(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        categoryCombo = new JComboBox<>();
        amountField = new JTextField(15);
        descriptionField = new JTextField(20);
        budgetLabel = new JLabel("Budget Status", SwingConstants.CENTER);

        UIConstants.styleTextField(amountField);
        UIConstants.styleTextField(descriptionField);

        addFormComponent(panel, "Category:", categoryCombo, gbc, 0);
        addFormComponent(panel, "Amount:", amountField, gbc, 1);
        addFormComponent(panel, "Description:", descriptionField, gbc, 2);

        JButton addButton = new JButton("Add Expense");
        UIConstants.styleButton(addButton);
        addButton.addActionListener(e -> addExpense());

        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(addButton, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UIConstants.stylePanel(panel);

        String[] columns = { "Date", "Category", "Amount", "Description" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        expenseTable = new JTable(tableModel);
        expenseTable.setFont(UIConstants.REGULAR_FONT);
        expenseTable.getTableHeader().setFont(UIConstants.SMALL_FONT);

        JScrollPane scrollPane = new JScrollPane(expenseTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        UIConstants.styleButton(editButton);
        UIConstants.styleButton(deleteButton);
        deleteButton.setBackground(UIConstants.ERROR_COLOR);

        editButton.addActionListener(e -> editSelectedExpense());
        deleteButton.addActionListener(e -> deleteSelectedExpense());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addFormComponent(JPanel panel, String label, JComponent component,
            GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(component, gbc);
    }

    private void setupValidation() {
        amountField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE
                        && c != KeyEvent.VK_DELETE) {
                    evt.consume();
                }
                if (c == '.' && amountField.getText().contains(".")) {
                    evt.consume();
                }
            }
        });

        descriptionField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (descriptionField.getText().length() >= 200) {
                    evt.consume();
                }
            }
        });
    }

    private void loadCategories() {
        try {
            CategoryController controller = new CategoryController();
            List<Category> categories = controller.getCategoriesForUser(userId);
            categoryCombo.removeAllItems();

            for (Category category : categories) {
                categoryCombo.addItem(category);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadExpenses() {
        try {
            ExpenseController controller = new ExpenseController();
            CategoryController categoryController = new CategoryController();
            List<Expense> expenses = controller.getExpensesByUserId(userId);
            tableModel.setRowCount(0);
            expenseIds.clear();

            for (Expense expense : expenses) {
                Category category = categoryController.getCategoryById(expense.getCategoryId());
                String categoryName = category != null ? category.getCategoryName() : "Unknown";
                Object[] row = {
                        expense.getDateOfExpense(),
                        categoryName,
                        String.format("$%.2f", expense.getExpenseAmt()),
                        expense.getDescription() != null ? expense.getDescription() : ""
                };
                tableModel.addRow(row);
                expenseIds.add(expense.getExpenseId());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading expenses: " + e.getMessage());
            e.printStackTrace();
        }
        updateSummaryCards();
    }

    private void addExpense() {
        try {
            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            if (selectedCategory == null) {
                JOptionPane.showMessageDialog(this, "Please select a category");
                return;
            }

            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount");
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountText.replace(",", ""));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount (numbers only)");
                return;
            }

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero");
                return;
            }

            Expense expense = new Expense();
            expense.setUserId(userId);
            expense.setCategoryId(selectedCategory.getCategoryId());
            expense.setExpenseAmt(amount);
            expense.setDescription(descriptionField.getText().trim());
            expense.setDateOfExpense(new Date());

            ExpenseController controller = new ExpenseController();
            if (controller.addExpense(expense)) {
                JOptionPane.showMessageDialog(this, "Expense added successfully!");
                loadExpenses();
                updateAvailableBudget(selectedCategory.getCategoryName(), budgetLabel);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add expense");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        updateSummaryCards();
    }

    private void editSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to edit");
            return;
        }

        try {
            String categoryName = (String) tableModel.getValueAt(selectedRow, 1);
            String amountStr = ((String) tableModel.getValueAt(selectedRow, 2)).replace("$", "").trim();
            String description = (String) tableModel.getValueAt(selectedRow, 3);

            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                Category cat = (Category) categoryCombo.getItemAt(i);
                if (cat.getCategoryName().equals(categoryName)) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }
            amountField.setText(amountStr);
            descriptionField.setText(description);

            JPanel editPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            editPanel.add(new JLabel("Category:"));
            editPanel.add(categoryCombo);
            editPanel.add(new JLabel("Amount:"));
            editPanel.add(amountField);
            editPanel.add(new JLabel("Description:"));
            editPanel.add(descriptionField);

            int option = JOptionPane.showConfirmDialog(
                    this,
                    editPanel,
                    "Edit Expense",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                updateExpense(selectedRow);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error parsing expense data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateExpense(int selectedRow) {
        try {
            int expenseId = expenseIds.get(selectedRow);
            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            if (selectedCategory == null) {
                throw new Exception("Please select a category");
            }

            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) {
                    throw new Exception("Amount must be greater than zero");
                }
            } catch (NumberFormatException e) {
                throw new Exception("Please enter a valid amount");
            }

            String description = descriptionField.getText().trim();

            Expense expense = new Expense();
            expense.setExpenseId(expenseId);
            expense.setUserId(userId);
            expense.setCategoryId(selectedCategory.getCategoryId());
            expense.setExpenseAmt(amount);
            expense.setDescription(description);
            expense.setDateOfExpense(new Date());

            ExpenseController controller = new ExpenseController();
            if (controller.updateExpense(expense)) {
                JOptionPane.showMessageDialog(this, "Expense updated successfully!");
                loadExpenses();
                clearForm();
                updateParentDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update expense");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteSelectedExpense() {
        int selectedRow = expenseTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this expense?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int expenseId = expenseIds.get(selectedRow);
                ExpenseController controller = new ExpenseController();
                if (controller.deleteExpense(expenseId)) {
                    JOptionPane.showMessageDialog(this, "Expense deleted successfully!");
                    loadExpenses();
                    updateParentDashboard();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete expense");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting expense: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void clearForm() {
        categoryCombo.setSelectedIndex(-1);
        amountField.setText("");
        descriptionField.setText("");
    }

    private void updateAvailableBudget(String categoryName, JLabel budgetLabel) {
        if (categoryName.trim().isEmpty()) {
            budgetLabel.setText("Available Budget: $0.00");
            return;
        }

        try {
            CategoryController categoryController = new CategoryController();
            ExpenseController expenseController = new ExpenseController();
            List<Category> categories = categoryController.getCategoriesForUser(userId);

            for (Category category : categories) {
                if (category.getCategoryName().equalsIgnoreCase(categoryName)) {
                    double spent = expenseController.getTotalExpensesByCategory(category.getCategoryId());
                    budgetLabel.setText(String.format("Total Spent: $%.2f", spent));
                    return;
                }
            }
        } catch (Exception e) {
            budgetLabel.setText("Error loading budget");
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

    private void updateSummaryCards() {
        ExpenseController expenseController = new ExpenseController();
        CategoryController categoryController = new CategoryController();

        double totalExpenses = expenseController.getTotalExpensesByUserId(userId);
        double totalBudget = 0;
        List<Category> categories = categoryController.getCategoriesForUser(userId);
        for (Category cat : categories) {
            totalBudget += cat.getBudget();
        }
        double remaining = totalBudget - totalExpenses;

        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JPanel && "summaryCards".equals(subComp.getName())) {
                        JPanel summaryCards = (JPanel) subComp;

                        updateSummaryCardValue(summaryCards, 0, String.format("$%.2f", totalExpenses));
                        updateSummaryCardValue(summaryCards, 1, String.format("$%.2f", totalBudget));
                        updateSummaryCardValue(summaryCards, 2, String.format("$%.2f", remaining));
                    }
                }
            }
        }
    }

    private void updateSummaryCardValue(JPanel summaryCards, int cardIndex, String value) {
        Component card = summaryCards.getComponent(cardIndex);
        if (card instanceof JPanel) {
            Component[] cardComponents = ((JPanel) card).getComponents();
            for (Component comp : cardComponents) {
                if (comp instanceof JLabel && ((JLabel) comp).getFont().equals(UIConstants.HEADER_FONT)) {
                    ((JLabel) comp).setText(value);
                    break;
                }
            }
        }
    }
}