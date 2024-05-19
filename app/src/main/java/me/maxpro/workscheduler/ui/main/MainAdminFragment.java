package me.maxpro.workscheduler.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.maxpro.workscheduler.ControlActivity;
import me.maxpro.workscheduler.HolidaysActivity;
import me.maxpro.workscheduler.SelectUserListActivity;
import me.maxpro.workscheduler.UserListActivity;
import me.maxpro.workscheduler.VacationUserListActivity;
import me.maxpro.workscheduler.databinding.FragmentMainAdminBinding;
import me.maxpro.workscheduler.utils.WSSession;

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

        binding.usersButton.setOnClickListener(view_ -> {
            openUserListActivity();
        });
        binding.holidaysButton.setOnClickListener(view_ -> {
            openHolidaysActivity();
        });
        binding.vacationsButton.setOnClickListener(view_ -> {
            openVacationsActivity();
        });

//        binding.editTitle.setText("Управление сменами на " + RuLang.formatMonthYear(now));
    }

    private void openUserListActivity() {
        Intent a = new Intent(getContext(), UserListActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        a.putExtra("reload-users", true);
        startActivity(a);
    }

    private void openHolidaysActivity() {
        WSSession session = WSSession.getInstance();
        Intent a = new Intent(getContext(), HolidaysActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        if(session.role == WSSession.Role.USER) {
            a.putExtra("start-with-next-month", true);
        } else {
            a.putExtra("can-edit-since-now", true);
        }
        startActivity(a);
    }

    private void openVacationsActivity() {
        Intent a = new Intent(getContext(), VacationUserListActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        a.putExtra("reload-users", true);
        startActivity(a);
    }

}












