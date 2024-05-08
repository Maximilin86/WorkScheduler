package me.maxpro.workscheduler.client.data;

import androidx.annotation.NonNull;

import me.maxpro.workscheduler.utils.WSSession;

public class User {

    public final int id;
    public final String login;
    public final String firstName;
    public final String lastName;
    public final String fathersName;
    public final WSSession.Role role;

    public User(int id, String login, String firstName, String lastName, String fathersName, WSSession.Role role) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fathersName = fathersName;
        this.role = role;
    }

    @NonNull
    @Override
    public String toString() {
        String out = firstName + " " + lastName;
        if(fathersName != null) {
            out += " " + fathersName;
        }
        return out;
    }
}
