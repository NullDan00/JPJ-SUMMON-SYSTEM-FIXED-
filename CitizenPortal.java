package com.mycompany.jpjsummon;

import javax.swing.JOptionPane;

// PORTAL: Citizen self-service
public class CitizenPortal {

    //LOGIN + DASHBOARD
    public static void runCitizenPortal() {
        Driver loggedInUser = null;

        while (loggedInUser == null) {
            String icInput = JOptionPane.showInputDialog(null,
                "Enter your IC Number to sign in:\n" +
                "💡 Pro-tip: Log in with IC '010203-10-1234' to test simulated overdue compounds!",
                "Citizen Login", JOptionPane.QUESTION_MESSAGE);

            if (icInput == null || icInput.trim().isEmpty()) {
                return;
            }

            loggedInUser = Database.findDriverByIc(icInput);

            if (loggedInUser == null) {
                int registerChoice = JOptionPane.showConfirmDialog(null,
                    "No profile found with that IC.\nWould you like to register a new citizen profile?",
                    "New User Registration", JOptionPane.YES_NO_OPTION);

                if (registerChoice == JOptionPane.YES_OPTION) {
                    String nameInput = JOptionPane.showInputDialog(null, "Enter your full name:", "Registration", JOptionPane.QUESTION_MESSAGE);
                    if (nameInput != null && !nameInput.trim().isEmpty()) {
                        loggedInUser = new Driver(nameInput, icInput);
                        Database.driverList.add(loggedInUser);
                        JOptionPane.showMessageDialog(null, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }

        while (loggedInUser != null) {
            String menu = "===== 👤 MYJPJ CITIZEN PORTAL =====\n" +
                          "Logged in as: " + loggedInUser.getName() + " (" + loggedInUser.getIcNumber() + ")\n\n" +
                          "1. View Profile & Demerit Points\n" +
                          "2. View My Active Summonses\n" +
                          "3. View Specific Summons Details\n" +
                          "4. Pay Compound Fee (With Discounts / Partial Payments)\n" +
                          "5. Dispute Summons (Request Court Hearing)\n" +
                          "6. Log Out (Return to Main Gateway)\n\n" +
                          "Select Service (1-6):";

            String input = JOptionPane.showInputDialog(null, menu, "Citizen Dashboard", JOptionPane.PLAIN_MESSAGE);
            if (input == null || input.trim().equals("6")) {
                loggedInUser = null;
                break;
            }

            switch (input.trim()) {
                case "1":
                    JOptionPane.showMessageDialog(null, loggedInUser.getFormattedDetails(), "Profile Information", JOptionPane.INFORMATION_MESSAGE);
                    break;
                case "2":
                    citizenViewSummonses(loggedInUser);
                    break;
                case "3":
                    citizenViewDetails(loggedInUser);
                    break;
                case "4":
                    citizenPaySummons(loggedInUser);
                    break;
                case "5":
                    citizenDisputeSummons(loggedInUser);
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid Option!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //FEATURE 2: VIEW SUMMONSES
    private static void citizenViewSummonses(Driver citizen) {
        StringBuilder sb = new StringBuilder("===== My Traffic Summonses =====\n\n");
        boolean found = false;
        for (Summons s : Database.summonsList) {
            if (s.getDriver().getIcNumber().equalsIgnoreCase(citizen.getIcNumber())) {
                String statusText = s.getStatus();
                if (s.isOverdue()) {
                    statusText += " (🚨 Overdue - Late Fees Applied)";
                }
                sb.append(String.format("ID: #%d | Vehicle: %s | Offence: %s | Outstanding Due: RM%.2f | Status: %s\n",
                    s.getSummonsId(), s.getVehiclePlate(), s.getOffence(), s.getCurrentAmount(), statusText));
                found = true;
            }
        }
        if (!found) {
            sb.append("🎉 You have 0 outstanding traffic summonses! Drive safely.");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "My Summons History", JOptionPane.INFORMATION_MESSAGE);
    }

    //FEATURE 3: VIEW DETAILS
    private static void citizenViewDetails(Driver citizen) {
        String inputId = JOptionPane.showInputDialog(null, "Enter Summons ID to view Details:", "Lookup Details", JOptionPane.QUESTION_MESSAGE);
        if (inputId == null || inputId.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(inputId.trim());
            Summons s = Database.findSummonsById(id);
            if (s != null && s.getDriver().getIcNumber().equalsIgnoreCase(citizen.getIcNumber())) {
                JOptionPane.showMessageDialog(null, s.getFormattedDetails(), "Summons File Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Record not found or access denied.", "Query Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //FEATURE 4: PAY SUMMONS
    private static void citizenPaySummons(Driver citizen) {
        String inputId = JOptionPane.showInputDialog(null, "Enter Summons ID to make a payment:", "Payment Gateway", JOptionPane.QUESTION_MESSAGE);
        if (inputId == null || inputId.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(inputId.trim());
            Summons s = Database.findSummonsById(id);
            if (s != null && s.getDriver().getIcNumber().equalsIgnoreCase(citizen.getIcNumber())) {
                if (s.getStatus().equalsIgnoreCase("Outstanding") || s.getStatus().contains("Court Verdict")) {

                    double dynamicOutstanding = s.getCurrentAmount();

                    //DISCOUNT SELECTION
                    String discountMenu = "===== ACTIVE FESTIVE DISCOUNT CAMPAIGNS =====\n" +
                                          "Please key-in your selection [1-3]:\n\n" +
                                          "1. Hari Raya Celebration Campaign (50% Off)\n" +
                                          "2. Hari Merdeka Sale Campaign (31% Off)\n" +
                                          "3. Do Not Apply Any Festive Discount\n\n" +
                                          "Your Choice:";

                    String discountChoice = JOptionPane.showInputDialog(null, discountMenu, "Apply Discount Code", JOptionPane.QUESTION_MESSAGE);
                    if (discountChoice == null || discountChoice.trim().isEmpty()) return;

                    double discountPercent = 0.0;
                    String discountName = "No Festive Discount Applied";

                    switch (discountChoice.trim()) {
                        case "1":
                            discountPercent = 0.50;
                            discountName = "Hari Raya Celebration Campaign (50%)";
                            break;
                        case "2":
                            discountPercent = 0.31;
                            discountName = "Hari Merdeka Sale Campaign (31%)";
                            break;
                        case "3":
                            discountPercent = 0.0;
                            discountName = "None";
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Invalid input! Proceeding with zero discount.", "Promotion Skipped", JOptionPane.WARNING_MESSAGE);
                    }

                    double savings = dynamicOutstanding * discountPercent;
                    double finalAdjustedDue = dynamicOutstanding - savings;

                    //PAYMENT INPUT (PARTIAL PAYMENT)
                    String checkoutDisplay = "===== BILL STATEMENT REVIEW =====\n" +
                                             "Current Summons Outstanding: RM " + String.format("%.2f", dynamicOutstanding) + "\n" +
                                             "Campaign Promotion: " + discountName + "\n" +
                                             "Savings Applied: -RM " + String.format("%.2f", savings) + "\n" +
                                             "Total Max Payable Limit: RM " + String.format("%.2f", finalAdjustedDue) + "\n\n" +
                                             "Please enter the payment amount (RM):";

                    String payInput = JOptionPane.showInputDialog(null, checkoutDisplay, "Complete Transaction", JOptionPane.QUESTION_MESSAGE);
                    if (payInput == null || payInput.trim().isEmpty()) return;

                    try {
                        double userPayment = Double.parseDouble(payInput.trim());

                        if (userPayment <= 0) {
                            JOptionPane.showMessageDialog(null, "Transaction Cancelled: Payment must be more than RM 0.00.", "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        if (userPayment > finalAdjustedDue) {
                            JOptionPane.showMessageDialog(null, "Transaction Cancelled: You cannot pay more than the outstanding total due of RM " + String.format("%.2f", finalAdjustedDue), "Excess Amount Detected", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        int confirmTx = JOptionPane.showConfirmDialog(null,
                            "Confirm digital checkout transfer of RM " + String.format("%.2f", userPayment) + " to JPJ Malaysia?",
                            "Transaction Authorization", JOptionPane.YES_NO_OPTION);

                        if (confirmTx == JOptionPane.YES_OPTION) {
                            s.makePartialPayment(userPayment);

                            double remainingBalance = s.getCurrentAmount();
                            String successMsg = "Transaction Succeeded!\n" +
                                                "Amount Processed: RM " + String.format("%.2f", userPayment) + "\n" +
                                                "Remaining Outstanding Balance: RM " + String.format("%.2f", remainingBalance) + "\n" +
                                                "Status of Summons: " + s.getStatus().toUpperCase();

                            JOptionPane.showMessageDialog(null, successMsg, "Receipt Confirmed", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Invalid payment value inputted.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "This summons is already settled and has no outstanding balance.", "Refused", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Summons ID not found.", "Refused", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //FEATURE 5: DISPUTE SUMMONS
    private static void citizenDisputeSummons(Driver citizen) {
        String inputId = JOptionPane.showInputDialog(null, "Enter Summons ID you wish to dispute in court:", "Dispute Case", JOptionPane.QUESTION_MESSAGE);
        if (inputId == null || inputId.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(inputId.trim());
            Summons s = Database.findSummonsById(id);
            if (s != null && s.getDriver().getIcNumber().equalsIgnoreCase(citizen.getIcNumber())) {
                if (s.getStatus().equalsIgnoreCase("Outstanding")) {
                    int courtConfirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to dispute this summons? This will schedule a court trial.",
                        "Confirm Trial Request", JOptionPane.YES_NO_OPTION);

                    if (courtConfirm == JOptionPane.YES_OPTION) {
                        s.setStatus("Scheduled Court Case");
                        JOptionPane.showMessageDialog(null, "Hearing requested! Case status is now pending trial.", "Court Hearing Scheduled", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Only outstanding compounds can be disputed.", "Dispute Refused", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Summons ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
