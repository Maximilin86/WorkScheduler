package me.maxpro.workscheduler;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import me.maxpro.workscheduler.databinding.ActivityCalendarBinding;
import me.maxpro.workscheduler.ui.calendar.CalendarAdminFragment;
import me.maxpro.workscheduler.ui.calendar.CalendarBlockedFragment;
import me.maxpro.workscheduler.ui.calendar.CalendarFragment;
import me.maxpro.workscheduler.ui.calendar.CalendarUserFragment;
import me.maxpro.workscheduler.ui.calendar.wrap.CustomCalendarWidget;
import me.maxpro.workscheduler.utils.RuLang;
import me.maxpro.workscheduler.utils.WSSession;


public class CalendarActivity extends AppCompatActivity {

    private static final String CALENDAR_FRAGMENT = "CALENDAR_FRAGMENT";

    private ActivityCalendarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        CustomCalendarWidget customCalendarWidget = new CustomCalendarWidget(binding.compactcalendarView);

        Date viewDate = new Date();
        if(getIntent().getBooleanExtra("start-with-next-month", false)) {
            viewDate.setMonth(viewDate.getMonth() + 1);
        }
        binding.compactcalendarView.setCurrentDate(viewDate);

        // Показывание текущего месяца
        binding.currentMonth.setText(RuLang.formatMonthYear(binding.compactcalendarView.getFirstDayOfCurrentMonth()));
        customCalendarWidget.onDayChanged(date -> binding.currentMonth.setText(RuLang.formatMonthYear(date)));

        // показывание выбранного дня
        binding.currentDay.setText(RuLang.formatDayMonth(new Date()));
        customCalendarWidget.onDayChanged(date -> binding.currentDay.setText(RuLang.formatDayMonth(date)));

        updateControlFragment(customCalendarWidget.view.getFirstDayOfCurrentMonth());

        customCalendarWidget.onDayChanged(date -> {
            CalendarFragment calendarFragment = (CalendarFragment) getSupportFragmentManager().findFragmentByTag(CALENDAR_FRAGMENT);
            if (calendarFragment != null) {
                calendarFragment.onSelectDate(date);
            }
            if(getIntent().getBooleanExtra("can-edit-since-now", false)) {
                updateControlFragment(date);
            }
        });
        customCalendarWidget.onMonthChanged(date -> {
            updateControlFragment(date);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateControlFragment(Date date) {
        boolean isDayBlocked;
        if(getIntent().getBooleanExtra("can-edit-since-now", false)) {
            Date now = new Date();
            Instant dateInstant = date.toInstant()
                    .truncatedTo(ChronoUnit.DAYS);
            Instant nowInstant = now.toInstant()
                    .truncatedTo(ChronoUnit.DAYS);
            Log.d("Test", "dateInstant: " + dateInstant);
            Log.d("Test", "nowInstant: " + nowInstant);
            isDayBlocked = dateInstant.isBefore(nowInstant);
        } else {
            int viewMonth = date.getYear() * 12 + date.getMonth();
            Date now = new Date();
            int nowMonth = now.getYear() * 12 + now.getMonth();
            isDayBlocked = viewMonth <= nowMonth;
        }

        if (isDayBlocked) {
            // добавить блокирующий фрагмент
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_placeholder, CalendarBlockedFragment.class, new Bundle())
                    .commit();
        } else {
            // добавить фрагмент с контролем
            WSSession session = WSSession.getInstance();
            Class<? extends Fragment> fragmentClass =
                    session.role == WSSession.Role.USER ?
                            CalendarUserFragment.class :
                            CalendarAdminFragment.class;
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_placeholder, fragmentClass, new Bundle())
                    .commit();
        }
    }

}
