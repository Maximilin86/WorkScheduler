package me.maxpro.workscheduler;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WSClient {
    static public String Login(String user, String password) {
        try {
            JSONObject jo = new JSONObject();
            jo.put("user", user);
            jo.put("password", password);
            String json = jo.toString();

            URL url = new URL("http://192.168.1.100:8000/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            try(OutputStream os = connection.getOutputStream()) {
                try(Writer w = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                    w.write(json);
                }
                os.flush();
            }
            int status = connection.getResponseCode();
            if(status != 200 /*&& status != 201*/) {
                throw new Exception("Invalid return status");
            }
            JSONObject jsonResponse;
            try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                jsonResponse = new JSONObject(sb.toString());
            }
            if(jsonResponse.has("error")) {
                throw new Exception("server error: " + jsonResponse.getString("error"));
            }
            String token = jsonResponse.getString("token");
            connection.disconnect();
            return token;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
