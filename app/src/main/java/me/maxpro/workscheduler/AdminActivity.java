package me.maxpro.workscheduler;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;


public class AdminActivity extends Activity {

    private CompactCalendarView compactCalendarView;
    private TextView currentMonth;
    private Spinner watcher;

    private String formatMonth(Date dateClicked) {
        String[] names = new String[] {
                "Январь", "Февраль",
                "Март", "Апрель", "Май",
                "Июнь", "Июль", "Август",
                "Сентябрь", "Октябрь", "Ноябрь",
                "Декабрь",
        };
        return names[dateClicked.getMonth()] + " " + Integer.toString(dateClicked.getYear() + 1900);
//        return dateFormatForMonth.format(dateClicked);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view);
        compactCalendarView = findViewById(R.id.compactcalendar_view);
        currentMonth = findViewById(R.id.current_month);
        watcher = findViewById(R.id.watcher);
        compactCalendarView.setDayColumnNames(new String[] {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"});

        Instant day = Instant.now().plus(1, ChronoUnit.DAYS);
        Event ev1 = new Event(Color.GREEN, day.toEpochMilli(), "Some extra data that I want to store.");
        compactCalendarView.addEvent(ev1);

        currentMonth.setText(formatMonth(compactCalendarView.getFirstDayOfCurrentMonth()));
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                currentMonth.setText(formatMonth(dateClicked));
//                List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);
//                Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(dateClicked));
//                if (bookingsFromMap != null) {
//                    Log.d(TAG, bookingsFromMap.toString());
//                    mutableBookings.clear();
//                    for (Event booking : bookingsFromMap) {
//                        mutableBookings.add((String) booking.getData());
//                    }
//                    adapter.notifyDataSetChanged();
//                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                currentMonth.setText(formatMonth(firstDayOfNewMonth));
            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[] {
                "Test1",
                "Test2",
                "Test3",
        });
        watcher.setAdapter(adapter);

    }

}
