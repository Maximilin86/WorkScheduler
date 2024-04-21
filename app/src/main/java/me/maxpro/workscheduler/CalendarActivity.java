package me.maxpro.workscheduler;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

import me.maxpro.workscheduler.ui.calendar.CalendarFragment;
import me.maxpro.workscheduler.ui.calendar.CalendarUserFragment;
import me.maxpro.workscheduler.ui.calendar.wrap.CustomCalendarWidget;


public class CalendarActivity extends AppCompatActivity {

    private CustomCalendarWidget calendarView;
    private TextView currentMonth;
    private TextView currentDay;

    private String formatMonth(Date dateClicked) {
        String[] names = new String[] {
                "Январь", "Февраль",
                "Март", "Апрель", "Май",
                "Июнь", "Июль", "Август",
                "Сентябрь", "Октябрь", "Ноябрь",
                "Декабрь",
        };
        return names[dateClicked.getMonth()] + " " + Integer.toString(dateClicked.getYear() + 1900);
    }
    private String formatDay(Date date) {
        String[] names = new String[] {
                "Января", "Февраля",
                "Марта", "Апреля", "Мая",
                "Июня", "Июля", "Августа",
                "Сентября", "Октября", "Ноября",
                "Декабря",
        };
        CharSequence day = DateFormat.format("dd", date);
        return day + " " + names[date.getMonth()];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        calendarView = new CustomCalendarWidget(findViewById(R.id.compactcalendar_view));
        currentMonth = findViewById(R.id.current_month);
        currentDay = findViewById(R.id.current_day);

        // Показывание текущего месяца
        currentMonth.setText(formatMonth(calendarView.getFirstDayOfCurrentMonth()));
        calendarView.onDayChanged(date -> currentMonth.setText(formatMonth(date)));

        // показывание выбранного дня
        currentDay.setText(formatDay(new Date()));
        calendarView.onDayChanged(date -> currentDay.setText(formatDay(date)));


        Bundle bundle = new Bundle();
        bundle.putString("token", "");


        final String CALENDAR_FRAGMENT = "CALENDAR_FRAGMENT";
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_placeholder, CalendarUserFragment.class, bundle, CALENDAR_FRAGMENT)
//                .add(R.id.fragment_placeholder, CalendarAdminFragment.class, bundle)
                .commit();

        calendarView.onDayChanged(date -> {
            CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentByTag(CALENDAR_FRAGMENT);
            if (calendarFragment != null) {
                calendarFragment.onSelectDate(date);
            }
        });
    }

    public CustomCalendarWidget getCalendarWidget() {
        return calendarView;
    }
}
