package me.maxpro.workscheduler.ui.calendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import me.maxpro.workscheduler.CalendarActivity;
import me.maxpro.workscheduler.R;
import me.maxpro.workscheduler.WSClient;
import me.maxpro.workscheduler.ui.calendar.wrap.SelectElementWidget;
import me.maxpro.workscheduler.ui.dashboard.DashboardViewModel;

public class CalendarUserFragment extends Fragment implements CalendarFragment {

    private static final String ARG_TOKEN = "token";

    private String token;

    private SelectElementWidget day_desire;

    public CalendarUserFragment() {
        // Required empty public constructor
    }

    public static CalendarUserFragment newInstance(String token) {
        CalendarUserFragment fragment = new CalendarUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TOKEN, token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString(ARG_TOKEN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.bringToFront();

        CalendarViewModel dashboardViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        day_desire = new SelectElementWidget(view.findViewById(R.id.day_desire));
        day_desire.setItems(
                Pair.create("", "Нет"),
                Pair.create("work", "Смена"),
                Pair.create("rest", "Выходной"),
                Pair.create("work_8h", "В день")
        );
        day_desire.onChangeValue((oldValue, itemId) -> {
            day_desire.view.setEnabled(false);
            WSClient.setDesire(token, itemId)
                    .whenCompleteAsync((s, throwable) -> day_desire.view.setEnabled(true), WSClient.MAIN)  // в любом случае
                    .handleAsync((unused, throwable) -> {  // при ошибке
                        day_desire.selectItemIdSilent(oldValue);  // возвращаем старое значение
                        WSClient.showNetworkError(view.getContext(), throwable);
                        return null;
                    }, WSClient.MAIN);
        });
    }

    @Override
    public void onSelectDate(Date date) {

    }

}