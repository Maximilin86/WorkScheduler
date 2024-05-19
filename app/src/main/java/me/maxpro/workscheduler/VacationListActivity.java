package me.maxpro.workscheduler;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxpro.workscheduler.client.ClientUtils;
import me.maxpro.workscheduler.client.WSClient;
import me.maxpro.workscheduler.client.data.User;
import me.maxpro.workscheduler.client.data.UsersData;
import me.maxpro.workscheduler.client.data.Vacation;
import me.maxpro.workscheduler.client.data.VacationData;
import me.maxpro.workscheduler.databinding.ActivityVacationListBinding;
import me.maxpro.workscheduler.utils.WSSession;

public class VacationListActivity extends AppCompatActivity {

    private ActivityVacationListBinding binding;
    private User user;
    private ActivityResultLauncher<Intent> intentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVacationListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
            @Override
            public void onActivityResult(ActivityResult activityResult) {
                if (activityResult.getResultCode() == Activity.RESULT_OK) {
                    VacationData data = WSSession.getInstance().vacationsByUser.get(user.id);
                    if (data != null) updateVacations(data);
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Отпуски");
        }

        Intent intent = getIntent();
        int userId = intent.getIntExtra("user_id", -1);
        if(userId == -1) {
            finish();
            return;
        }
        user = WSSession.getInstance().users.byId.get(userId);
        if(user == null) {
            finish();
            return;
        }

        if (actionBar != null) {
            actionBar.setTitle(user.getShortName());
        }

        if(intent.getBooleanExtra("reload-vacations", false)) {
            loadVacations();
        } else {
//            VacationData data = WSSession.getInstance().vacationsByUser.get(user.id);
//            if (data != null) updateVacations(data);
            VacationData data = new VacationData();
            data.byId.put(3, new Vacation(
                    3, new Date(2024 - 1900, 5, 10), new Date(2024 - 1900, 5, 20), Vacation.Type.VACATION, ""
            ));
            data.byId.put(4, new Vacation(
                    4, new Date(2024 - 1900, 7, 5), new Date(2024 - 1900, 7, 25), Vacation.Type.VACATION, ""
            ));
            data.byId.put(5, new Vacation(
                    5, new Date(2024 - 1900, 9, 1), new Date(2024 - 1900, 10, 1), Vacation.Type.BUSINESS_TRIP, ""
            ));
            updateVacations(data);
        }

        binding.vacationList.setOnItemClickListener((parent, view, position, id) -> {
            Vacation vacation = (Vacation) parent.getItemAtPosition(position);
            openVacationEditActivity(user.id, vacation.id);
        });

        binding.fab.setOnClickListener(v -> {
            addElement();
        });
    }

    private void openVacationEditActivity(int userId, int vacationId) {
        Intent a = new Intent(this, VacationEditActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        a.putExtra("user_id", userId);
        a.putExtra("vacation_id", vacationId);
        intentLauncher.launch(a);
    }

    private void updateVacations(VacationData data) {
        List<Vacation> items = new ArrayList<>();
        for (Vacation vacation : data.byId.values()) {
            items.add(vacation);
        }

        int resource = android.R.layout.simple_list_item_1;
        ArrayAdapter<Vacation> arrayAdapter = new ArrayAdapter<>(this,
                resource,
                items);
        binding.vacationList.setAdapter(arrayAdapter);
    }

    private void setEnabledScene(boolean value) {

    }

    private void loadVacations() {
        setEnabledScene(false);
        WSClient.loadVacations(WSSession.getInstance().token, user.id)
                .whenCompleteAsync((vacationsData, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        WSSession.getInstance().vacationsByUser.put(user.id, vacationsData);
                        this.updateVacations(vacationsData);
                    } else {  // при ошибке
                        ClientUtils.showNetworkError(this, throwable, this::finish);
                    }
                }, WSClient.MAIN);
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

    private void addElement() {
        openVacationEditActivity(user.id, -1);
    }

}