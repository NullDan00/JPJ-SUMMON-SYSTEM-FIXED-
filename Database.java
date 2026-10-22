package com.mycompany.jpjsummon;

import java.util.ArrayList;
import java.time.LocalDate;

// DATA LAYER: shared lists + search methods + sample data (Chapter 4 loops, Chapter 5 methods)
public class Database {
    public static ArrayList<Summons> summonsList = new ArrayList<>();
    public static ArrayList<Driver> driverList = new ArrayList<>();
    public static ArrayList<Officer> officerList = new ArrayList<>();

    public static Officer findOfficerByUsername(String username) {
        for (Officer o : officerList) {
            if (o.getUsername().equalsIgnoreCase(username)) {
                return o;
            }
        }
        return null;
    }

    public static Driver findOrCreateDriver(String ic, String name) {
        Driver existing = findDriverByIc(ic);
        if (existing != null) {
            return existing;
        }
        Driver newDriver = new Driver(name, ic);
        driverList.add(newDriver);
        return newDriver;
    }

    public static Summons findSummonsById(int id) {
        for (Summons s : summonsList) {
            if (s.getSummonsId() == id) {
                return s;
            }
        }
        return null;
    }

    public static Driver findDriverByIc(String ic) {
        for (Driver d : driverList) {
            if (d.getIcNumber().equalsIgnoreCase(ic.trim())) {
                return d;
            }
        }
        return null;
    }

    public static void initializeSampleData() {
        // Default Officer Account
        Officer defaultOfficer = new Officer("Sergeant Razak", "JPJ-1001", "jpjofficer", "jpj123");
        officerList.add(defaultOfficer);

        Driver d1 = new Driver("Muhammad Danish Lee", "010203-10-1234");
        Driver d2 = new Driver("Khoo Mei Ling", "980712-14-5678");

        d1.addDemeritPoints(10);
        d2.addDemeritPoints(5);
        driverList.add(d1);
        driverList.add(d2);

        // Overdue summons
        LocalDate pastIssueDate = LocalDate.now().minusDays(30);
        Summons overdueSummons = new Summons("WPP1234", "Speeding (Over 110km/h)", 300.0, pastIssueDate, 14, d1, "Sergeant Razak");

        // Active summons
        Summons normalSummons = new Summons("VCA5678", "Double Parking Obstruction", 150.0, 14, d2, "Sergeant Razak");

        summonsList.add(overdueSummons);
        summonsList.add(normalSummons);
    }
}
