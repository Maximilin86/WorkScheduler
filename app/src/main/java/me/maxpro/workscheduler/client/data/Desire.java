package me.maxpro.workscheduler.client.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Desire {


    public final Type desire;
    public final String comment;

    public Desire(Type desire, String comment) {
        this.desire = desire;
        this.comment = comment;
    }

    public enum Type {
        REST("Выходной"),
        WORK("В день"),
        ALL_DAY("Смена");

        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        @Nullable
        public static Type fromString(String roleStr) {
            for (Type v : values()) {
                if (v.name().equalsIgnoreCase(roleStr)) return v;
            }
            return null;
        }

        @Nullable
        public static Type fromId(int id) {
            return values()[id];
        }

        public int toId() {
            for (int i = 0; i < values().length; i++) {
                if(values()[i] == this) return i;
            }
            return -1;
        }

        public String getDisplayName() {
            return this.displayName;
        }
    }

    @NonNull
    @Override
    public String toString() {
        if(this.desire == null) return "Не выбрано";
        return this.desire.getDisplayName();
    }

}
