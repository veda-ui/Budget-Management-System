package models;

public class SavingGoal {
    private int goalId;
    private int userId;
    private double targetAmt;
    private double savedAmt;
    private int targetYear;
    private int targetMonth;
    private int targetDate;

    
    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getTargetAmt() {
        return targetAmt;
    }

    public void setTargetAmt(double targetAmt) {
        this.targetAmt = targetAmt;
    }

    public double getSavedAmt() {
        return savedAmt;
    }

    public void setSavedAmt(double savedAmt) {
        this.savedAmt = savedAmt;
    }

    public int getTargetYear() {
        return targetYear;
    }

    public void setTargetYear(int targetYear) {
        this.targetYear = targetYear;
    }

    public int getTargetMonth() {
        return targetMonth;
    }

    public void setTargetMonth(int targetMonth) {
        this.targetMonth = targetMonth;
    }

    public int getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(int targetDate) {
        this.targetDate = targetDate;
    }
}
