package me.maxpro.workscheduler.client.data;

import androidx.annotation.Nullable;

public class Order {


    public final int userId;
    public final Type order;
    public final String comment;

    public Order(int userId, Type order, String comment) {
        this.userId = userId;
        this.order = order;
        this.comment = comment;
    }

    public enum Type {
        REST,
        WORK,
        ALL_DAY;

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

    }

}
