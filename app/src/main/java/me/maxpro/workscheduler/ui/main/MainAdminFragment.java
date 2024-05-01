package me.maxpro.workscheduler.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;

import me.maxpro.workscheduler.CalendarActivity;
import me.maxpro.workscheduler.R;
import me.maxpro.workscheduler.databinding.FragmentMainAdminBinding;
import me.maxpro.workscheduler.databinding.FragmentMainUserBinding;
import me.maxpro.workscheduler.utils.RuLang;

public class MainAdminFragment extends Fragment {

    private FragmentMainAdminBinding binding;

    public MainAdminFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Date now = new Date();
        now.setMonth(now.getMonth() + 1);
        binding.editTitle.setText("Управление сменами на " + RuLang.formatMonthYear(now));
        binding.smenaAuto.setOnClickListener(view_ -> {

        });
        binding.smenaEdit.setOnClickListener(view_ -> {
            openCalendarActivity();
        });
    }

    private void openCalendarActivity() {
        Intent a = new Intent(getContext(), CalendarActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        a.putExtra("start-with-next-month", true);
        a.putExtra("can-edit-since-now", true);
        startActivity(a);
    }

}