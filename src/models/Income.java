package models;

public class Income {
    private int incomeId;
    private int userId;
    private double incomeAmt;
    private double savings;

  
    public int getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(int incomeId) {
        this.incomeId = incomeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getIncomeAmt() {
        return incomeAmt;
    }

    public void setIncomeAmt(double incomeAmt) {
        this.incomeAmt = incomeAmt;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }
}
