package me.maxpro.workscheduler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.maxpro.workscheduler.client.data.Holiday;
import me.maxpro.workscheduler.client.data.HolidaysData;
import me.maxpro.workscheduler.client.data.Order;
import me.maxpro.workscheduler.client.data.OrdersData;
import me.maxpro.workscheduler.databinding.ActivityHolidaysBinding;
import me.maxpro.workscheduler.ui.control.ControlBlockedFragment;
import me.maxpro.workscheduler.ui.control.wrap.CustomCalendarWidget;
import me.maxpro.workscheduler.ui.holidays.HolidaysAdminFragment;
import me.maxpro.workscheduler.utils.RuLang;
import me.maxpro.workscheduler.utils.WSSession;

public class HolidaysActivity extends AppCompatActivity {

    private ActivityHolidaysBinding binding;
    private CustomCalendarWidget customCalendarWidget;
    private final Map<Integer, HolidaysData> cachedByMonth = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHolidaysBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Выходные дни");
        }

        Date viewDate = new Date();
        if(getIntent().getBooleanExtra("start-with-next-month", false)) {
            viewDate.setMonth(viewDate.getMonth() + 1);
        }
        customCalendarWidget = new CustomCalendarWidget(binding.compactcalendarView, viewDate);
        requestLoadDataForMonth(viewDate);


        // Показывание текущего месяца
        binding.currentMonth.setText(RuLang.formatMonthYear(binding.compactcalendarView.getFirstDayOfCurrentMonth()));
        customCalendarWidget.onDayChanged(date -> binding.currentMonth.setText(RuLang.formatMonthYear(date)));

        // показывание выбранного дня
        binding.currentDay.setText(RuLang.formatDayMonth(new Date()));
        customCalendarWidget.onDayChanged(date -> {
            if(cachedByMonth.containsKey(calcEpochMonthNumber(date))) {  // prevent change dates while data loading
                updateControlFragment();
            }
        });
        customCalendarWidget.onMonthChanged(date -> {
            if(!cachedByMonth.containsKey(calcEpochMonthNumber(date))) {
//                removeControlFragment();
//                if (session.role == WSSession.Role.ADMIN) {
//                    requestLoadDataForMonth(date);
//                } else {
//                    requestUserLoadData(date);
//                }
            }
            requestLoadDataForMonth(date);
        });
    }

    private void setEnabledScene(boolean value) {

    }

    public static boolean isWorkDay(Date day, @Nullable Holiday holiday) {
        boolean isWorkDay;
        if(holiday != null) {
            isWorkDay = holiday.isWorkDay;
        } else {
            int dayOfWeek = RuLang.getRuDayOfWeek(day);
            isWorkDay = dayOfWeek < 5;  // [5, 6] = [сб, вс]
        }
        return isWorkDay;
    }

    public void renderDayHoliday(Date day, @Nullable Holiday holiday) {
        boolean isWorkDay = isWorkDay(day, holiday);
        customCalendarWidget.view.removeEvents(day);
        if(!isWorkDay) {
            customCalendarWidget.addEvent(day, Color.GREEN, "Event");
        }
    }

    private void updateHolidaysData(Date date, HolidaysData data) {
        cachedByMonth.put(calcEpochMonthNumber(date), data);
        if(compareMonth(date, customCalendarWidget.getSelectedDate()) != 0) return;  // prevent visual bugs
        for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
            Date tmp = new Date(date.getTime());
            tmp.setDate(dayOfMonth);
            if(tmp.getMonth() != date.getMonth()) break;
            Holiday holiday = data.byDay.get(dayOfMonth);
            renderDayHoliday(tmp, holiday);
        }
        updateControlFragment();
    }

    private void requestLoadDataForMonth(Date viewDate) {
        this.updateHolidaysData(viewDate, new HolidaysData());  // todo: delete me
//        setEnabledScene(false);
//        WSClient.getMonthData(WSSession.getInstance().token, viewDate)
//                .whenCompleteAsync((ordersData, throwable) -> {
//                    setEnabledScene(true);
//                    if(throwable == null) {  // при успехе
//                        this.updateHolidaysData(viewDate, ordersData);
//                    } else {  // при ошибке
//                        ClientUtils.showNetworkError(this, throwable, this::finish);
//                    }
//                }, WSClient.MAIN);
    }


    private int calcEpochMonthNumber(Date date) {
        return date.getYear() * 12 + date.getMonth();
    }
    private int compareMonth(Date left, Date right) {
        return calcEpochMonthNumber(left) - calcEpochMonthNumber(right);
    }
    private void updateControlFragment() {
        Date date = customCalendarWidget.getSelectedDate();
        binding.currentDay.setText(RuLang.formatDayMonth(date));
//        boolean isMonthBlocked = compareMonth(date, new Date()) <= 0;  // viewMonth <= nowMonth
//        if(isMonthBlocked) {
//            if(getIntent().getBooleanExtra("can-edit-since-now", false)) {
//                isMonthBlocked = compareMonth(date, new Date()) < 0;
//            }
//        }
//
//        if (isMonthBlocked) {
//            // добавить блокирующий фрагмент
//            getSupportFragmentManager().beginTransaction()
//                    .setReorderingAllowed(true)
//                    .replace(R.id.fragment_placeholder, ControlBlockedFragment.class, new Bundle())
//                    .commit();
//        } else {
//        }
        // добавить фрагмент с контролем
        Bundle args = new Bundle();
        args.putLong("date", date.getTime());
        WSSession session = WSSession.getInstance();
        Class<? extends Fragment> fragmentClass =
                session.role == WSSession.Role.USER ?
                        ControlBlockedFragment.class :
                        HolidaysAdminFragment.class;
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_placeholder, fragmentClass, args)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateDayType(Date date, Holiday holiday) {
        HolidaysData data = cachedByMonth.get(calcEpochMonthNumber(date));
        if(data == null) {
            data = new HolidaysData();
            cachedByMonth.put(calcEpochMonthNumber(date), data);
        }
        int dayOfMonth = date.getDate();  // [1:31]
        data.byDay.put(dayOfMonth, holiday);
        renderDayHoliday(date, holiday);
    }

    @Nullable public Holiday getHoliday(Date date) {
        HolidaysData data = cachedByMonth.get(calcEpochMonthNumber(date));
        if(data == null) return null;
        int dayOfMonth = date.getDate();  // [1:31]
        return data.byDay.get(dayOfMonth);
    }

}