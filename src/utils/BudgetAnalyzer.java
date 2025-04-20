package utils;

import models.*;
import controllers.*;
import java.util.*;

public class BudgetAnalyzer {
    public static Map<String, Double> calculateBudgetVariance(int userId) {
        Map<String, Double> variance = new HashMap<>();
        CategoryController categoryController = new CategoryController();
        ExpenseController expenseController = new ExpenseController();

        try {
            List<Category> categories = categoryController.getCategoriesForUser(userId);
            for (Category category : categories) {
                String categoryName = category.getCategoryName();
                double budget = category.getBudget();
                double actual = expenseController.getTotalExpensesByCategory(category.getCategoryId());
                variance.put(categoryName, budget - actual);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return variance;
    }

    public static boolean isOverBudget(int userId) {
        try {
            IncomeController incomeController = new IncomeController();
            ExpenseController expenseController = new ExpenseController();

            Income income = incomeController.getIncomeByUserId(userId);
            if (income == null || income.getIncomeAmt() == 0) {
                return false; 
            }

            double totalExpenses = expenseController.getTotalExpensesByUserId(userId);
            double totalBudget = calculateTotalBudget(userId);

            return totalExpenses > totalBudget || totalExpenses > income.getIncomeAmt();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static double calculateTotalBudget(int userId) {
        CategoryController categoryController = new CategoryController();
        List<Category> categories = categoryController.getCategoriesForUser(userId);
        return categories.stream()
                .mapToDouble(Category::getBudget)
                .sum();
    }

    public static double getSavingsRate(int userId) {
        try {
            IncomeController incomeController = new IncomeController();
            Income income = incomeController.getIncomeByUserId(userId);
            if (income != null && income.getIncomeAmt() > 0) {
                double savingsAmount = income.getSavings();
                double incomeAmount = income.getIncomeAmt();
                return (savingsAmount / incomeAmount) * 100.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static Map<String, Double> getBudgetSummary(int userId) {
        Map<String, Double> summary = new HashMap<>();
        try {
            CategoryController categoryController = new CategoryController();
            ExpenseController expenseController = new ExpenseController();
            List<Category> categories = categoryController.getCategoriesForUser(userId);

            double totalBudget = 0;
            double totalExpenses = 0;

            for (Category category : categories) {
                totalBudget += category.getBudget();
                totalExpenses += expenseController.getTotalExpensesByCategory(category.getCategoryId());
            }

            summary.put("totalBudget", totalBudget);
            summary.put("totalExpenses", totalExpenses);
            summary.put("remaining", totalBudget - totalExpenses);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return summary;
    }
}
