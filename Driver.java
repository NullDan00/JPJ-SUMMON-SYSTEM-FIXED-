package com.mycompany.jpjsummon;

// MODEL: Citizen Profile (Chapter 5)
public class Driver {
    private String name;
    private String icNumber;
    private int demeritPoints;
    private boolean isBlacklisted;

    public Driver(String inputName, String inputIcNumber) {
        name = inputName;
        icNumber = inputIcNumber;
        demeritPoints = 0;
        isBlacklisted = false;
    }

    public String getName() {
        return name;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public int getDemeritPoints() {
        return demeritPoints;
    }

    public boolean isBlacklisted() {
        return isBlacklisted;
    }

    public void addDemeritPoints(int pointsToAdd) {
        demeritPoints += pointsToAdd; // Arithmetic Assignment Operator (Chapter 2)
    }

    public void setBlacklisted(boolean blacklistStatus) {
        isBlacklisted = blacklistStatus;
    }

    public String getFormattedDetails() {
        return "Citizen Name: " + name + "\n" +
               "IC Number: " + icNumber + "\n" +
               "Current Demerit Points: " + demeritPoints + "/20\n" +
               "License Status: " + (isBlacklisted ? "🔴 BLACKLISTED" : "🟢 ACTIVE");
    }
}
