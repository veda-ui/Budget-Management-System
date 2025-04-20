# Trackit - Budget Management System

A Java-based desktop application for managing personal finances, tracking expenses, and setting saving goals.

## Features

- **Income Management**
  - Track multiple income sources
  - Record regular and one-time income entries
  - View income history and statistics

- **Expense Tracking**
  - Categorize expenses
  - Record and manage daily expenses
  - Generate expense reports

- **Saving Goals**
  - Set personalized saving targets
  - Track progress towards financial goals
  - Calculate required monthly savings
  - Deduct from savings when needed

- **Dashboard**
  - Overview of financial status
  - Visual charts and statistics
  - Real-time updates

## Technology Stack

- Java SE
- Swing (GUI Framework)
- JFreeChart (for data visualization)
- Mysql
- MVC(Model-View-Controller) architecture
- Custom Exception Handling
- Properties Files for Configuration Management

## Prerequisites

- JDBC Driver
- Jcommon 
- Jfreechart

## Installation

1. Clone the repository
2. Ensure you have Java Development Kit (JDK) 11 or higher installed
3. Ensure the prerequisites are present in your lib folder
4. Import the project into your preferred IDE
5. Run the `MainApp.java` file to start the application

## Project Structure

```
flexi/
|── config/             # Config file
├── src/
│   ├── controllers/    # Business logic
│   ├── models/         # Data models
│   ├── ui/            # User interface components
│   ├── utils/         # Utility classes
│   └── db/            # Database related code
├── lib/               # External dependencies
└── database/          # SQL database file
```

## Usage

1. Launch the application
2. Create a new user account or login
3. Use the dashboard to navigate different features
4. Manage your income, expenses, and saving goals

## Architecture Overview

The application follows the MVC (Model-View-Controller) pattern:

- **Models**: Represent data structures
- **Views**: Swing GUI components for user interaction
- **Controllers**: Handle user input and update models/views


## License

This project is for educational purposes only. Not for commercial use
