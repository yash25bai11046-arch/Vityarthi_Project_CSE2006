# Problem Statement

Enterprise payroll, manufacturing, and shift-management environments relying on manual ledgers or disassociated spreadsheets encounter several systemic operational vulnerabilities:

* **Reconciliation Latency & Clerical Errors:** Delay in reconciliation and clerical errors during manual verification of employee master records against physical or biometric punched cards.
* **Statutory Compliance & Legal Risks:** Miscalculations of statutory progressive marginal tax brackets or overtime multipliers resulting in compliance and legal risks.
* **Unbudgeted Overtime Creep:** Unbudgeted overtime increases due to unmonitored velocity of shifts which spike end-of-disbursement liabilities.
* **Absence of Predictive Analytical Models:** Absence of predictive models to statistically forecast future liabilities given current attendance patterns.
* **Reactive Workforce Retention Management:** Inadequate systematic retention management to monitor chronic absences for operational impact mitigation and reward high-achievers.

---

## Scope of the Project

The proposed project implements a standalone high-throughput command-line enterprise platform written purely in Core Java (JDK 17+) to address the pain points without external third-party frameworks. The work scope includes:

* Processing and reconciliation of 150 enterprise records from separated CSV files (`employees.csv` and `attendance_punches.csv`).
* Defensive batch data parsing with custom checked exceptions to avoid run-time termination on identified corrupt records.
* Calculation of progressive income tax withholdings (5%, 10%, and 15% multipliers).
* Statutory overtime multiplier application (1.5x regular pay rate).
* Forecasting of full 22-day operational cycle liabilities using in-engine closed-form Ordinary Least Squares Linear Regression.
* Categorization into an automated HR Appraisal & Retention Decision Board based on attendance velocity and overtime metrics.
* Relational database storage using parameterized SQLite JDBC transactions with offline fallback and audit reporting in text format (`payroll_audit_report.txt`).

---

## Target Users

The target users of this enterprise payroll solution include:

* **Payroll Officers / Financial Clerks:** To operate the automated monthly payroll calculations, ensure statutory tax withholdings compliance, and export audit trails for verification.
* **Human Resources (HR):** To review workforce retention boards, monitor chronic absentees, and recommend bonuses for high-achievers.
* **Operations / Department Managers:** To monitor departmental attendance trends, track cumulative overtime hours, and manage shift allocations.
* **Corporate Financial Controllers / Executives:** To leverage predictive Linear Regression forecasting capabilities for next-cycle labor expenditure planning.

---

## High-Level Features

Key capabilities / features:

* **Decoupled Multi-Source CSV Ingestion:** Rapid loading and synchronization of independent employee and punch data sources using `LinkedHashMap` for constant-time $O(1)$ retrieval.
* **Custom Exception Framework:** Parsing defensive layer that captures missing fields, malformed numbers, and negative hours to error logs without job termination.
* **Progressive Multi-Bracket Tax Engine:** Marginal tax withholding calculation engine that supports statutory compliance across multiple salary thresholds.
* **Native OLS Linear Regression Forecaster:** Embedded mathematical model ($y = \beta_0 + \beta_1 x$), estimating slope and intercept parameters, to predict budget requirements without requiring ML libraries.
* **HR Decision & Appraisal Matrix:** Automated categorization engine that flags employees for bonus consideration (8–15%) or HR warning notices.
* **Dual-Mode Persistence & Export:** Simultaneous write-operations to an ACID-compliant relational SQLite database (`payroll_records` table) and formatted text file for auditing.
