package ui;

import controllers.CategoryController;
import models.Category;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import utils.UIConstants;

public class ManageCategoriesScreen extends BaseScreen {
    private int userId;
    private JTextField categoryNameField;
    private JTextField budgetField;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private CategoryController controller;

    private JButton addButton;
    private boolean isEditing = false;
    private int editingCategoryId = -1;

    public ManageCategoriesScreen(int userId) {
        this.userId = userId;
        this.controller = new CategoryController();

        setTitle("Manage Categories");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIConstants.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Budget Categories", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        UIConstants.stylePanel(formCard);
        formCard.add(createFormPanel());

        JPanel tableCard = new JPanel(new BorderLayout());
        UIConstants.stylePanel(tableCard);
        tableCard.add(createTablePanel(), BorderLayout.CENTER);

        contentPanel.add(formCard, BorderLayout.NORTH);
        contentPanel.add(tableCard, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        loadCategories();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        UIConstants.stylePanel(panel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        categoryNameField = new JTextField(20);
        budgetField = new JTextField(10);

        JLabel nameLabel = new JLabel("Category Name");
        nameLabel.setFont(UIConstants.REGULAR_FONT);
        UIConstants.styleTextField(categoryNameField);

        JLabel budgetLabel = new JLabel("Monthly Budget");
        budgetLabel.setFont(UIConstants.REGULAR_FONT);
        UIConstants.styleTextField(budgetField);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(nameLabel, gbc);

        gbc.gridy = 1;
        panel.add(categoryNameField, gbc);

        gbc.gridy = 2;
        panel.add(budgetLabel, gbc);

        gbc.gridy = 3;
        panel.add(budgetField, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        addButton = new JButton("Add Category");
        JButton clearButton = new JButton("Clear");

        UIConstants.styleButton(addButton);
        UIConstants.styleButton(clearButton);
        addButton.setBackground(UIConstants.SUCCESS_COLOR);

        addButton.addActionListener(e -> {
            if (isEditing) {
                updateCategory();
            } else {
                saveCategory();
            }
        });
        clearButton.addActionListener(e -> clearForm());

        buttonsPanel.add(addButton);
        buttonsPanel.add(clearButton);

        gbc.gridy = 4;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(buttonsPanel, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        UIConstants.stylePanel(panel);

        String[] columns = { "Category Name", "Budget", "Actions" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setFont(UIConstants.REGULAR_FONT);
        categoryTable.getTableHeader().setFont(UIConstants.SMALL_FONT);
        categoryTable.setRowHeight(30);
        categoryTable.setShowGrid(true);
        categoryTable.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        UIConstants.styleButton(editButton);
        UIConstants.styleButton(deleteButton);
        deleteButton.setBackground(UIConstants.ERROR_COLOR);

        editButton.addActionListener(e -> editSelectedCategory());
        deleteButton.addActionListener(e -> deleteSelectedCategory());

        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadCategories() {
        tableModel.setRowCount(0);
        List<Category> categories = controller.getCategoriesForUser(userId);
        for (Category category : categories) {
            Object[] row = {
                    category.getCategoryName(),
                    String.format("$%.2f", category.getBudget()),
                    category.getCategoryId()
            };
            tableModel.addRow(row);
        }
    }

    private void saveCategory() {
        try {
            String name = categoryNameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a category name");
                return;
            }

            double budget = Double.parseDouble(budgetField.getText().trim());
            if (budget < 0) {
                JOptionPane.showMessageDialog(this, "Budget cannot be negative");
                return;
            }

            Category category = new Category();
            category.setUserId(userId);
            category.setCategoryName(name);
            category.setBudget(budget);

            CategoryController controller = new CategoryController();
            if (controller.addCategory(category) != null) {
                JOptionPane.showMessageDialog(this, "Category added successfully!");
                clearForm();
                loadCategories();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add category");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid budget amount");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category to edit");
            return;
        }

        String name = (String) tableModel.getValueAt(row, 0);
        String budgetStr = ((String) tableModel.getValueAt(row, 1)).replace("$", "").trim();
        editingCategoryId = (Integer) tableModel.getValueAt(row, 2);

        categoryNameField.setText(name);
        budgetField.setText(budgetStr);

        isEditing = true;
        addButton.setText("Update Category");
    }

    private void updateCategory() {
        try {
            if (editingCategoryId == -1) {
                JOptionPane.showMessageDialog(this, "No category selected for editing");
                return;
            }

            String name = categoryNameField.getText().trim();
            double budget = Double.parseDouble(budgetField.getText().trim());

            if (name.isEmpty() || budget < 0) {
                JOptionPane.showMessageDialog(this, "Please enter valid category name and budget");
                return;
            }

            Category category = new Category();
            category.setCategoryId(editingCategoryId);
            category.setUserId(userId);
            category.setCategoryName(name);
            category.setBudget(budget);

            if (controller.updateCategory(category)) {
                JOptionPane.showMessageDialog(this, "Category updated successfully!");
                loadCategories();
                clearForm();
                isEditing = false;
                editingCategoryId = -1;
                addButton.setText("Add Category");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update category");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid budget amount");
        }
    }

    private void deleteSelectedCategory() {
        int row = categoryTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete");
            return;
        }

        int categoryId = (Integer) tableModel.getValueAt(row, 2);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this category?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteCategory(categoryId)) {
                loadCategories();
                JOptionPane.showMessageDialog(this, "Category deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete category");
            }
        }
    }

    private void clearForm() {
        categoryNameField.setText("");
        budgetField.setText("");
        isEditing = false;
        editingCategoryId = -1;
        addButton.setText("Add Category");
    }
}
