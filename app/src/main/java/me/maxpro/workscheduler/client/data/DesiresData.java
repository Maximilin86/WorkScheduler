package me.maxpro.workscheduler.client.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DesiresData {

    public final Map<Integer, Desire> desiresByDay = new HashMap<>();

    public void parse(JSONArray json) throws JSONException {
        for (int i = 0; i < json.length(); i++) {
            JSONObject byDay = json.getJSONObject(i);
            int day = byDay.getInt("day");
            Desire.Type order = Desire.Type.fromId(byDay.getInt("desire_id"));
            String comment = byDay.getString("comment");
            this.desiresByDay.put(day, new Desire(order, comment));
        }
    }
}
