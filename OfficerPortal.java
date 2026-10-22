package com.mycompany.jpjsummon;

import javax.swing.JOptionPane;

//PORTAL: JPJ Officer
public class OfficerPortal {

    //AUTHENTICATION
    public static void handleOfficerAccess() {
        while (true) {
            String officerAccessMenu = "===== 👮 JPJ OFFICER GATEWAY =====\n" +
                                       "1. Sign In with Existing Account\n" +
                                       "2. Register New Officer Account\n" +
                                       "3. Back to Main Gateway\n\n" +
                                       "Select Option (1-3):";

            String choice = JOptionPane.showInputDialog(null, officerAccessMenu, "Officer Authentication", JOptionPane.PLAIN_MESSAGE);

            if (choice == null || choice.trim().equals("3")) {
                break;
            }

            switch (choice.trim()) {
                case "1":
                    Officer loggedInOfficer = signInOfficer();
                    if (loggedInOfficer != null) {
                        runOfficerPortal(loggedInOfficer);
                    }
                    break;
                case "2":
                    registerNewOfficer();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid selection! Enter 1, 2, or 3.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void registerNewOfficer() {
        String name = JOptionPane.showInputDialog(null, "Enter Officer Full Name:", "Officer Registration", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;

        String badge = JOptionPane.showInputDialog(null, "Enter Official Badge/Staff ID (e.g., JPJ-5542):", "Officer Registration", JOptionPane.QUESTION_MESSAGE);
        if (badge == null || badge.trim().isEmpty()) return;

        String user = JOptionPane.showInputDialog(null, "Choose a Username:", "Officer Registration", JOptionPane.QUESTION_MESSAGE);
        if (user == null || user.trim().isEmpty()) return;

        if (Database.findOfficerByUsername(user.trim()) != null) {
            JOptionPane.showMessageDialog(null, "This username is already taken. Please choose another one.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String pass = JOptionPane.showInputDialog(null, "Choose a Password:", "Officer Registration", JOptionPane.QUESTION_MESSAGE);
        if (pass == null || pass.trim().isEmpty()) return;

        Officer newOfficer = new Officer(name.trim(), badge.trim(), user.trim(), pass.trim());
        Database.officerList.add(newOfficer);

        JOptionPane.showMessageDialog(null,
            "Registration Successful!\nWelcome Officer " + name + " (Badge: " + badge + ").\nYou can now sign in.",
            "Account Created", JOptionPane.INFORMATION_MESSAGE);
    }

    private static Officer signInOfficer() {
        String username = JOptionPane.showInputDialog(null, "Enter Officer Username:\n(Default test user: 'jpjofficer')", "Officer Sign In", JOptionPane.QUESTION_MESSAGE);
        if (username == null) return null;

        String password = JOptionPane.showInputDialog(null, "Enter Officer Password:\n(Default test pass: 'jpj123')", "Officer Sign In", JOptionPane.QUESTION_MESSAGE);
        if (password == null) return null;

        Officer officer = Database.findOfficerByUsername(username.trim());

        if (officer != null && officer.verifyPassword(password.trim())) {
            JOptionPane.showMessageDialog(null,
                "Access Granted!\nWelcome, Officer " + officer.getOfficerName() + " (" + officer.getBadgeId() + ")",
                "Authentication Approved", JOptionPane.INFORMATION_MESSAGE);
            return officer;
        } else {
            JOptionPane.showMessageDialog(null, "Invalid username or password. Access Denied.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    //DASHBOARD
    private static void runOfficerPortal(Officer activeOfficer) {
        while (true) {
            String menu = "===== 👮 OFFICER DASHBOARD =====\n" +
                          "Officer On Duty: " + activeOfficer.getOfficerName() + " | Badge: " + activeOfficer.getBadgeId() + "\n\n" +
                          "1. Issue New Summons to Citizen\n" +
                          "2. View All Global Summonses\n" +
                          "3. View All Registered Drivers\n" +
                          "4. Review Court Hearing Verdicts\n" +
                          "5. Log Out (Return to Officer Gateway)\n\n" +
                          "Select Service (1-5):";

            String input = JOptionPane.showInputDialog(null, menu, "Officer Controls", JOptionPane.PLAIN_MESSAGE);
            if (input == null || input.trim().equals("5")) {
                break;
            }

            switch (input.trim()) {
                case "1":
                    officerIssueSummons(activeOfficer);
                    break;
                case "2":
                    officerViewAllSummonses();
                    break;
                case "3":
                    officerViewAllDrivers();
                    break;
                case "4":
                    officerRecordCourtAttendance();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //FEATURE 1: ISSUE SUMMONS
    private static void officerIssueSummons(Officer activeOfficer) {
        String name = JOptionPane.showInputDialog(null, "Enter offender's full name:", "Issue Summons", JOptionPane.QUESTION_MESSAGE);
        if (name == null || name.trim().isEmpty()) return;

        String ic = JOptionPane.showInputDialog(null, "Enter offender's IC Number:", "Issue Summons", JOptionPane.QUESTION_MESSAGE);
        if (ic == null || ic.trim().isEmpty()) return;

        Driver driver = Database.findOrCreateDriver(ic, name);

        String plate = JOptionPane.showInputDialog(null, "Enter Vehicle Number Plate:", "Issue Summons", JOptionPane.QUESTION_MESSAGE);
        if (plate == null || plate.trim().isEmpty()) return;

        String offence = JOptionPane.showInputDialog(null, "Enter offence (e.g., Speeding, Running Red Light):", "Issue Summons", JOptionPane.QUESTION_MESSAGE);
        if (offence == null || offence.trim().isEmpty()) return;

        double amount = 300.0;
        String amountInput = JOptionPane.showInputDialog(null, "Enter Penalty Fee (RM):", "300.00");
        if (amountInput != null && !amountInput.trim().isEmpty()) {
            try {
                amount = Double.parseDouble(amountInput); // Chapter 2 Conversions
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid amount. Defaulting to RM 300.00.", "Format Warning", JOptionPane.WARNING_MESSAGE);
            }
        }

        Summons newSummons = new Summons(plate, offence, amount, 14, driver, activeOfficer.getOfficerName());
        Database.summonsList.add(newSummons);
        driver.addDemeritPoints(10);

        JOptionPane.showMessageDialog(null,
            "Summons Successfully Issued!\n" +
            "Summons ID: #" + newSummons.getSummonsId() + "\n" +
            "Offender: " + driver.getName() + " (" + driver.getIcNumber() + ")\n" +
            "Issued By: " + activeOfficer.getOfficerName() + " (" + activeOfficer.getBadgeId() + ")\n" +
            "10 Demerit Points applied to offender's license.",
            "Summons Dispatched", JOptionPane.INFORMATION_MESSAGE);
    }

    //FEATURE 2: VIEW ALL SUMMONSES
    private static void officerViewAllSummonses() {
        if (Database.summonsList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Database empty. No summons records exist.", "System Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder("===== Global Summons Records =====\n\n");
        for (Summons s : Database.summonsList) {
            sb.append(String.format("ID: #%d | Citizen: %s | Plate: %s | Original Fee: RM%.2f | Current Due: RM%.2f | Status: %s\n",
                s.getSummonsId(), s.getDriver().getName(), s.getVehiclePlate(), s.getOriginalAmount(), s.getCurrentAmount(), s.getStatus()));
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Officer Global View", JOptionPane.INFORMATION_MESSAGE);
    }

    //FEATURE 3: VIEW ALL DRIVERS
    private static void officerViewAllDrivers() {
        StringBuilder sb = new StringBuilder("===== Registered Citizens Database =====\n\n");
        for (Driver d : Database.driverList) {
            sb.append(d.getFormattedDetails()).append("\n----------------------------------\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Database Records", JOptionPane.INFORMATION_MESSAGE);
    }

    //FEATURE 4: COURT VERDICT
    private static void officerRecordCourtAttendance() {
        String inputId = JOptionPane.showInputDialog(null, "Enter Summons ID scheduled for court hearing:", "Court Management", JOptionPane.QUESTION_MESSAGE);
        if (inputId == null || inputId.trim().isEmpty()) return;

        try {
            int id = Integer.parseInt(inputId.trim());
            Summons s = Database.findSummonsById(id);
            if (s != null) {
                int attended = JOptionPane.showConfirmDialog(null,
                    "Did offender " + s.getDriver().getName() + " attend the scheduled court hearing?",
                    "Court Verification", JOptionPane.YES_NO_OPTION);

                if (attended == JOptionPane.YES_OPTION) {
                    String verdictMenu = "===== ⚖️ COURT HEARING VERDICT =====\n" +
                                         "Offender: " + s.getDriver().getName() + "\n" +
                                         "Offence: " + s.getOffence() + "\n" +
                                         "Base Fine: RM " + String.format("%.2f", s.getOriginalAmount()) + "\n\n" +
                                         "Select Court Ruling [1-3]:\n" +
                                         "1. No Discount (Court upholds full penalty: RM " + String.format("%.2f", s.getOriginalAmount()) + ")\n" +
                                         "2. Court Leniency Discount (50% Off: RM " + String.format("%.2f", s.getOriginalAmount() * 0.50) + ")\n" +
                                         "3. Custom Court-Ordered Fine (Judge sets specific fine)\n\n" +
                                         "Enter Choice [1-3]:";

                    String verdictChoice = JOptionPane.showInputDialog(null, verdictMenu, "Court Decision", JOptionPane.QUESTION_MESSAGE);
                    if (verdictChoice == null || verdictChoice.trim().isEmpty()) return;

                    double newFine = s.getOriginalAmount();
                    String verdictDetails = "";

                    switch (verdictChoice.trim()) {
                        case "1":
                            newFine = s.getOriginalAmount();
                            verdictDetails = "No Discount Granted.\nOffender must pay the full fine of RM " + String.format("%.2f", newFine);
                            break;
                        case "2":
                            newFine = s.getOriginalAmount() * 0.50;
                            verdictDetails = "50% Leniency Discount Granted by Court!\nRevised fine: RM " + String.format("%.2f", newFine);
                            break;
                        case "3":
                            String customInput = JOptionPane.showInputDialog(null, "Enter custom fine amount ordered by judge (RM):", "Judge's Ruling", JOptionPane.QUESTION_MESSAGE);
                            if (customInput != null && !customInput.trim().isEmpty()) {
                                try {
                                    newFine = Double.parseDouble(customInput.trim());
                                    if (newFine < 0) newFine = 0.0;
                                    verdictDetails = "Custom Fine Ordered by Judge: RM " + String.format("%.2f", newFine);
                                } catch (NumberFormatException e) {
                                    JOptionPane.showMessageDialog(null, "Invalid amount. Upholding original fine.", "Warning", JOptionPane.WARNING_MESSAGE);
                                    newFine = s.getOriginalAmount();
                                    verdictDetails = "No Discount Granted.\nOffender must pay full fine of RM " + String.format("%.2f", newFine);
                                }
                            }
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Invalid selection! Defaulting to No Discount.", "Warning", JOptionPane.WARNING_MESSAGE);
                            newFine = s.getOriginalAmount();
                            verdictDetails = "No Discount Granted.\nOffender must pay full fine of RM " + String.format("%.2f", newFine);
                    }

                    s.setOriginalAmount(newFine);
                    s.setStatus("Court Verdict: Outstanding");

                    JOptionPane.showMessageDialog(null,
                        "Hearing Recorded Successfully!\n\n" +
                        verdictDetails + "\n" +
                        "Outstanding Balance Remaining: RM " + String.format("%.2f", s.getCurrentAmount()) + "\n" +
                        "Status: Court Verdict: Outstanding (Citizen must now pay the revised fine).",
                        "Court Verdict Registered", JOptionPane.INFORMATION_MESSAGE);

                } else if (attended == JOptionPane.NO_OPTION) {
                    s.getDriver().setBlacklisted(true);
                    s.setStatus("Blacklisted");
                    JOptionPane.showMessageDialog(null, "Offender failed to attend court. Citizen is now BLACKLISTED.", "Penalty Applied", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Summons ID not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
