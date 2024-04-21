package me.maxpro.workscheduler;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WSClient {

    public static class ClientException extends RuntimeException {

        private final String displayText;

        public ClientException(String text, String displayText) {
            super(text);
            this.displayText = displayText;
        }

        public ClientException(String text, Throwable t, String displayText) {
            super(text, t);
            this.displayText = displayText;
        }

        public String getDisplayText() {
            return displayText;
        }

    }

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static final Executor MAIN = HANDLER::post;


    private static void handleServerError(JSONObject jsonResponse) {
        if(!jsonResponse.has("error")) return;
        try {
            String error = jsonResponse.getString("error");
            throw new ClientException(
                    "server error: " + error,
                    "Ошибка на сервере: " + error
            );
        } catch (JSONException ignore) {}
    }

    @NonNull
    private static JSONObject parseServerAnswer(String json) {
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            throw new ClientException("Json parse error", e, "Ошибка парсинга ответа от сервера" + e.getMessage());
        }
    }

    interface JSONRunnable {
        void run() throws JSONException;
    }

    private static void buildMessage(JSONRunnable r) {
        try {
            r.run();
        } catch(JSONException e) {
            throw new ClientException("Json format error", e, "Ошибка формирования запроса " + e.getMessage());
        }
    }

    public static void showNetworkError(Context context, Throwable throwable) {
        if(throwable == null) return;
        String message;
        if (throwable instanceof CompletionException) {
            if(throwable.getCause() != null) {
                throwable = throwable.getCause();
            }
        }
        if (throwable instanceof ClientException) {
            message = ((ClientException) throwable).getDisplayText();
        } else {
            message = throwable.getMessage();
        }
        new AlertDialog.Builder(context)
                .setTitle("Сетевая Ошибка")
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    static public CompletableFuture<String> setDesire(String token, String desireId) {  // Main thread
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            buildMessage(() -> {
                jo.put("token", token);
                jo.put("desireId", desireId);
            });
            String json = post_request("/set_desire", jo.toString());
            JSONObject jsonResponse = parseServerAnswer(json);
            handleServerError(jsonResponse);
            return "OK";
        }, EXECUTOR);
        return future.thenApplyAsync((a) -> a, MAIN);  // move to main thread
    }

    static public CompletableFuture<String> Login(String user, String password) {  // Main thread
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {  // Network thread
            JSONObject jo = new JSONObject();
            buildMessage(() -> {
                jo.put("user", user);
                jo.put("password", password);
            });
            String json = post_request("/login", jo.toString());
            JSONObject jsonResponse = parseServerAnswer(json);
            handleServerError(jsonResponse);
            String token = getStringOrNull(jsonResponse, "token");
            if(token == null || token.isEmpty()) throw new ClientException("token is not found", "Токен не найден");
            return token;
        }, EXECUTOR);
        return future.thenApplyAsync((a) -> a, MAIN);  // move to main thread
    }

    @Nullable
    private static String getStringOrNull(JSONObject json, String key) {
        try {
            return json.getString(key);
        } catch (JSONException e) {
            return null;
        }
    }

    @NonNull
    private static String post_request(String path, String jsonString) {
        StringEntity requestEntity = new StringEntity(
                jsonString,
                ContentType.APPLICATION_JSON);
        HttpPost postMethod = new HttpPost("http://192.168.0.108:8000" + path);
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

        try {
            int statusCode = rawResponse.getCode();
            String json = EntityUtils.toString(rawResponse.getEntity());
            Log.d("Test", "POST Response: " + statusCode + " " + json);
            return json;
        } catch (IOException | ParseException e) {
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
