package me.maxpro.workscheduler.client.data;

import org.json.JSONException;
import org.json.JSONObject;

import me.maxpro.workscheduler.client.ClientException;
import me.maxpro.workscheduler.utils.WSSession;

public class LoginData {

    public String token;
    public WSSession.Role role;
    public String displayName;

    public void parse(JSONObject jsonResponse) throws JSONException {
        this.token = jsonResponse.getString("token");
        String roleStr = jsonResponse.getString("role");
        this.role = WSSession.Role.fromString(roleStr);
        if(this.role == null) throw new ClientException("role is not found", "Неизвестная роль");
        this.displayName = jsonResponse.getString("display-name");
    }
}
