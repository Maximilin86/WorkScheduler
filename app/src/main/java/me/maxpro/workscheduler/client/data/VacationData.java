package me.maxpro.workscheduler.client.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class VacationData {

    public final Map<Integer, Vacation> byId = new HashMap<>();

    public void parse(JSONArray json) throws JSONException, ParseException {
        for (int i = 0; i < json.length(); i++) {
            JSONObject row = json.getJSONObject(i);
            Vacation vacation = new Vacation();
            vacation.parse(row);
            byId.put(vacation.id, vacation);
        }
    }

}
