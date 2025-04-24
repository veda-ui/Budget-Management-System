package interfaces;

import models.Income;

public interface IncomeManager {
    void loadCurrentIncome(); 

    void saveIncome(); 

    void clearForm(); 

    void updateParentDashboard(); 
}
