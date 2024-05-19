package me.maxpro.workscheduler.client;

import org.json.JSONException;
import java.text.ParseException;

interface JSONRunnable {
    void run() throws JSONException, ParseException;
}
