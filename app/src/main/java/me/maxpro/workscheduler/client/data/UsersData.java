package me.maxpro.workscheduler.client.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.maxpro.workscheduler.client.ClientException;
import me.maxpro.workscheduler.utils.WSSession;

public class UsersData {

    public final Map<Integer, User> usersById = new HashMap<>();

    public void parseUsers(JSONArray json) throws JSONException {
        for (int i = 0; i < json.length(); i++) {
            JSONObject row = json.getJSONObject(i);
            int user_id = row.getInt("id");
            String login = row.getString("login");
            String first_name = row.getString("first_name");
            String last_name = row.getString("last_name");
            String fathers_name = row.has("fathers_name") && !row.isNull("fathers_name") ? row.getString("fathers_name") : null;

            String roleStr = row.getString("role");
            WSSession.Role role = WSSession.Role.fromString(roleStr);
            if(role == null) throw new ClientException("role is not found", "Неизвестная роль");

            usersById.put(user_id, new User(user_id, login, first_name, last_name, fathers_name, role));
        }
    }
}
