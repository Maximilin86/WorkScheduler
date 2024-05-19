package me.maxpro.workscheduler.client.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Holiday {

    public final boolean isWorkDay;
    public final String comment;

    public Holiday(boolean isWorkDay, String comment) {
        this.isWorkDay = isWorkDay;
        this.comment = comment;
    }

}
