package me.maxpro.workscheduler;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarCellView;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


public class MenuActivity extends Activity {

    CalendarPickerView calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);


        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, -1);
        Date minDate = c.getTime();
        c.add(Calendar.YEAR, 2);
        Date maxData = c.getTime();
        Log.d("Test", minDate + " " + maxData);
        Date today = new Date();
        calendar.init(minDate, maxData)
                .withSelectedDate(today);

        calendar.setDecorators(Arrays.asList(
                (cellView, date) -> {
                    // Highlight the date if it's a holiday
//                    if ((date.getDay() % 3) != 0) {
//                        cellView.setBackgroundColor(Color.parseColor("#baabab"));
//                    }
                    if ((date.getDay() % 3) == 1) {
                        cellView.setOutlineAmbientShadowColor(Color.parseColor("#5d9e7d"));
                    }
                    if ((date.getDay() % 3) == 2) {
                        cellView.setOutlineSpotShadowColor(Color.parseColor("#5d929e"));
                    }
                }
        ));

    }

}
