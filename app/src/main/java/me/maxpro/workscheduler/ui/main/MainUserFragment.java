package me.maxpro.workscheduler.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.maxpro.workscheduler.CalendarActivity;
import me.maxpro.workscheduler.MainActivity;
import me.maxpro.workscheduler.R;
import me.maxpro.workscheduler.databinding.FragmentMainUserBinding;

public class MainUserFragment extends Fragment {

    private FragmentMainUserBinding binding;

    public MainUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.calendarButton.setOnClickListener(view_ -> {
            openCalendarActivity();
        });
    }

    private void openCalendarActivity() {
        Intent a = new Intent(getContext(), CalendarActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(a);
    }

}