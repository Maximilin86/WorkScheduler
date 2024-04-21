package me.maxpro.workscheduler.ui.calendar;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;

import me.maxpro.workscheduler.R;
import me.maxpro.workscheduler.WSClient;
import me.maxpro.workscheduler.ui.calendar.wrap.SelectElementWidget;

public class CalendarBlockedFragment extends Fragment implements CalendarFragment {






    public CalendarBlockedFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_blocked, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.bringToFront();
    }

    @Override
    public void onSelectDate(Date date) {

    }

}