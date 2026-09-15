# Problem Statement & Scope

## 1. Problem Statement

Herewith, in the context of enterprise management, manually marking attendance and calculating payroll figures introduce administrative errors, regulatory tax withholding discrepancies, and unplanned overtime expenditure. Furthermore, companies often face challenges with recognizing chronic absences in a timely manner and identifying their most productive workers for performance appraisal.

## 2. Scope of the Project

This Employment Attendance & Automated Payroll Management System offers a stand-alone terminal solution that parses and validates multiple-filed employee rosters and monthly attendance records, calculates wages, overtime pay (1.5x multiplier), and income tax (5,10,15%), forecast organizational salary budget expenditure with a simple linear regression model ($y = \beta_0 + \beta_1 x$), segment employees for appraisal and retention, and store the relevant information in an SQL relational database while also providing audit trail reports in text format. Notably, the system is designed to process up to 150 enterprise-grade records.

## 3. End-Users

This project is designed for human resource managers, payroll and finance officers, and operational supervisors.

## 4. Key High-Level Features

- CSV Data Ingestion Engine with custom checked exception handling (`InvalidPayrollRecordException`)
- Progressive Multi-Tier Income Tax & Overtime Calculation Engine
- Supervised Machine Learning Budget Forecaster (Ordinary Least Squares Linear Regression)
- Dual-Track HR Appraisal & Retention Decision Support Board
- Relational Database Persistence (JDBC SQLite with offline fallback)
- Audit Trail Text Report Generator
