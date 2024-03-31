package me.maxpro.workscheduler;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

public class WSClient {

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static final Executor MAIN = HANDLER::post;


    public static String formatHexDump(byte[] array, int offset, int length) {
        final int width = 16;

        StringBuilder builder = new StringBuilder();

        for (int rowOffset = offset; rowOffset < offset + length; rowOffset += width) {
            builder.append(String.format("%06d:  ", rowOffset));

            for (int index = 0; index < width; index++) {
                if (rowOffset + index < array.length) {
                    builder.append(String.format("%02x ", array[rowOffset + index]));
                } else {
                    builder.append("   ");
                }
            }

            if (rowOffset < array.length) {
                int asciiWidth = Math.min(width, array.length - rowOffset);
                builder.append("  |  ");
                try {
                    builder.append(new String(array, rowOffset, asciiWidth, "UTF-8").replaceAll("\r\n", " ").replaceAll("\n", " "));
                } catch (UnsupportedEncodingException ignored) {
                    //If UTF-8 isn't available as an encoding then what can we do?!
                }
            }

            builder.append(String.format("%n"));
        }

        return builder.toString();
    }

    static public CompletableFuture<String> Login(String user, String password) {  // Main thread
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {  // Network thread
            try {
                return post_request(user, password);
            } catch(Throwable e) {
                throw new RuntimeException(e);
            }
        }, EXECUTOR);
        return future.thenApplyAsync((a) -> a, MAIN);  // move to main thread
    }

    @NonNull
    private static String post_request(String user, String password) throws Exception {
        Thread.sleep(1000);
        JSONObject jo = new JSONObject();
        jo.put("user", user);
        jo.put("password", password);
        String jsonString = jo.toString();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        StringEntity requestEntity = new StringEntity(
                jsonString,
                ContentType.APPLICATION_JSON);
        HttpPost postMethod = new HttpPost("http://192.168.1.104:8000/login");
        postMethod.setEntity(requestEntity);


        RequestConfig.Builder requestConfig = RequestConfig.custom();
        requestConfig.setConnectTimeout(5, TimeUnit.SECONDS);
        requestConfig.setConnectionRequestTimeout(5, TimeUnit.SECONDS);
        requestConfig.setResponseTimeout(5, TimeUnit.SECONDS);
        postMethod.setConfig(requestConfig.build());

        CloseableHttpResponse rawResponse = httpClient.execute(postMethod);
        Log.d("Test", "POST Response Status:: " + rawResponse.getCode());
        String json = EntityUtils.toString(rawResponse.getEntity());
        httpClient.close();

        JSONObject jsonResponse = new JSONObject(json);
        Log.d("Test", "POST Response Status:: " + jsonResponse.toString());
        if(jsonResponse.has("error")) {
            throw new Exception("server error: " + jsonResponse.getString("error"));
        }
        String token = jsonResponse.getString("token");
        if(token == null || token.isEmpty()) throw new RuntimeException("bad token");
        return token;
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
