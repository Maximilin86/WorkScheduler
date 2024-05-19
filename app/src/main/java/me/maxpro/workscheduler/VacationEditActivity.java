package me.maxpro.workscheduler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import me.maxpro.workscheduler.client.ClientUtils;
import me.maxpro.workscheduler.client.WSClient;
import me.maxpro.workscheduler.client.data.User;
import me.maxpro.workscheduler.client.data.Vacation;
import me.maxpro.workscheduler.client.data.VacationData;
import me.maxpro.workscheduler.databinding.ActivityVacationEditBinding;
import me.maxpro.workscheduler.ui.holidays.HolidaysAdminFragment;
import me.maxpro.workscheduler.utils.WSSession;

public class VacationEditActivity extends AppCompatActivity {

    private ActivityVacationEditBinding binding;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVacationEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Редактирование отпуска");
        }

        Intent intent = getIntent();
        int userId = intent.getIntExtra("user_id", -1);
        if(userId == -1) {
            finish();
            return;
        }
        WSSession session = WSSession.getInstance();
        user = session.users.byId.get(userId);
        if(user == null) {
            finish();
            return;
        }
        binding.nameText.setText(user.getFullName());

        Vacation vacation = null;
        int vacationId = intent.getIntExtra("vacation_id", -1);
        if(vacationId == -1) {
            if (actionBar != null) {
                actionBar.setTitle("Создание отпуска");
            }
            binding.deleteButton.setVisibility(View.INVISIBLE);
        } else {
            VacationData data = session.vacationsByUser.get(user.id);
            if(data != null) {
                vacation = data.byId.get(vacationId);
            }
        }
        if(vacation == null) vacation = new Vacation(-1, new Date(), new Date(), Vacation.Type.VACATION, "");

        binding.calendar.setWeekOffset(1);
        Calendar from = new GregorianCalendar();
        from.setTime(vacation.from);
        Calendar to = new GregorianCalendar();
        to.setTime(vacation.to);
        binding.calendar.setSelectedDateRange(from, to);


        List<Vacation.Type> items = Arrays.asList(Vacation.Type.values());

        ArrayAdapter<Vacation.Type> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                items);
        binding.recordType.setAdapter(arrayAdapter);

        ArrayAdapter<Vacation.Type> adapter = (ArrayAdapter<Vacation.Type>) binding.recordType.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Vacation.Type item = adapter.getItem(i);
            if(item == null || !Objects.equals(item, vacation.type)) continue;
            binding.recordType.setSelection(i);
            break;
        }

        binding.saveButton.setOnClickListener(v -> {
            saveVacation();
        });
        binding.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Подтверждение")
                    .setMessage("Удалить запись?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteVacation())
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

    }

    private void setEnabledScene(boolean value) {

    }

    private void saveVacation() {
//        setEnabledScene(false);
//        WSClient.saveUser(WSSession.getInstance().token, user, password)
//                .whenCompleteAsync((usersData, throwable) -> {
//                    setEnabledScene(true);
//                    if(throwable == null) {  // при успехе
//                        WSSession.getInstance().users = usersData;
//                        setResult(Activity.RESULT_OK, new Intent());
//                        finish();
//                    } else {  // при ошибке
//                        ClientUtils.showNetworkError(this, throwable, this::finish);
//                    }
//                }, WSClient.MAIN);
    }

    private void deleteVacation() {
//        Intent intent = getIntent();
//        int userId = intent.getIntExtra("user_id", -1);
//        setEnabledScene(false);
//        WSClient.deleteUser(WSSession.getInstance().token, userId)
//                .whenCompleteAsync((usersData, throwable) -> {
//                    setEnabledScene(true);
//                    if(throwable == null) {  // при успехе
//                        WSSession.getInstance().users = usersData;
//                        finish();
//                    } else {  // при ошибке
//                        ClientUtils.showNetworkError(this, throwable, this::finish);
//                    }
//                }, WSClient.MAIN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        setResult(Activity.RESULT_OK, new Intent());
        super.finish();
    }

}