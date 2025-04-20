DROP TABLE IF EXISTS Expenditure;
DROP TABLE IF EXISTS Categories;
DROP TABLE IF EXISTS Saving_Goal;
DROP TABLE IF EXISTS Income;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email_id VARCHAR(100) UNIQUE NOT NULL,
    pincode VARCHAR(512) NOT NULL,  
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Income (
    income_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    income_amt DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    savings DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE TABLE Categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_name VARCHAR(50) NOT NULL,
    budget DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE TABLE Expenditure (
    expenditure_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    expense_amt DECIMAL(10,2) NOT NULL,
    description VARCHAR(200),
    date_of_expense DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES Categories(category_id) ON DELETE CASCADE
);

CREATE TABLE Saving_Goal (
    goal_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    target_amt DECIMAL(10,2) NOT NULL,
    saved_amt DECIMAL(10,2) DEFAULT 0.00,
    target_year INT NOT NULL,
    target_month INT NOT NULL,
    target_date INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE
);


CREATE INDEX idx_user_id ON Income(user_id);
CREATE INDEX idx_category_user ON Categories(user_id);
CREATE INDEX idx_expenditure_user ON Expenditure(user_id);
CREATE INDEX idx_expenditure_date ON Expenditure(date_of_expense);

DROP TRIGGER IF EXISTS UpdateSavingsAfterExpense;
DROP TRIGGER IF EXISTS InitializeSavings;
DROP TRIGGER IF EXISTS UpdateSavingsOnIncomeChange;

DELIMITER //


CREATE TRIGGER UpdateSavingsAfterExpense
AFTER INSERT ON Expenditure
FOR EACH ROW
BEGIN
    UPDATE Income 
    SET savings = GREATEST(0, savings - NEW.expense_amt)
    WHERE user_id = NEW.user_id;
END//

DELIMITER ;

