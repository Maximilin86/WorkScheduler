package me.maxpro.workscheduler;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.maxpro.workscheduler.client.ClientUtils;
import me.maxpro.workscheduler.client.data.MonthData;
import me.maxpro.workscheduler.client.data.Order;
import me.maxpro.workscheduler.client.data.UsersData;
import me.maxpro.workscheduler.databinding.ActivityCalendarBinding;
import me.maxpro.workscheduler.ui.calendar.CalendarAdminFragment;
import me.maxpro.workscheduler.ui.calendar.CalendarBlockedFragment;
import me.maxpro.workscheduler.ui.calendar.CalendarUserFragment;
import me.maxpro.workscheduler.ui.calendar.wrap.CustomCalendarWidget;
import me.maxpro.workscheduler.utils.RuLang;
import me.maxpro.workscheduler.client.WSClient;
import me.maxpro.workscheduler.utils.WSSession;


public class CalendarActivity extends AppCompatActivity {

    private ActivityCalendarBinding binding;
    private CustomCalendarWidget customCalendarWidget;
    private Date selectedDate;
    private final Map<Integer, MonthData> loadedMonths = new HashMap<>();



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Календарь");
        }

//        LoaderManager.getInstance(this).restartLoader(0, null, mLoaderCallbacks);

        customCalendarWidget = new CustomCalendarWidget(binding.compactcalendarView);

        Date viewDate = new Date();
        if(getIntent().getBooleanExtra("start-with-next-month", false)) {
            viewDate.setMonth(viewDate.getMonth() + 1);
        }
        selectedDate = viewDate;
        binding.compactcalendarView.setCurrentDate(viewDate);


        requestLoadInitial(viewDate);

        // Показывание текущего месяца
        binding.currentMonth.setText(RuLang.formatMonthYear(binding.compactcalendarView.getFirstDayOfCurrentMonth()));
        customCalendarWidget.onDayChanged(date -> binding.currentMonth.setText(RuLang.formatMonthYear(date)));

        // показывание выбранного дня
        binding.currentDay.setText(RuLang.formatDayMonth(new Date()));
        customCalendarWidget.onDayChanged(date -> {
            selectedDate = date;
            if(loadedMonths.containsKey(calcMonths(date))) {  // prevent change dates while data loading
                updateControlFragment(date);
            }
        });
        customCalendarWidget.onMonthChanged(date -> {
            if(!loadedMonths.containsKey(calcMonths(date))) {
                removeControlFragment();
                requestLoadDataForMonth(date);
            }
        });
    }

    private void setEnabledScene(boolean value) {

    }

    private void updateMonthData(Date date, MonthData data) {
        for (Map.Entry<Integer, List<Order>> entry : data.ordersByDay.entrySet()) {
            int dayOfMonth = entry.getKey();  // [1:31]
            date.setDate(dayOfMonth);
            customCalendarWidget.addEvent(date, Color.GREEN, "Event");
        }
        if(compareMonth(date, selectedDate) != 0) return;
        loadedMonths.put(calcMonths(date), data);
        updateControlFragment(selectedDate);
    }

    private void updateUsers(UsersData data) {
        WSSession.getInstance().users = data;
        updateControlFragment(selectedDate);
    }
    private void requestLoadInitial(Date viewDate) {
        setEnabledScene(false);
        WSSession session = WSSession.getInstance();
        WSClient.loadUsers(session.token)
                .thenCompose(usersData -> {
                    return WSClient.getMonthData(WSSession.getInstance().token, viewDate)
                            .thenApply(monthData -> Pair.create(usersData, monthData));
                })
                .whenCompleteAsync((pair, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        this.updateUsers(pair.first);
                        this.updateMonthData(viewDate, pair.second);
                    } else {  // при ошибке
                        ClientUtils.showNetworkError(this, throwable, this::finish);
                    }
                }, WSClient.MAIN);
    }

    private void requestLoadDataForMonth(Date viewDate) {
        setEnabledScene(false);
        WSClient.getMonthData(WSSession.getInstance().token, viewDate)
                .whenCompleteAsync((monthData, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        this.updateMonthData(viewDate, monthData);
                    } else {  // при ошибке
                        ClientUtils.showNetworkError(this, throwable, this::finish);
                    }
                }, WSClient.MAIN);
    }

    private void requestAutoFill() {
        setEnabledScene(false);
        Date date = selectedDate;
        WSClient.autifill(WSSession.getInstance().token, date)
                .whenCompleteAsync((monthData, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        this.updateMonthData(date, monthData);
                    } else {  // при ошибке
                        ClientUtils.showNetworkError(this, throwable, this::finish);
                    }
                }, WSClient.MAIN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (itemId == R.id.auto_fill) {
            requestAutoFill();
            Toast.makeText(this, "Clicked Menu 1", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeControlFragment() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_placeholder, CalendarBlockedFragment.class, new Bundle())
                .commit();
    }
    private int calcMonths(Date date) {
        return date.getYear() * 12 + date.getMonth();
    }
    private int compareMonth(Date left, Date right) {
        return calcMonths(left) - calcMonths(right);
    }
    private void updateControlFragment(Date date) {
        binding.currentDay.setText(RuLang.formatDayMonth(date));
        boolean isMonthBlocked = compareMonth(date, new Date()) <= 0;  // viewMonth <= nowMonth
        if(isMonthBlocked) {
            if(getIntent().getBooleanExtra("can-edit-since-now", false)) {
                isMonthBlocked = compareMonth(date, new Date()) < 0;
            }
        }

        if (isMonthBlocked) {
            // добавить блокирующий фрагмент
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_placeholder, CalendarBlockedFragment.class, new Bundle())
                    .commit();
        } else {
            // добавить фрагмент с контролем
            Bundle args = new Bundle();
            args.putLong("date", date.getTime());
            WSSession session = WSSession.getInstance();
            Class<? extends Fragment> fragmentClass =
                    session.role == WSSession.Role.USER ?
                            CalendarUserFragment.class :
                            CalendarAdminFragment.class;
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_placeholder, fragmentClass, args)
                    .commit();
        }
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public List<Order> getOrders(Date date) {
        MonthData data = loadedMonths.get(calcMonths(date));
        if(data == null) return Collections.emptyList();
        int dayOfMonth = date.getDate();  // [1:31]
        List<Order> orders = data.ordersByDay.get(dayOfMonth);
        if(orders == null) return Collections.emptyList();
        return orders;
    }

    public void updateOrders(Date date, List<Order> orders) {
        MonthData data = loadedMonths.get(calcMonths(date));
        if(data == null) {
            data = new MonthData();
            loadedMonths.put(calcMonths(date), data);
        }
        int dayOfMonth = date.getDate();  // [1:31]
        data.ordersByDay.put(dayOfMonth, orders);
    }
}
