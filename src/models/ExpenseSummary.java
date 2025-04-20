package models;

public class ExpenseSummary {
    private String categoryName;
    private double totalAmount;

    public ExpenseSummary(String categoryName, double totalAmount) {
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
