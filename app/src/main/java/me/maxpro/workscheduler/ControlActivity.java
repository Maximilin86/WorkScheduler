package me.maxpro.workscheduler;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import me.maxpro.workscheduler.client.data.Desire;
import me.maxpro.workscheduler.client.data.DesiresData;
import me.maxpro.workscheduler.client.data.OrdersData;
import me.maxpro.workscheduler.client.data.Order;
import me.maxpro.workscheduler.client.data.UsersData;
import me.maxpro.workscheduler.databinding.ActivityControlBinding;
import me.maxpro.workscheduler.ui.control.ControlAdminFragment;
import me.maxpro.workscheduler.ui.control.ControlBlockedFragment;
import me.maxpro.workscheduler.ui.control.ControlUserFragment;
import me.maxpro.workscheduler.ui.control.wrap.CustomCalendarWidget;
import me.maxpro.workscheduler.utils.RuLang;
import me.maxpro.workscheduler.client.WSClient;
import me.maxpro.workscheduler.utils.WSSession;


public class ControlActivity extends AppCompatActivity {

    private ActivityControlBinding binding;
    private CustomCalendarWidget customCalendarWidget;
    private final Map<Integer, OrdersData> cachedByMonth = new HashMap<>();
    private final Map<Integer, DesiresData> cachedMonthDesires = new HashMap<>();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityControlBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Управление сменами");
        }

//        LoaderManager.getInstance(this).restartLoader(0, null, mLoaderCallbacks);

        Date viewDate = new Date();
        if(getIntent().getBooleanExtra("start-with-next-month", false)) {
            viewDate.setMonth(viewDate.getMonth() + 1);
        }
        customCalendarWidget = new CustomCalendarWidget(binding.compactcalendarView, viewDate);

        WSSession session = WSSession.getInstance();
        if (session.role == WSSession.Role.ADMIN) {
            requestAdminLoadInitial(viewDate);
        } else {
            requestUserLoadData(viewDate);
        }

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
                removeControlFragment();
                if (session.role == WSSession.Role.ADMIN) {
                    requestLoadDataForMonth(date);
                } else {
                    requestUserLoadData(date);
                }
            }
        });
    }

    private void setEnabledScene(boolean value) {

    }

    public void renderDayOrders(Date day, List<Order> orders) {
        boolean hasAllDay = false;
        boolean hasWork8h = false;
        for (Order order : orders) {
            if (order.order == Order.Type.ALL_DAY) {
                hasAllDay = true;
            }
            if (order.order == Order.Type.WORK) {
                hasWork8h = true;
            }
        }
        customCalendarWidget.view.removeEvents(day);
        if(!hasAllDay && !hasWork8h) return;
        WSSession session = WSSession.getInstance();
        if (session.role == WSSession.Role.USER) {
            if(hasAllDay) {
                customCalendarWidget.addEvent(day, Color.GREEN, "Event");
                return;
            }
            if(hasWork8h) {
                customCalendarWidget.addEvent(day, Color.YELLOW, "Event");
                return;
            }
            return;
        }
        if(hasAllDay && hasWork8h) {
            customCalendarWidget.addEvent(day, Color.GREEN, "Event");
            return;
        }
        customCalendarWidget.addEvent(day, Color.YELLOW, "Event");
    }
    private void updateDesiresData(Date date, DesiresData data) {
        cachedMonthDesires.put(calcEpochMonthNumber(date), data);
        if(compareMonth(date, customCalendarWidget.getSelectedDate()) != 0) return;
    }
    private void updateOrdersData(Date date, OrdersData data) {
        cachedByMonth.put(calcEpochMonthNumber(date), data);
        if(compareMonth(date, customCalendarWidget.getSelectedDate()) != 0) return;  // prevent visual bugs
        for (int dayOfMonth = 1; dayOfMonth <= 31; dayOfMonth++) {
            Date tmp = new Date(date.getTime());
            tmp.setDate(dayOfMonth);
            if(tmp.getMonth() != date.getMonth()) break;
            List<Order> orders = data.ordersByDay.getOrDefault(dayOfMonth, Collections.emptyList());
            renderDayOrders(tmp, orders);
        }
        updateControlFragment();
    }
    public void updateOrders(Date date, List<Order> orders) {
        OrdersData data = cachedByMonth.get(calcEpochMonthNumber(date));
        if(data == null) {
            data = new OrdersData();
            cachedByMonth.put(calcEpochMonthNumber(date), data);
        }
        int dayOfMonth = date.getDate();  // [1:31]
        data.ordersByDay.put(dayOfMonth, orders);
        renderDayOrders(date, orders);
    }

    private void updateUsers(UsersData data) {
        WSSession.getInstance().users = data;
        updateControlFragment();
    }
    private void requestUserLoadData(Date viewDate) {
        setEnabledScene(false);
        WSSession session = WSSession.getInstance();
        WSClient.getDesireData(session.token, viewDate)
                .thenCompose(desiresData -> {
                    return WSClient.getMonthData(WSSession.getInstance().token, viewDate)
                            .thenApply(ordersData -> Pair.create(desiresData, ordersData));
                })
                .whenCompleteAsync((pair, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        this.updateDesiresData(viewDate, pair.first);
                        this.updateOrdersData(viewDate, pair.second);
                    } else {  // при ошибке
                        ClientUtils.showNetworkError(this, throwable, this::finish);
                    }
                }, WSClient.MAIN);
    }
    private void requestAdminLoadInitial(Date viewDate) {
        setEnabledScene(false);
        WSSession session = WSSession.getInstance();
        WSClient.loadUsers(session.token)
                .thenCompose(usersData -> {
                    return WSClient.getMonthData(WSSession.getInstance().token, viewDate)
                            .thenApply(ordersData -> Pair.create(usersData, ordersData));
                })
                .whenCompleteAsync((pair, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        this.updateUsers(pair.first);
                        this.updateOrdersData(viewDate, pair.second);
                    } else {  // при ошибке
                        ClientUtils.showNetworkError(this, throwable, this::finish);
                    }
                }, WSClient.MAIN);
    }

    private void requestLoadDataForMonth(Date viewDate) {
        setEnabledScene(false);
        WSClient.getMonthData(WSSession.getInstance().token, viewDate)
                .whenCompleteAsync((ordersData, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        this.updateOrdersData(viewDate, ordersData);
                    } else {  // при ошибке
                        ClientUtils.showNetworkError(this, throwable, this::finish);
                    }
                }, WSClient.MAIN);
    }

    private void requestAutoFill() {
        setEnabledScene(false);
        Date date = customCalendarWidget.getSelectedDate();
        WSClient.autifill(WSSession.getInstance().token, date)
                .whenCompleteAsync((ordersData, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        this.updateOrdersData(date, ordersData);
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
                .replace(R.id.fragment_placeholder, ControlBlockedFragment.class, new Bundle())
                .commit();
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
                    .replace(R.id.fragment_placeholder, ControlBlockedFragment.class, new Bundle())
                    .commit();
        } else {
            // добавить фрагмент с контролем
            Bundle args = new Bundle();
            args.putLong("date", date.getTime());
            WSSession session = WSSession.getInstance();
            Class<? extends Fragment> fragmentClass =
                    session.role == WSSession.Role.USER ?
                            ControlUserFragment.class :
                            ControlAdminFragment.class;
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_placeholder, fragmentClass, args)
                    .commit();
        }
    }

    public List<Order> getOrders(Date date) {
        OrdersData data = cachedByMonth.get(calcEpochMonthNumber(date));
        if(data == null) return Collections.emptyList();
        int dayOfMonth = date.getDate();  // [1:31]
        List<Order> orders = data.ordersByDay.get(dayOfMonth);
        if(orders == null) return Collections.emptyList();
        return orders;
    }

    @Nullable
    public Desire getDesire(Date date) {
        DesiresData data = cachedMonthDesires.get(calcEpochMonthNumber(date));
        if(data == null) return null;
        int dayOfMonth = date.getDate();  // [1:31]
        return data.desiresByDay.get(dayOfMonth);
    }

    public void setDesire(Date date, Desire desire) {
        DesiresData data = cachedMonthDesires.get(calcEpochMonthNumber(date));
        if(data == null) return;
        int dayOfMonth = date.getDate();  // [1:31]
        data.desiresByDay.put(dayOfMonth, desire);
    }

}
