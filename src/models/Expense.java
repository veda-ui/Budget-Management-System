package models;

import java.util.Date;

public class Expense {
    private int expenseId;
    private int userId;
    private int categoryId;
    private double expenseAmt;
    private Date dateOfExpense;
    private String description;

    
    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getExpenseAmt() {
        return expenseAmt;
    }

    public void setExpenseAmt(double expenseAmt) {
        this.expenseAmt = expenseAmt;
    }

    public Date getDateOfExpense() {
        return dateOfExpense;
    }

    public void setDateOfExpense(Date dateOfExpense) {
        this.dateOfExpense = dateOfExpense;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
