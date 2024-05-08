package me.maxpro.workscheduler.client;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.maxpro.workscheduler.client.data.LoginData;
import me.maxpro.workscheduler.client.data.MonthData;
import me.maxpro.workscheduler.client.data.Order;
import me.maxpro.workscheduler.client.data.UsersData;
import me.maxpro.workscheduler.utils.WSSession;

public class WSClient {


    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    public static final Executor MAIN = HANDLER::post;

    public static CompletableFuture<MonthData> getMonthData(String token, Date date) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("date", DateFormat.format("yyyy-MM-dd", date));
            });
            JSONObject jsonResponse = postJson("/get_month_data", jo);
            MonthData monthData = new MonthData();
            ClientUtils.parseMessage(() -> {
                JSONArray ordersByDay = jsonResponse.getJSONArray("orders_by_day");
                monthData.parseOrdersByDay(ordersByDay);
            });
            return monthData;
        }, EXECUTOR);
    }

    public static CompletableFuture<MonthData> autifill(String token, Date date) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("date", DateFormat.format("yyyy-MM-dd", date));
            });
            JSONObject jsonResponse = postJson("/autifill", jo);
            MonthData monthData = new MonthData();
            ClientUtils.parseMessage(() -> {
                JSONArray ordersByDay = jsonResponse.getJSONArray("orders_by_day");
                monthData.parseOrdersByDay(ordersByDay);
            });
            return monthData;
        }, EXECUTOR);
    }

    public static CompletableFuture<UsersData> loadUsers(String token) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
            });
            JSONObject jsonResponse = postJson("/get_users", jo);
            UsersData monthData = new UsersData();
            ClientUtils.parseMessage(() -> {
                JSONArray users = jsonResponse.getJSONArray("users");
                monthData.parseUsers(users);
            });
            return monthData;
        }, EXECUTOR);
    }

    public static CompletableFuture<String> setOrder(String token, Date date, int userId, Order.Type order, @Nullable String comment) {  // Main thread
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("date", DateFormat.format("yyyy-MM-dd", date));
                jo.put("user_id", userId);
                jo.put("order_id", order.toId());
                if(comment != null) jo.put("comment", comment);
            });
            JSONObject jsonResponse = postJson("/set_order", jo);
            return "OK";
        }, EXECUTOR);
    }

    public static CompletableFuture<String> setWorkOrders(String token, Date date, int[] userIds, @Nullable String comment) {  // Main thread
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("date", DateFormat.format("yyyy-MM-dd", date));
                JSONArray jsonArray = new JSONArray();
                for (int userId : userIds) jsonArray.put(userId);
                jo.put("user_ids", jsonArray);
                if(comment != null) jo.put("comment", comment);
            });
            JSONObject jsonResponse = postJson("/set_work_orders", jo);
            return "OK";
        }, EXECUTOR);
    }

    public static CompletionStage<List<Order>> getOrders(String token, Date date) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("date", DateFormat.format("yyyy-MM-dd", date));
            });
            JSONObject jsonResponse = postJson("/get_orders", jo);
            List<Order> orders = new ArrayList<>();
            ClientUtils.parseMessage(() -> {
                JSONArray ordersJson = jsonResponse.getJSONArray("orders");
                for (int i = 0; i < ordersJson.length(); i++) {
                    JSONObject row = ordersJson.getJSONObject(i);
                    int userId = row.getInt("user_id");
                    int orderId = row.getInt("order_id");
                    Order.Type order = Order.Type.fromId(orderId);
                    String comment = row.has("comment") ? row.getString("comment") : "";
                    orders.add(new Order(userId, order, comment));
                }
            });
            return orders;
        }, EXECUTOR);
    }

    public static CompletableFuture<String> setDesire(String token, Date date, String desireId, @Nullable String comment) {  // Main thread
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("date", DateFormat.format("yyyy-MM-dd", date));
                jo.put("desire_id", desireId);
                if(comment != null) jo.put("comment", comment);
            });
            JSONObject jsonResponse = postJson("/set_desire", jo);
            return "OK";
        }, EXECUTOR);
    }

    static public CompletableFuture<LoginData> login(String login, String password) {  // Main thread
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("login", login);
                jo.put("password", password);
            });
            JSONObject jsonResponse = postJson("/login", jo);
            LoginData loginData = new LoginData();
            ClientUtils.parseMessage(() -> {
                loginData.parse(jsonResponse);
            });
            return loginData;
        }, EXECUTOR);
    }

    @NonNull
    private static JSONObject postJson(String path, JSONObject jo) {
        MyResponse response = postText(path, new StringEntity(jo.toString(), ContentType.APPLICATION_JSON));
        JSONObject jsonResponse = ClientUtils.parseServerAnswer(response);
        ClientUtils.handleServerError(jsonResponse);
        return jsonResponse;
    }

    @NonNull
    private static MyResponse postText(String path, StringEntity requestEntity) {
        String url = WSSession.getInstance().url;
        HttpPost postMethod = new HttpPost(url + path);
        postMethod.setEntity(requestEntity);


        RequestConfig.Builder requestConfig = RequestConfig.custom();
        requestConfig.setConnectTimeout(5, TimeUnit.SECONDS);
        requestConfig.setConnectionRequestTimeout(5, TimeUnit.SECONDS);
        requestConfig.setResponseTimeout(5, TimeUnit.SECONDS);
        postMethod.setConfig(requestConfig.build());

        CloseableHttpResponse rawResponse;
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            rawResponse = httpClient.execute(postMethod);
            httpClient.close();
        } catch (IOException e) {
            throw new ClientException("Connect server error", e, "Нет ответа от сервера");
        }
        int statusCode = rawResponse.getCode();

        try {
            String responseBody = EntityUtils.toString(rawResponse.getEntity());
            Log.d("Test", "POST Response: " + statusCode + " " + responseBody);
            return new MyResponse(responseBody, statusCode, rawResponse.getReasonPhrase(), path);
        } catch (IOException | ParseException e) {
            if(statusCode != 200) {
                throw new ClientException("Network error", e, "Сервер ответил " + statusCode + " " + rawResponse.getReasonPhrase() + " на запрос " + path);
            }
            throw new ClientException("Cant get answer from server", e, "Ошибка получения ответа от сервера");
        }
    }


//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet("http://192.168.0.102:8000/login");
//        httpGet.addHeader("User-Agent", USER_AGENT);
//        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
//
//        Log.d("Test", "GET Response Status: " + httpResponse.getReasonPhrase());
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(
//                httpResponse.getEntity().getContent()));
//
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//        while ((inputLine = reader.readLine()) != null) {
//            response.append(inputLine);
//        }
//        reader.close();
//
//        // print result
//        Log.d("Test", response.toString());
//        httpClient.close();

}
