package me.maxpro.workscheduler;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.maxpro.workscheduler.utils.WSClient;
import me.maxpro.workscheduler.utils.WSSession;

public class LoginActivity extends AppCompatActivity  {
    Button loginButton, cancelButton;
    EditText loginField, passwordField;
    LinearLayout root;
    TextView tx1;
    int counter = 3;

    private void setEnabledScene(boolean value) {
        for (int i = 0; i < root.getChildCount(); i++) {
            root.getChildAt(i).setEnabled(value);
        }
    }

    private void openMenuActivity() {
//        Intent a = new Intent(LoginActivity.this, CalendarActivity.class);
        Intent a = new Intent(LoginActivity.this, MainActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        WSSession.getInstance().url = getResources().getString(R.string.server_url);
        Log.d("Test", "url: " + WSSession.getInstance().url);

        loginButton = (Button)findViewById(R.id.login_button);
        loginField = (EditText)findViewById(R.id.login_field);
        passwordField = (EditText)findViewById(R.id.password_field);
        root = (LinearLayout) findViewById(R.id.root);
        cancelButton = (Button)findViewById(R.id.cancel_button);
        tx1 = (TextView)findViewById(R.id.textView3);
        tx1.setVisibility(View.GONE);

        loginButton.setOnClickListener(view -> {
//            Log.d("Test", "Login " + loginField.getText().toString() + " " + passwordField.getText().toString());
            setEnabledScene(false);
            WSClient.Login(loginField.getText().toString(), passwordField.getText().toString())
                    .whenCompleteAsync((s, throwable) -> setEnabledScene(true), WSClient.MAIN)  // в любом случае
                    .thenAcceptAsync(args -> {
                        WSSession.getInstance().initFromLogin(args);
                        openMenuActivity();
                    }, WSClient.MAIN)  // при успехе
                    .handleAsync((unused, throwable) -> {  // при ошибке
                        WSClient.showNetworkError(view.getContext(), throwable);
                        return null;
                    }, WSClient.MAIN);
//            if(loginField.getText().toString().equals("admin") && passwordField.getText().toString().equals("admin")) {
//                Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
//            } else{
//                Toast.makeText(getApplicationContext(), "Неправильно введен логин или пароль",Toast.LENGTH_SHORT).show();
//                tx1.setVisibility(View.VISIBLE);
//                tx1.setBackgroundColor(Color.RED);
//                counter--;
//                tx1.setText(Integer.toString(counter));
//
//                if (counter == 0) {
//                    loginButton.setEnabled(false);
//                }
//            }
        });
        cancelButton.setOnClickListener(view -> finish());
    }

}