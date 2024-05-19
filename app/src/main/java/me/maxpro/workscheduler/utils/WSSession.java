package me.maxpro.workscheduler.utils;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import me.maxpro.workscheduler.client.data.LoginData;
import me.maxpro.workscheduler.client.data.UsersData;
import me.maxpro.workscheduler.client.data.VacationData;

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
    public UsersData users = null;
    public final Map<Integer, VacationData> vacationsByUser = new HashMap();

    public WSSession() {}

    public void initFromLogin(LoginData args) {
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
