package me.maxpro.workscheduler.client.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HolidaysData {

    public final Map<Integer, Holiday> byDay = new HashMap<>();

    public void parse(JSONArray json) throws JSONException {
        for (int i = 0; i < json.length(); i++) {
            JSONObject byDay = json.getJSONObject(i);
            int day = byDay.getInt("day");
            boolean isWorkDay = byDay.has("is_work_day") ? byDay.getBoolean("is_work_day") : false;
            String comment = byDay.has("comment") ? byDay.getString("comment") : "";
            this.byDay.put(day, new Holiday(isWorkDay, comment));
        }
    }
}
