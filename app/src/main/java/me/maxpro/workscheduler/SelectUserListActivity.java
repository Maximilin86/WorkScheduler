package me.maxpro.workscheduler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import me.maxpro.workscheduler.databinding.ActivitySelectUserListBinding;
import me.maxpro.workscheduler.utils.WSSession;

public class SelectUserListActivity extends AppCompatActivity {

    private ActivitySelectUserListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Сотрудники");
        }

        binding.userList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        binding.userList.setItemsCanFocus(false);

        Intent intent = getIntent();
        if(intent.getBooleanExtra("reload-users", false)) {
            loadUsers();
        } else {
            updateUsers(WSSession.getInstance().users);
        }

        binding.userList.setOnItemClickListener((parent, view, position, id) -> {
            User user = (User) parent.getItemAtPosition(position);

            Toast toast = Toast.makeText(getApplicationContext(), user.id + " " + user.login, Toast.LENGTH_SHORT);
            toast.show();
        });
    }

    private void updateUsers(UsersData data) {
        List<User> items = new ArrayList<>();
        for (User user : data.usersById.values()) {
            items.add(user);
        }

        int resource = android.R.layout.simple_list_item_multiple_choice;
//        int resource = android.R.layout.simple_list_item_1;
        ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(this,
                resource,
                items);
        binding.userList.setAdapter(arrayAdapter);

        Intent intent = getIntent();
        int[] userIds = intent.getIntArrayExtra("selected");
        Log.d("Test", Arrays.toString(userIds));
        if(userIds != null) {
            for (int userId : userIds) {
                ArrayAdapter<User> adapter = (ArrayAdapter<User>) binding.userList.getAdapter();
                for (int i = 0; i < adapter.getCount(); i++) {
                    User user = adapter.getItem(i);
                    if(user == null || user.id != userId) continue;
                    binding.userList.setItemChecked(i, true);
                    Log.d("Test", "sel " + i + " " + userId);
                    break;
                }
            }
        }
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        ArrayAdapter<User> adapter = (ArrayAdapter<User>) binding.userList.getAdapter();
        List<Integer> userIdList = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (!binding.userList.isItemChecked(i)) continue;
            User user = adapter.getItem(i);
            if (user == null) continue;
            userIdList.add(user.id);
        }
        int[] userIds = new int[userIdList.size()];
        for (int i = 0; i < userIdList.size(); i++) userIds[i] = userIdList.get(i);
        data.putExtra("selected", userIds);
        setResult(Activity.RESULT_OK, data);
        super.finish();
    }

}