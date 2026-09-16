import java.io.*;
import java.sql.*;
import java.util.*;

class InvalidPayrollRecordException extends Exception {
    public InvalidPayrollRecordException(String message) {
        super(message);
    }
}
class Employee {
    private final String id;
    private final String name;
    private final String department;
    private final double dailyRate;
    public Employee(String id, String name, String department, double dailyRate) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.dailyRate = dailyRate;
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public double getDailyRate() { return dailyRate; }
}
class PerformanceEvaluator {
    public static int computeRiskScore(int days, int ot) {
        if (days < 18 && ot == 0) return 3;
        if (days < 20) return 2;
        return 0;
    }
    public static String resolveActionPlan(int days, int ot) {
        if (days < 18 && ot == 0)
            return "CRITICAL: Issue 7-day notice with Warning.";
        if (days < 20 && ot > 0)
            return "MODERATE: Schedule a performance review; address leave frequency.";
        if (days < 20)
            return "WARNING: Send written attendance reminder; monitor daily punch logs.";
        return "GOOD: Meets operational requirements; maintain normal workflow.";
    }
    public static boolean isEligibleForBonus(int days, int ot) {
        return days >= 22 && ot >= 1;
    }
    public static String resolveAppraisalRemark(int days, int ot) {
        if (days >= 22 && ot >= 3) return "ELIGIBLE FOR 15% APPRAISAL + ANNUAL PERFORMANCE BONUS";
        if (days >= 22 && ot >= 1) return "ELIGIBLE FOR 8% APPRAISAL";
        return "STANDARD TRACK";
    }
}
class PayrollSlip {
    private final Employee employee;
    private final int daysPresent, overtimeDays;
    private final double grossSalary, taxDeduction, netSalary;
    public PayrollSlip(Employee employee, int daysPresent, int overtimeDays) {
        this.employee = employee;
        this.daysPresent = daysPresent;
        this.overtimeDays = overtimeDays;
        double rate = employee.getDailyRate();
        grossSalary = (daysPresent * rate) + (overtimeDays * rate * 1.5);
        if (grossSalary > 7000) taxDeduction = grossSalary * 0.15;
        else if (grossSalary > 5000) taxDeduction = grossSalary * 0.10;
        else taxDeduction = grossSalary * 0.05;
        netSalary = grossSalary - taxDeduction;
    }
    public Employee getEmployee() { return employee; }
    public int getDaysPresent() { return daysPresent; }
    public int getOvertimeDays() { return overtimeDays; }
    public double getGrossSalary() { return grossSalary; }
    public double getTaxDeduction() { return taxDeduction; }
    public double getNetSalary() { return netSalary; }
    public int getRiskScore() { return PerformanceEvaluator.computeRiskScore(daysPresent, overtimeDays); }
    public String getActionPlan() { return PerformanceEvaluator.resolveActionPlan(daysPresent, overtimeDays); }
    public boolean isAppraisalCandidate() { return PerformanceEvaluator.isEligibleForBonus(daysPresent, overtimeDays); }
    public String getAppraisalRemark() { return PerformanceEvaluator.resolveAppraisalRemark(daysPresent, overtimeDays); }
}
class PayrollPredictor {
    private double b0, b1;
    private boolean trained = false;
    public void train(List<PayrollSlip> data) {
        if (data.isEmpty()) return;
        double sumX = 0, sumY = 0;
        for (PayrollSlip s : data) {
            sumX += s.getDaysPresent();
            sumY += s.getGrossSalary();
        }
        double meanX = sumX / data.size(), meanY = sumY / data.size();
        double top = 0, bottom = 0;
        for (PayrollSlip s : data) {
            double dx = s.getDaysPresent() - meanX;
            top += dx * (s.getGrossSalary() - meanY);
            bottom += dx * dx;
        }
        b1 = (bottom != 0) ? top / bottom : 0;
        b0 = meanY - (b1 * meanX);
        trained = true;
    }
    public double predictSalaryForDays(int days) {
        return trained ? b0 + (b1 * days) : 0.0;
    }
    public boolean isTrained() { return trained; }
}
class CsvDataService {
    private static List<String[]> readRows(String path, String what, int columns)
            throws InvalidPayrollRecordException {
        File file = new File(path);
        if (!file.exists()) throw new InvalidPayrollRecordException(what + " file missing at: " + path);
        List<String[]> rows = new ArrayList<>();
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                br.readLine();
                String line; 
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("EmpID")) continue;

                    String[] parts = line.split(",");
                    if (parts.length < columns)
                        throw new InvalidPayrollRecordException("Corrupt " + what + " record: " + line);
                    rows.add(parts);
                }
            }
        } catch (IOException e) {
            throw new InvalidPayrollRecordException("Error reading " + what + ": " + e.getMessage());
        }
        return rows;
    }
    private static double toNumber(String text) throws InvalidPayrollRecordException {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidPayrollRecordException("Invalid number in record: " + text.trim());
        }
    }
    public static Map<String, Employee> readEmployees(String path) throws InvalidPayrollRecordException {
        Map<String, Employee> roster = new LinkedHashMap<>();
        for (String[] p : readRows(path, "Employees", 4)) {
            String id = p[0].trim();
            roster.put(id, new Employee(id, p[1].trim(), p[2].trim(), toNumber(p[3])));
        }
        return roster;
    }
    public static List<PayrollSlip> processAttendance(String path, Map<String, Employee> roster)
            throws InvalidPayrollRecordException {

        List<PayrollSlip> slips = new ArrayList<>();
        for (String[] p : readRows(path, "Attendance", 3)) {
            String id = p[0].trim();
            int days = (int) toNumber(p[1]), ot = (int) toNumber(p[2]);

            if (days < 0 || ot < 0)
                throw new InvalidPayrollRecordException("Negative attendance found for " + id);

            Employee emp = roster.get(id);
            if (emp != null) slips.add(new PayrollSlip(emp, days, ot));
        }
        return slips;
    }
}
class ReportExportService {
    public static void exportReport(List<PayrollSlip> slips, String outPath) {
        try {
            try (PrintWriter pw = new PrintWriter(new FileWriter(outPath))) {
            pw.println(Main.LINE);
            pw.println("                 OFFICIAL MONTHLY PAYROLL REPORT OF EMPLOYEES");
            pw.println(Main.LINE);
            pw.printf("%-8s | %-22s | %-18s | %-10s | %-10s | %-10s%n",
                    "EMP ID", "NAME", "DEPARTMENT", "GROSS ($)", "TAX ($)", "NET ($)");
            pw.println(Main.DASH);
            for (PayrollSlip s : slips)
                pw.printf("%-8s | %-22s | %-18s | %-10.2f | %-10.2f | %-10.2f%n",
                        s.getEmployee().getId(), s.getEmployee().getName(), s.getEmployee().getDepartment(),
                        s.getGrossSalary(), s.getTaxDeduction(), s.getNetSalary());
            pw.println(Main.LINE);
            }
            System.out.println("[Success] Report saved to: " + outPath);
        } catch (IOException e) {
            System.out.println("[Error] Failed to write report: " + e.getMessage());
        }
    }
}
public class Main {
    static final String LINE = "==========================================================================================";
    static final String DASH = "------------------------------------------------------------------------------------------";
    private static final String DB_URL = "jdbc:sqlite:payroll_system.db";
    private static final int MONTH_DAYS = 22;
    private static final String ROW = "%-8s | %-22s | %-18s | %-6s | %-4s | %-40s%n";
        private static final String MENU = """
            ============================================================
             EMPLOYEE ATTENDANCE & SALARY MANAGEMENT SYSTEM
            ============================================================
            1. Load Employees List & Attendance Records from CSV
            2. View Monthly Salary & Total Running Days of Company
            3. Search Individual Employee Pay Slip & Performance Card
            4. Company Salary Funds & Budget Forecast (AI)
            5. Staff Appraisal Recommendation & Critical Action Board
            6. Export Salary List to Text File
            7. Exit the System
            ============================================================""";
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Map<String, Employee> roster = new LinkedHashMap<>();
        List<PayrollSlip> slips = new ArrayList<>();
        PayrollPredictor predictor = new PayrollPredictor();
        boolean dataLoaded = false;
        initDB();
        while (true) {
            System.out.println(MENU);
            System.out.print("Choose an option (1-7): ");
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[Error] Please enter a valid number (1 to 7).");
                continue;
            }
// options 2-6 need the data first
            if (choice >= 2 && choice <= 6 && (!dataLoaded || (choice == 4 && !predictor.isTrained()))) {
                System.out.println("[Notice] Please load records using Option 1 first.");
                continue;
            }
            switch (choice) {
                case 1 -> {
                    try {
                        roster = CsvDataService.readEmployees("data/employees.csv");
                        slips = CsvDataService.processAttendance("data/attendance_punches.csv", roster);
                        predictor.train(slips);
                        dataLoaded = true;

                        System.out.println("\n[Success] Ingested " + roster.size() + " enterprise employee profiles.");
                        System.out.println("[Success] Calculated payroll & trained budget forecasting engine.");
                        saveRecordsToDB(slips);
                    } catch (InvalidPayrollRecordException e) {
                        System.out.println("[Error] " + e.getMessage());
                    }
                }
                case 2 ->
                    showSalaryRoster(slips);
                case 3 -> {
                    System.out.print("Enter Employee ID (e.g., EMP-XXX): ");
                    String id = sc.nextLine().trim();
                    PayrollSlip found = null;
                    for (PayrollSlip s : slips)
                        if (s.getEmployee().getId().equalsIgnoreCase(id)) { found = s; break; }

                    if (found == null) System.out.println("[Error] Employee ID not found.");
                    else showPerformanceCard(found);
                }
                case 4 ->
                    showBudgetForecast(slips, predictor);
                case 5 ->
                    showAppraisalBoard(slips);
                case 6 ->
                    ReportExportService.exportReport(slips, "payroll_audit_report.txt");
                case 7 -> {
                    System.out.println("Exiting Payroll System. Goodbye!");
                    sc.close();
                    return;
                }
                default ->
                    System.out.println("[Error] Choice out of range (1 to 7).");
            }
        }
    }
    private static void showSalaryRoster(List<PayrollSlip> slips) {
        System.out.printf("\n%-8s | %-22s | %-18s | %-6s | %-4s | %-10s | %-10s%n",
                "EMP ID", "NAME", "DEPT", "DAYS", "OT", "GROSS ($)", "NET ($)");
        System.out.println(DASH);
        int totalDays = 0, totalOt = 0;
        double totalGross = 0, totalNet = 0;
        for (PayrollSlip s : slips) {
            totalDays += s.getDaysPresent();
            totalOt += s.getOvertimeDays();
            totalGross += s.getGrossSalary();
            totalNet += s.getNetSalary();
            System.out.printf("%-8s | %-22s | %-18s | %-6d | %-4d | $%-9.2f | $%-9.2f%n",
                    s.getEmployee().getId(), s.getEmployee().getName(), s.getEmployee().getDepartment(),
                    s.getDaysPresent(), s.getOvertimeDays(), s.getGrossSalary(), s.getNetSalary());
        }
        System.out.println(LINE);
        System.out.println("            COMPANY MONTHLY OPERATIONAL DAYS AND SALARY SUMMARY REPORT");
        System.out.println(LINE);
        System.out.println("Standard Working Days in Month : " + MONTH_DAYS + " days");
        System.out.println("Total Employee Count           : " + slips.size() + " members");
        System.out.println("Total Days Worked Across Staff : " + totalDays + " person-days");
        System.out.println("Total Extra Overtime Shifts    : " + totalOt + " shifts");
        System.out.printf("Total Company Gross Payable    : $%.2f%n", totalGross);
        System.out.printf("Total Net Salary Disbursed     : $%.2f%n", totalNet);
        System.out.println(LINE);
    }
    private static void showPerformanceCard(PayrollSlip s) {
        int r = s.getRiskScore();
        String risk = (r == 3) ? " [CRITICAL ATTRITION]" : (r == 2) ? " [ATTENTION REQUIRED]" : " [STABLE]";
        System.out.println("\n========================================================");
        System.out.println("               INDIVIDUAL PERFORMANCE CARD              ");
        System.out.println("========================================================");
        System.out.println("Employee ID       : " + s.getEmployee().getId());
        System.out.println("Name              : " + s.getEmployee().getName());
        System.out.println("Department        : " + s.getEmployee().getDepartment());
        System.out.println("Days Worked       : " + s.getDaysPresent() + " / " + MONTH_DAYS + " days");
        System.out.println("Overtime Shifts   : " + s.getOvertimeDays() + " extra days");
        System.out.println("--------------------------------------------------------");
        System.out.printf("Gross Remuneration: $%.2f%n", s.getGrossSalary());
        System.out.printf("Tax Deduction     : $%.2f%n", s.getTaxDeduction());
        System.out.printf("Net Disbursed     : $%.2f%n", s.getNetSalary());
        System.out.println("--------------------------------------------------------");
        System.out.println("Risk Factor Level : Grade " + r + risk);
        System.out.println("Actionable Plan   : " + s.getActionPlan());
        System.out.println("Appraisal Status  : " + s.getAppraisalRemark());
        System.out.println("========================================================");
    }
    private static void showBudgetForecast(List<PayrollSlip> slips, PayrollPredictor predictor) {
        double gross = 0, net = 0;
        for (PayrollSlip s : slips) {
            gross += s.getGrossSalary();
            net += s.getNetSalary();
        }
        double forecast = slips.size() * predictor.predictSalaryForDays(MONTH_DAYS);
        System.out.println("\n" + LINE);
        System.out.println("                 COMPANY SALARY FUNDS & AI BUDGET FORECAST");
        System.out.println(LINE);
        System.out.printf("Total Current Outflow (Gross Budget)   : $%.2f%n", gross);
        System.out.printf("Total Net Salary Disbursed to Staff    : $%.2f%n", net);
        System.out.printf("AI Forecasted Budget (Full 22-Day)     : $%.2f%n", forecast);
        System.out.printf("Estimated Budget Variance / Surplus    : $%.2f%n", forecast - gross);
        System.out.println(LINE);
    }
    private static void showAppraisalBoard(List<PayrollSlip> slips) {
        System.out.println("\n" + LINE);
        System.out.println("        STAFF APPRAISAL RECOMMENDATIONS & CRITICAL PERFORMANCE BOARD");
        System.out.println(LINE);
        System.out.println("TOP PERFORMERS RECOMMENDED FOR APPRAISAL & ANNUAL BONUS:");
        int good = printBoard(slips, true, "APPRAISAL / INCREMENT INCENTIVE");
        System.out.println("Total Candidates Recommended for Appraisal: " + good + " employees.");
        System.out.println(DASH);
        System.out.println("CRITICAL PERFORMANCE / TERMINATION CANDIDATES (ATTENDANCE < 18 DAYS & ZERO OVERTIME):");
        int bad = printBoard(slips, false, "MANDATORY ACTION PLAN");
        System.out.println("Total Staff Flagged for Immediate HR Action: " + bad + " employees.");
        System.out.println(LINE);
    }
    private static int printBoard(List<PayrollSlip> slips, boolean topPerformers, String lastColumn) {
        System.out.printf(ROW, "EMP ID", "NAME", "DEPT", "DAYS", "OT", lastColumn);
        System.out.println(DASH);
        int count = 0;
        for (PayrollSlip s : slips) {
            boolean show = topPerformers ? s.isAppraisalCandidate() : (s.getRiskScore() == 3);
            if (!show) continue;
            count++;
            System.out.printf(ROW, s.getEmployee().getId(), s.getEmployee().getName(),
                    s.getEmployee().getDepartment(), s.getDaysPresent(), s.getOvertimeDays(),
                    topPerformers ? s.getAppraisalRemark() : s.getActionPlan());
        }
        return count;
    }
    private static void initDB() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS payroll_records ("
                    + "emp_id TEXT, gross_salary REAL, net_salary REAL, risk_grade INT)");
        } catch (SQLException e) {
            System.out.println("[Database] Error occurred while initializing the database.");
        }
    }
    private static void saveRecordsToDB(List<PayrollSlip> slips) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO payroll_records VALUES (?,?,?,?)")) {

            for (PayrollSlip s : slips) {
                ps.setString(1, s.getEmployee().getId());
                ps.setDouble(2, s.getGrossSalary());
                ps.setDouble(3, s.getNetSalary());
                ps.setInt(4, s.getRiskScore());
                ps.executeUpdate();
            }
            System.out.println("[Database] Processed " + slips.size() + " records saved to SQLite.");
        } catch (SQLException e) {
            System.out.println("[Database] Offline persistence mode active.");
        }
    }
}