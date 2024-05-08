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
import android.widget.ArrayAdapter;

import java.util.Date;

import me.maxpro.workscheduler.CalendarActivity;
import me.maxpro.workscheduler.client.ClientUtils;
import me.maxpro.workscheduler.client.WSClient;
import me.maxpro.workscheduler.utils.WSSession;
import me.maxpro.workscheduler.databinding.FragmentCalendarUserBinding;
import me.maxpro.workscheduler.ui.calendar.wrap.SelectElementWidget;

public class CalendarUserFragment extends Fragment implements CalendarFragment {

    private FragmentCalendarUserBinding binding;
    private Date date;

    public CalendarUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCalendarUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        date = new Date(getArguments().getLong("date"));

        CalendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        SelectElementWidget day_desire = new SelectElementWidget(binding.dayDesire);
        day_desire.setItems(
                Pair.create("", "Нет"),
                Pair.create("all_day", "Смена"),
                Pair.create("work", "В день"),
                Pair.create("rest", "Выходной")
        );
        day_desire.onChangeValue((oldValue, itemId) -> {
            day_desire.view.setEnabled(false);
            WSSession session = WSSession.getInstance();
            Date selectedDate = ((CalendarActivity) getActivity()).getSelectedDate();
            WSClient.setDesire(session.token, selectedDate, itemId, "")
                    .whenCompleteAsync((s, throwable) -> day_desire.view.setEnabled(true), WSClient.MAIN)  // в любом случае
                    .handleAsync((unused, throwable) -> {  // при ошибке
                        day_desire.selectItemIdSilent(oldValue);  // возвращаем старое значение
                        ClientUtils.showNetworkError(view.getContext(), throwable, () -> {});
                        return null;
                    }, WSClient.MAIN);
        });
    }

}