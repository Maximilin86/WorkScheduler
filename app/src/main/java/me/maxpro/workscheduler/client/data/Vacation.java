package me.maxpro.workscheduler.client.data;

import android.content.Context;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

import me.maxpro.workscheduler.WorkSchedulerApplication;
import me.maxpro.workscheduler.client.ClientException;
import me.maxpro.workscheduler.utils.WSSession;

public class Vacation {

    public int id;
    public Date from;
    public Date to;
    public Type type;
    public String comment;

    public Vacation() {}
    public Vacation(int id, Date from, Date to, Type type, String comment) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.type = type;
        this.comment = comment;
    }

    public void parse(JSONObject row) throws JSONException, ParseException {
        this.id = row.getInt("id");
        java.text.DateFormat dateFormat = DateFormat.getDateFormat(WorkSchedulerApplication.getAppContext());
        this.from = dateFormat.parse(row.getString("from"));
        this.to = dateFormat.parse(row.getString("to"));

        String typeStr = row.getString("type");
        Type type = Type.fromString(typeStr);
        if(type == null) throw new ClientException("type is not found", "Неизвестный тип отпуска");
        this.type = type;

        this.comment = row.has("comment") ? row.getString("comment") : "";
    }

    @NonNull
    @Override
    public String toString() {
        return DateFormat.format("yyyy-MM-dd", this.from)
                + " - "
                + DateFormat.format("yyyy-MM-dd", this.to)
                + " "
                + this.type.getDisplayName();
    }


    public enum Type {
        VACATION("Отпуск"),
        BUSINESS_TRIP("Командировка");

        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        @Nullable
        public static Type fromString(String roleStr) {
            for (Type type : values()) {
                if (type.name().equalsIgnoreCase(roleStr)) return type;
            }
            return null;
        }

        public String getDisplayName() {
            return displayName;
        }

        @NonNull
        @Override
        public String toString() {
            return displayName;
        }
    }
}
