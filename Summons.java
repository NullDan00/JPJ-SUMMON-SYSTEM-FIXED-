package com.mycompany.jpjsummon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// MODEL: Traffic Compound Details (Chapter 5)
public class Summons {
    private static int nextSummonsId = 1001;
    private int summonsId;
    private String vehiclePlate;
    private String offence;
    private double originalAmount;
    private double amountPaid;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String status;
    private Driver driver;
    private String issuedByOfficer;

    // Standard Constructor
    public Summons(String inputVehiclePlate, String inputOffence, double inputAmount, int dueDays, Driver assignedDriver, String officerName) {
        summonsId = nextSummonsId++;
        vehiclePlate = inputVehiclePlate;
        offence = inputOffence;
        originalAmount = inputAmount;
        amountPaid = 0.0;
        issueDate = LocalDate.now();
        dueDate = issueDate.plusDays(dueDays);
        status = "Outstanding";
        driver = assignedDriver;
        issuedByOfficer = officerName;
    }

    // Overloaded Constructor for simulating overdue records (Chapter 5)
    public Summons(String inputVehiclePlate, String inputOffence, double inputAmount, LocalDate inputIssueDate, int dueDays, Driver assignedDriver, String officerName) {
        summonsId = nextSummonsId++;
        vehiclePlate = inputVehiclePlate;
        offence = inputOffence;
        originalAmount = inputAmount;
        amountPaid = 0.0;
        issueDate = inputIssueDate;
        dueDate = issueDate.plusDays(dueDays);
        status = "Outstanding";
        driver = assignedDriver;
        issuedByOfficer = officerName;
    }

    public int getSummonsId() {
        return summonsId;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public String getOffence() {
        return offence;
    }

    public void setOriginalAmount(double newAmount) {
        originalAmount = newAmount;
    }

    // Dynamic calculation (Chapter 2 / Chapter 3)
    public double getCurrentAmount() {
        if (status.equalsIgnoreCase("Paid") || status.equalsIgnoreCase("Court Case Closed")) {
            return 0.0;
        }

        double baseAmount = originalAmount;
        // 10% late fee if overdue and not a court verdict case
        if (!status.contains("Court Verdict") && LocalDate.now().isAfter(dueDate)) {
            baseAmount = originalAmount * 1.10;
        }

        double remaining = baseAmount - amountPaid;
        if (remaining < 0.0) {
            return 0.0;
        }
        return remaining;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void makePartialPayment(double paymentAmount) {
        amountPaid += paymentAmount;
        if (getCurrentAmount() <= 0.01) {
            if (status.contains("Court Verdict")) {
                status = "Court Case Closed";
            } else {
                status = "Paid";
            }
        }
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && !status.equalsIgnoreCase("Paid") && !status.equalsIgnoreCase("Court Case Closed");
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus) {
        status = newStatus;
        if (newStatus.equalsIgnoreCase("Paid") || newStatus.equalsIgnoreCase("Court Case Closed")) {
            if (LocalDate.now().isAfter(dueDate) && !status.contains("Court Verdict")) {
                amountPaid = originalAmount * 1.10;
            } else {
                amountPaid = originalAmount;
            }
        }
    }

    public Driver getDriver() {
        return driver;
    }

    public String getFormattedDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        double currentDue = getCurrentAmount();
        String overdueAlert = isOverdue() ? "⚠️ OVERDUE (10% Late Interest Applied)\n" : "";

        return "=============================\n" +
               "       JPJ SUMMONS FILE      \n" +
               "=============================\n" +
               overdueAlert +
               "Summons ID: #" + summonsId + "\n" +
               "Vehicle Plate: " + vehiclePlate + "\n" +
               "Offence Type: " + offence + "\n" +
               "Issued By Officer: " + issuedByOfficer + "\n" +
               "Original/Court Penalty: RM" + String.format("%.2f", originalAmount) + "\n" +
               "Amount Already Paid: RM" + String.format("%.2f", amountPaid) + "\n" +
               "Current Outstanding: RM" + String.format("%.2f", currentDue) + "\n" +
               "Date Issued: " + issueDate.format(formatter) + "\n" +
               "Due Date: " + dueDate.format(formatter) + "\n" +
               "Payment Status: " + status + "\n" +
               "=============================";
    }
}
