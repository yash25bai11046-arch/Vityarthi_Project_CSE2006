# Project Title

## Employee Attendance & Automated Payroll Management System with Machine Learning Budget Forecaster

---

## Overview of the Project

The Employee Attendance & Automated Payroll Management System is a command-line application built natively in pure Core Java (JDK 17+), with no 3rd-party framework dependencies, that automates organizational payroll processes by syncing employee profiles to daily punch cards, calculating statutory progressive tax deductions and overtime multipliers, trapping bad records in custom exceptions, and forecasting future cycle expenditures using an embedded Ordinary least squares regression (OLS) linear regression model.

---

## Features

* **Employee CSV Importer/Exporter:** Streams decoupled employee records and daily shift logs from memory using `LinkedHashMap` for deterministic $O(1)$ key lookups.
* **Custom Checked Exceptions:** Traps corrupted, blank, or negative inputs in a custom payroll record parser without interrupting batch execution.
* **Progressive Marginal Tax Engine:** Calculates statutory tax withholdings across tiered brackets (5%, 10%, 15%), not flat deductions.
* **Closed-form ML Budget Forecaster:** Slope and intercept derived natively from Simple Linear Regression ($y = \beta_0 + \beta_1 x$) to estimate upcoming 22-day full-cycle payrolls.
* **HR Appraisal & Retention Board:** Analyzes shift frequency and overtime velocity to recommend top performers for bonuses (8–15%) and flag chronic absenteeism (< 18 days).
* **Dual-Stream Persistence:** Writes transactional SQLite relational tables (w/automatic in-memory fallback) alongside formatted plain-text audit reports (`payroll_audit_report.txt`).

---

## Technologies/Tools Used

* **Programming Language:** Java Standard Edition (JDK 17+)
* **Core APIs:** Standard `java.io`, `java.util`, `java.sql` Packages
* **Storage / Persistence:** SQLite JDBC Driver, In-Memory Fallback, Flat-File Text Output
* **Development & Build:** Visual Studio Code, Windows PowerShell CLI, Native `javac`

---

## Steps to Install & Run the Project

1. Clone the Repository
``` text
git clone <https://github.com/yash25bai11046-arch/vityarthi_Project_CSE2006.git>
```
``` text
cd vityarthi_Project_CSE2006
```

2. Verify Directory Structure
``` text
vityarthi_Project_CSE2006/
├── data/
│   ├── employees.csv
│   └── attendance_punches.csv
├── docs/
│   └── Project_Report_CSE2006.pdf
├── Main.java
├── README.md
├── statement.md
└── .gitignore
```

3. Compile the Source Code
``` text
javac Main.java
```

4. Execute the Application
``` text
java Main
```

## Instructions for Testing

Upon running java Main, use the interactive console menu to execute and test each functional module:

Option 1 (Load Records): Ingests CSVs from data/, verifies record integrity, initializes SQLite storage, and trains the OLS regression model.

Option 2 (Roster Summary): Reviews organization-wide metrics including total accumulated days (2,993), overtime shifts (169), gross compensation ($1,034,117.50), and net disbursements.

Option 3 (Individual Search): Lookup an individual employee ID (e.g., EMP-220 or EMP-101) to inspect itemized tax calculations and performance appraisal risk scores.

Option 4 (AI Budget Forecast): Evaluates projected payroll obligations ($1,249,021.48) for the full 22-day operating cycle against baseline payroll.

Option 5 (HR Decision Board): Reviews staff categorized into bonus appraisal candidates and critical attendance alerts (< 18 days worked).

Option 6 (Export Audit Report): Generates and exports the complete audit trail into payroll_audit_report.txt.

Option 7 (Exit): Safely exits the command-line interface.

## Screenshots

Complete terminal execution outputs, console interactive menus, and system architecture diagrams are documented in docs/Project_Report_CSE2006.pdf.
