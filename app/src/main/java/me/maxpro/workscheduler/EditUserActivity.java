package me.maxpro.workscheduler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import me.maxpro.workscheduler.client.ClientUtils;
import me.maxpro.workscheduler.client.WSClient;
import me.maxpro.workscheduler.client.data.User;
import me.maxpro.workscheduler.databinding.ActivityEditUserBinding;
import me.maxpro.workscheduler.utils.WSSession;

public class EditUserActivity extends AppCompatActivity {

    private ActivityEditUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        binding = ActivityEditUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Редактирование сотрудника");
        }

        Intent intent = getIntent();
        int userId = intent.getIntExtra("user_id", -1);
        if(userId == -1) {
            finish();
            return;
        }
        User user = WSSession.getInstance().users.usersById.get(userId);
        if(user == null) {
            finish();
            return;
        }

        binding.editLogin.setText(user.login);
        binding.editFirstName.setText(user.firstName);
        binding.editLastName.setText(user.lastName);
        binding.editFathersName.setText(user.fathersName);

        binding.saveButton.setOnClickListener(v -> {
            saveUser();
        });
        binding.deleteUser.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Подтверждение")
                    .setMessage("Удалить сотрудника?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteUser();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }

    private void setEnabledScene(boolean value) {

    }

    private void saveUser() {
        Intent intent = getIntent();
        int userId = intent.getIntExtra("user_id", -1);
        User user = new User(
                userId,
                binding.editLogin.getText().toString(),
                binding.editFirstName.getText().toString(),
                binding.editLastName.getText().toString(),
                binding.editFathersName.getText().toString(),
                null
                );
        String password = binding.editPassword.getText().toString();
        setEnabledScene(false);
        WSClient.saveUser(WSSession.getInstance().token, user, password)
                .whenCompleteAsync((usersData, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        WSSession.getInstance().users = usersData;
                        setResult(Activity.RESULT_OK, new Intent());
                        finish();
                    } else {  // при ошибке
                        ClientUtils.showNetworkError(this, throwable, this::finish);
                    }
                }, WSClient.MAIN);
    }

    private void deleteUser() {
        Intent intent = getIntent();
        int userId = intent.getIntExtra("user_id", -1);
        setEnabledScene(false);
        WSClient.deleteUser(WSSession.getInstance().token, userId)
                .whenCompleteAsync((usersData, throwable) -> {
                    setEnabledScene(true);
                    if(throwable == null) {  // при успехе
                        WSSession.getInstance().users = usersData;
                        finish();
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

    @Override
    public void finish() {
        setResult(Activity.RESULT_OK, new Intent());
        super.finish();
    }
}