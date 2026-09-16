# Employment Attendance & Automated Payroll Management System

An automated standalone command-line enterprise application developed in Core Java to process monthly staff attendance, enforce progressive payroll tax policies, perform machine learning budget forecast and generate actionable employee retention matrices.

---

## Project Overview

In enterprise facilities and shift based operational environments manual attendance reconciliation and disparate payroll spreadsheets often lead to calculation errors, compliance disputes and unexpected budget liabilities due to unmonitored overtime.

This project provides a robust zero dependency Java solution to:

1. Ingest and validate multi file employee rosters and monthly shift punch logs (150 enterprise records).
2. Compute gross earnings, statutory overtime multipliers (1.5x) and progressive multi-tier tax brackets.
3. Implement closed form Simple Linear Regression from scratch to forecast upcoming organizational salary budgets.
4. Categorize employees into an HR Appraisal & Retention Risk Board (recommending performance bonuses or replacement interventions).
5. Log audit data in SQLite database storage via JDBC and export verifiable text audit reports.

---

## Key Features

- Data Ingestion & Custom Exceptions: Ingests 150 enterprise profiles over two CSV files (`employees.csv`, `attendance_punches.csv`) with complete validation for corrupted entries and negative attendance.
- Progressive Compensation Engine: Computes regular pay, overtime premiums (1.5x daily rate), progressive tax deductions (5%, 10%, 15%) and net payouts.
- Supervised ML Budget Forecaster: Implements closed form Ordinary Least Squares (OLS) Simple Linear Regression ($y = \beta_0 + \beta_1 x$) to statistically project full cycle monthly budget liabilities.
- Appraisal & Retention Matrix: Highlight top performers eligible for 8-15% bonus increments and flag at risk staff (< 18 days attendance and zero overtime) with mandatory HR action plans.
- Relational Persistence & File Export: Synchronize processed slips with parameterized SQLite JDBC statements and export a persistent audit report (`payroll_audit_report.txt`).

---

## Technical Stack & Modular Architecture

- Language: Java (JDK 17 or higher recommended)
- Database: SQLite (via JDBC `java.sql.`) with an offline memory fallback
- File I/O: Java Stream I/O (`BufferedReader`, `FileReader`, `PrintWriter`, `FileWriter`)
- Machine Learning: Pure-Java Closed-Form Simple Linear Regression ($y = \beta_0 + \beta_1 x$)
- Architecture: 8 Modular, Single-Responsibility Classes:
- `Employee`: Domain model encapsulating employee metadata.
- `PayrollSlip`: Computes gross compensation, progressive taxes and net disbursements.
- `PerformanceEvaluator`: Assesses performance tiers, bonus eligibility and risk scores.
- `PayrollPredictor`: Pure-Java machine learning regression engine.
- `InvalidPayrollRecordException`: Custom checked exception handling corrupt or invalid data.
- `CsvDataService`: Ingestion and validation service for CSV datasets.
- `ReportExportService`: Textual audit report generation engine.
- `Main`: Interactive Command-Line Interface (CLI) driver.

---

## Directory Structure

```text
.
├── data/
│  ├── attendance_punches.csv  # 150-record shift punch & overtime logs
│  └── employees.csv      # 150-record employee master registry
├── .gitignore          # Excludes JVM bytecode (.class) and local DB
├── Main.java          # Complete source code (8 modular classes)
├── README.md          # Environment setup, execution & verification guide
└── statement.md         # Scope, problem statement, and target users
```

---

## Environment Setup & Prerequisites

### 1. Check Java Installation

Ensure your system has the Java Development Kit (JDK 17+) and is accessible in your system PATH:

```text
javac -version
java -version
```

### 2. Clone the Repository

```text
git clone https://github.com/yash25bai11046-arch/Vityarthi_Project_CSE2006.git
cd Vityarthi_Project_CSE2006
```

---

## Build & Execution Instructions

This project does not require any external build tools (Maven/Gradle) or third party dependencies.

### 1. Compilation

From the project root directory, compile `Main.java`:

```text
javac Main.java
```

Note: This will compile into 8 distinct `.class` files for each modular class.

### 2. Running the Application

Launch the terminal interface:

```text
java Main
```

---

## Verification & Testing Guide

Once launched, navigate through the interactive menu options:

- Option 1 (Load Records): Ingests all 150 employee profiles and shift punches from the `data/` folder which automatically trains the Linear Regression model and synchronize records with the storage layer.
- Option 2 (Salary Roster & Metrics): Displays the complete 150 employee payroll table and aggregated operational metrics (Total Gross Payable, Total Net Disbursed, Total Days Worked and Total Overtime Shifts).
- Option 3 (Search Employee): Query specific IDs (e.g., `EMP-XXX`) to inspect detailed individual paycards, performance grades and custom action plans.
- Option 4 (AI Budget Forecast): Evaluates total company gross/net funds and uses regression trendlines to forecast the upcoming 22 day operational cycle budget liability.
- Option 5 (Appraisal & Critical Retention): Displays top performers recommended for 8-15% bonus increments and critical attrition candidates (< 18 days attendance) with mandatory HR intervention steps.
- Option 6 (Export Audit Report): Generate `payroll_audit_report.txt` in the root directory for auditing.
- Option 7 (Exit): Closes the program cleanly.

---

## Documentation

- Detailed report available in docs/Project_Report_CSE2006.pdf.
- Formal statement available in statement.md.
