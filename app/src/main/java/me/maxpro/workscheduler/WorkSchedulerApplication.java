package me.maxpro.workscheduler;

import android.app.Application;
import android.content.Context;

public class WorkSchedulerApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        WorkSchedulerApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return WorkSchedulerApplication.context;
    }

}
