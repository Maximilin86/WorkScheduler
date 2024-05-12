package me.maxpro.workscheduler.client.data;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import me.maxpro.workscheduler.client.ClientException;
import me.maxpro.workscheduler.utils.WSSession;

public class User {

    public int id;
    public String login;
    public String firstName;
    public String lastName;
    public String fathersName;
    public WSSession.Role role;

    public User() {}
    public User(int id, String login, String firstName, String lastName, String fathersName, WSSession.Role role) {
        this.id = id;
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fathersName = fathersName;
        this.role = role;
    }

    public void parse(JSONObject row) throws JSONException {
        this.id = row.getInt("id");
        this.login = row.getString("login");
        this.firstName = row.getString("first_name");
        this.lastName = row.getString("last_name");
        this.fathersName = row.has("fathers_name") && !row.isNull("fathers_name") ? row.getString("fathers_name") : null;

        String roleStr = row.getString("role");
        WSSession.Role role = WSSession.Role.fromString(roleStr);
        if(role == null) throw new ClientException("role is not found", "Неизвестная роль");
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
