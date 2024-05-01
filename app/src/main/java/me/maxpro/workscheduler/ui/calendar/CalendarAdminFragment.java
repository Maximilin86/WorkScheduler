package me.maxpro.workscheduler.ui.calendar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Date;

import me.maxpro.workscheduler.R;
import me.maxpro.workscheduler.databinding.FragmentCalendarAdminBinding;
import me.maxpro.workscheduler.databinding.FragmentMainAdminBinding;

public class CalendarAdminFragment extends Fragment implements CalendarFragment {

    private FragmentCalendarAdminBinding binding;

    public CalendarAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, new String[] {
                "Test1",
                "Test2",
                "Test3",
        });
        binding.dayDesire.setAdapter(adapter);

    }

    @Override
    public void onSelectDate(Date date) {

    }

}