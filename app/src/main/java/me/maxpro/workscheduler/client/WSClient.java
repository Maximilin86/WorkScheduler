package me.maxpro.workscheduler.client;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

import me.maxpro.workscheduler.client.data.Desire;
import me.maxpro.workscheduler.client.data.DesiresData;
import me.maxpro.workscheduler.client.data.LoginData;
import me.maxpro.workscheduler.client.data.OrdersData;
import me.maxpro.workscheduler.client.data.Order;
import me.maxpro.workscheduler.client.data.User;
import me.maxpro.workscheduler.client.data.UsersData;
import me.maxpro.workscheduler.client.data.VacationData;
import me.maxpro.workscheduler.utils.WSSession;

public class WSClient {


    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());
    public static final Executor MAIN = HANDLER::post;

    public static CompletableFuture<OrdersData> getMonthData(String token, Date date) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("date", DateFormat.format("yyyy-MM-dd", date));
            });
            JSONObject jsonResponse = postJson("/get_month_data", jo);
            OrdersData ordersData = new OrdersData();
            ClientUtils.parseMessage(() -> {
                JSONArray ordersByDay = jsonResponse.getJSONArray("orders_by_day");
                ordersData.parse(ordersByDay);
            });
            return ordersData;
        }, EXECUTOR);
    }

    public static CompletableFuture<DesiresData> getDesireData(String token, Date date) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("date", DateFormat.format("yyyy-MM-dd", date));
            });
            JSONObject jsonResponse = postJson("/get_desire_data", jo);
            DesiresData data = new DesiresData();
            ClientUtils.parseMessage(() -> {
                JSONArray ordersByDay = jsonResponse.getJSONArray("desires_by_day");
                data.parse(ordersByDay);
            });
            return data;
        }, EXECUTOR);
    }

    public static CompletableFuture<OrdersData> autifill(String token, Date date) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("date", DateFormat.format("yyyy-MM-dd", date));
            });
            JSONObject jsonResponse = postJson("/autifill", jo);
            OrdersData ordersData = new OrdersData();
            ClientUtils.parseMessage(() -> {
                JSONArray ordersByDay = jsonResponse.getJSONArray("orders_by_day");
                ordersData.parse(ordersByDay);
            });
            return ordersData;
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

    public static CompletableFuture<VacationData> loadVacations(String token, int userId) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("target_user_id", userId);
            });
            JSONObject jsonResponse = postJson("/get_vacations", jo);
            VacationData vacationsData = new VacationData();
            ClientUtils.parseMessage(() -> {
                JSONArray vacations = jsonResponse.getJSONArray("vacations");
                vacationsData.parse(vacations);
            });
            return vacationsData;
        }, EXECUTOR);
    }

    public static CompletableFuture<UsersData> saveUser(String token, User user, String password) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("user_id", user.id);
                jo.put("login", user.login);
                if(password != null && !password.isEmpty()) jo.put("password", password);
                jo.put("first_name", user.firstName);
                jo.put("last_name", user.lastName);
                jo.put("fathers_name", user.fathersName);
            });
            JSONObject jsonResponse = postJson("/set_user", jo);
            UsersData monthData = new UsersData();
            ClientUtils.parseMessage(() -> {
                JSONArray users = jsonResponse.getJSONArray("users");
                monthData.parseUsers(users);
            });
            return monthData;
        }, EXECUTOR);
    }

    public static CompletableFuture<UsersData> deleteUser(String token, int userId) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("user_id", userId);
            });
            JSONObject jsonResponse = postJson("/delete_user", jo);
            UsersData monthData = new UsersData();
            ClientUtils.parseMessage(() -> {
                JSONArray users = jsonResponse.getJSONArray("users");
                monthData.parseUsers(users);
            });
            return monthData;
        }, EXECUTOR);
    }

    public static CompletableFuture<User> addUser(String token) {
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
            });
            JSONObject jsonResponse = postJson("/add_user", jo);
            User user = new User();
            ClientUtils.parseMessage(() -> {
                JSONObject row = jsonResponse.getJSONObject("user");
                user.parse(row);
            });
            return user;
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

    public static CompletableFuture<String> setDesire(String token, Date date, Desire.Type desire, @Nullable String comment) {  // Main thread
        return CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            ClientUtils.buildMessage(() -> {
                jo.put("token", token);
                jo.put("date", DateFormat.format("yyyy-MM-dd", date));
                jo.put("desire_id", desire != null ? desire.toId() : -1);
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

        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            CloseableHttpResponse rawResponse;
            try {
                rawResponse = httpClient.execute(postMethod);
            } catch (IOException e) {
                throw new ClientException("Connect server error", e, "Нет ответа от сервера");
            }
            int statusCode = rawResponse.getCode();

            try(rawResponse) {
                String responseBody = EntityUtils.toString(rawResponse.getEntity());
                Log.d("Test", "POST Response: " + statusCode + " " + responseBody);
                return new MyResponse(responseBody, statusCode, rawResponse.getReasonPhrase(), path);
            } catch (IOException | ParseException e) {
                if(statusCode != 200) {
                    throw new ClientException("Network error", e, "Сервер ответил " + statusCode + " " + rawResponse.getReasonPhrase() + " на запрос " + path);
                }
                throw new ClientException("Cant get answer from server", e, "Ошибка получения ответа от сервера");
            }
        } finally {
            try {httpClient.close();} catch (IOException ignored) {}
        }
    }

    private static String toString(HttpEntity he) throws IOException {
        int total = (int) he.getContentLength();
        Log.d("Test", "len: " + total);
        byte[] bytes = new byte[total];
        int n = 0;
        try(InputStream is = he.getContent()) {
            while (n < total) {
                int count = is.read(bytes, n, total - n);
                if (count < 0)
                    break;
                n += count;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Test", "sblen: " + n);
        return new String(bytes, 0, n, StandardCharsets.UTF_8);
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
