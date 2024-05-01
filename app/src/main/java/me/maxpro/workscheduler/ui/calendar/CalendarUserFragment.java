package me.maxpro.workscheduler.ui.calendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import me.maxpro.workscheduler.utils.WSClient;
import me.maxpro.workscheduler.utils.WSSession;
import me.maxpro.workscheduler.databinding.FragmentCalendarUserBinding;
import me.maxpro.workscheduler.ui.calendar.wrap.SelectElementWidget;

public class CalendarUserFragment extends Fragment implements CalendarFragment {

    private FragmentCalendarUserBinding binding;

    public CalendarUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.bringToFront();

        CalendarViewModel dashboardViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        SelectElementWidget day_desire = new SelectElementWidget(binding.dayDesire);
        day_desire.setItems(
                Pair.create("", "Нет"),
                Pair.create("work", "Смена"),
                Pair.create("rest", "Выходной"),
                Pair.create("work_8h", "В день")
        );
        day_desire.onChangeValue((oldValue, itemId) -> {
            day_desire.view.setEnabled(false);
            WSSession session = WSSession.getInstance();
            WSClient.setDesire(session.token, itemId)
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