package me.maxpro.workscheduler.client.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersData {

    public final Map<Integer, List<Order>> ordersByDay = new HashMap<>();

    public void parseOrdersByDay(JSONArray json) throws JSONException {
        for (int i = 0; i < json.length(); i++) {
            JSONObject byDay = json.getJSONObject(i);
            int day = byDay.getInt("day");
            List<Order> orders = new ArrayList<>();
            JSONArray jsonOrders = byDay.getJSONArray("orders");
            for (int j = 0; j < jsonOrders.length(); j++) {
                JSONObject row = jsonOrders.getJSONObject(j);
                int user_id = row.getInt("user_id");
                Order.Type order = Order.Type.fromId(row.getInt("order_id"));
                String comment = row.getString("comment");
                orders.add(new Order(user_id, order, comment));
            }
            this.ordersByDay.put(day, orders);
        }
    }
}
