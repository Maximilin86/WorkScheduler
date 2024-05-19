package me.maxpro.workscheduler.client.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UsersData {

    public final Map<Integer, User> byId = new HashMap<>();

    public void parseUsers(JSONArray json) throws JSONException {
        for (int i = 0; i < json.length(); i++) {
            JSONObject row = json.getJSONObject(i);
            User user = new User();
            user.parse(row);
            byId.put(user.id, user);
        }
    }

}
