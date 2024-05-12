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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.maxpro.workscheduler.client.ClientUtils;
import me.maxpro.workscheduler.client.WSClient;
import me.maxpro.workscheduler.client.data.User;
import me.maxpro.workscheduler.client.data.UsersData;
import me.maxpro.workscheduler.databinding.ActivityUserListBinding;
import me.maxpro.workscheduler.utils.WSSession;

public class UserListActivity extends AppCompatActivity {

    private ActivityUserListBinding binding;
    private ActivityResultLauncher<Intent> intentLauncher;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.users_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
            @Override
            public void onActivityResult(ActivityResult activityResult) {
                if (activityResult.getResultCode() == Activity.RESULT_OK) {
                    updateUsers(WSSession.getInstance().users);
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Сотрудники");
        }

        Intent intent = getIntent();
        if(intent.getBooleanExtra("reload-users", false)) {
            loadUsers();
        } else {
            updateUsers(WSSession.getInstance().users);
        }

        binding.userList.setOnItemClickListener((parent, view, position, id) -> {
            User user = (User) parent.getItemAtPosition(position);
            openEditUserActivity(user.id);
        });
    }

    private void openEditUserActivity(int userId) {
        Intent a = new Intent(this, EditUserActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        a.putExtra("user_id", userId);
        intentLauncher.launch(a);
    }

    private void updateUsers(UsersData data) {
        List<User> items = new ArrayList<>();
        for (User user : data.usersById.values()) {
            items.add(user);
        }

        int resource = android.R.layout.simple_list_item_1;
        ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(this,
                resource,
                items);
        binding.userList.setAdapter(arrayAdapter);
    }

    private void setEnabledScene(boolean value) {

    }

    private void loadUsers() {
        setEnabledScene(false);
        WSClient.loadUsers(WSSession.getInstance().token)
                .whenCompleteAsync((usersData, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        WSSession.getInstance().users = usersData;
                        this.updateUsers(usersData);
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
        } else if (itemId == R.id.add_user) {
            addUser();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addUser() {
        setEnabledScene(false);
        WSClient.addUser(WSSession.getInstance().token)
                .whenCompleteAsync((user, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        WSSession.getInstance().users.usersById.put(user.id, user);
                        openEditUserActivity(user.id);
                    } else {  // при ошибке
                        ClientUtils.showNetworkError(this, throwable, this::finish);
                    }
                }, WSClient.MAIN);
    }

}