package me.maxpro.workscheduler.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CompletionException;

public class ClientUtils {




    public static void handleServerError(JSONObject jsonResponse) {
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
    public static JSONObject parseServerAnswer(MyResponse response) {
        try {
            return new JSONObject(response.body);
        } catch (JSONException e) {
            if(response.statusCode != 200) {
                throw new ClientException("Network error", e, "Сервер ответил " + response.statusCode + " " + response.reasonPhrase + " на запрос " + response.path);
            }
            throw new ClientException("Json parse error", e, "Ошибка парсинга ответа от сервера" + e.getMessage());
        }
    }

    public static void buildMessage(JSONRunnable r) {
        try {
            r.run();
        } catch(Exception e) {
            throw new ClientException("Json format error", e, "Ошибка формирования запроса " + e.getMessage());
        }
    }

    public static void parseMessage(JSONRunnable r) {
        try {
            r.run();
        } catch(Exception e) {
            throw new ClientException("Json parse error", e, "Ошибка парсинга ответа " + e.getMessage());
        }
    }

    public static void showNetworkError(Context context, Throwable throwable, Runnable onDismiss) {
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
            message = throwable.getClass().getSimpleName() + " " + throwable.getMessage();
        }
        throwable.printStackTrace();
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
//                .setNegativeButton(android.R.string.no, null)
                .setOnDismissListener(dialog -> {
                    onDismiss.run();
                })
                .setOnCancelListener(dialog -> {
                    onDismiss.run();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
