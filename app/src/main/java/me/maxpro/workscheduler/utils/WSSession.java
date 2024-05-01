package me.maxpro.workscheduler.utils;

import androidx.annotation.Nullable;

public class WSSession {

    private static WSSession INSTANCE = null;

    public static WSSession getInstance() {
        if(INSTANCE == null) INSTANCE = new WSSession();
        return INSTANCE;
    }

    public String url = null;
    public String token = null;
    public Role role = Role.USER;
    public String displayName = null;

    public WSSession() {}

    public void initFromLogin(WSClient.LoginArgs args) {
        token = args.token;
        role = args.role;
        displayName = args.displayName;
    }


    public enum Role {
        USER,
        ADMIN;

        @Nullable
        public static Role fromString(String roleStr) {
            for (Role role : values()) {
                if (role.name().equalsIgnoreCase(roleStr)) return role;
            }
            return null;
        }
    }


}