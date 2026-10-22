package com.mycompany.jpjsummon;

import javax.swing.JOptionPane;

// MAIN: Dual Portal Gateway
public class Main {

    public static void main(String[] args) {
        Database.initializeSampleData();

        while (true) {
            String portalMenu = "===== RAKYAT DIGITAL JPJ PORTAL =====\n" +
                                "Please select your access gateway:\n\n" +
                                "1. 👮 JPJ Officer Access (Sign In / Register)\n" +
                                "2. 👤 Citizen Self-Service Portal\n" +
                                "3. ❌ Exit Portal\n\n" +
                                "Select Option (1-3):";

            String gatewayChoice = JOptionPane.showInputDialog(null, portalMenu, "Main Gateway", JOptionPane.PLAIN_MESSAGE);

            if (gatewayChoice == null || gatewayChoice.trim().equals("3")) {
                JOptionPane.showMessageDialog(null, "Thank you for using JPJ Portal. Drive safely!", "Portal Closed", JOptionPane.INFORMATION_MESSAGE);
                break;
            }

            switch (gatewayChoice.trim()) {
                case "1":
                    OfficerPortal.handleOfficerAccess();
                    break;
                case "2":
                    CitizenPortal.runCitizenPortal();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice! Please select 1, 2, or 3.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
