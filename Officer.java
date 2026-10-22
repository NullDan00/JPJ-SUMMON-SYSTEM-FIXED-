package com.mycompany.jpjsummon;

// MODEL: JPJ Personnel Account
public class Officer {
    private String officerName;
    private String badgeId;
    private String username;
    private String password;

    public Officer(String inputName, String inputBadge, String inputUser, String inputPass) {
        officerName = inputName;
        badgeId = inputBadge;
        username = inputUser;
        password = inputPass;
    }

    public String getOfficerName() {
        return officerName;
    }

    public String getBadgeId() {
        return badgeId;
    }

    public String getUsername() {
        return username;
    }

    public boolean verifyPassword(String inputPass) {
        return password.equals(inputPass);
    }
}
