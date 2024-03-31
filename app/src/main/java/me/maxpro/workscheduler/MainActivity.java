package me.maxpro.workscheduler;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//public class MainActivity extends AppCompatActivity {
//
//    private ActivityMainBinding binding;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);
//    }
//
//}
import android.graphics.Color;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {
    Button b1,b2;
    EditText ed1,ed2;
    RelativeLayout root;
    String token;
    TextView tx1;
    int counter = 3;

    private void setEnabledScene(boolean value) {
        for (int i = 0; i < root.getChildCount(); i++) {
            root.getChildAt(i).setEnabled(value);
        }
    }

    private void openMenuActivity(String token) {
        Log.d("Test", token);
        Intent a = new Intent(MainActivity.this, MenuActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = (Button)findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);
        root = (RelativeLayout) findViewById(R.id.root);
        b2 = (Button)findViewById(R.id.button2);
        tx1 = (TextView)findViewById(R.id.textView3);
        tx1.setVisibility(View.GONE);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Test", "Hello World");
                // point 1
                setEnabledScene(false);
                WSClient.Login(ed1.getText().toString(),ed2.getText().toString())
                        .thenAccept(token -> {
                            // point 2
                            WSClient.MAIN.execute(() -> setEnabledScene(true));
                            Log.d("Test", "is GUI thread: " + Looper.getMainLooper().isCurrentThread());
                            openMenuActivity(token);
                        })
                        .exceptionally(throwable -> {
                            // point 2e
                            WSClient.MAIN.execute(() -> setEnabledScene(true));
                            Log.d("Test", "is GUI thread: " + Looper.getMainLooper().isCurrentThread());
                            Log.e("Test", "Error!!!", throwable);
                            return null;
                        });                        ;
                if(ed1.getText().toString().equals("admin") &&
                        ed2.getText().toString().equals("admin")) {
                    Toast.makeText(getApplicationContext(),
                            "Redirecting...",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Неправильно введен логин или пароль",Toast.LENGTH_SHORT).show();

                            tx1.setVisibility(View.VISIBLE);
                    tx1.setBackgroundColor(Color.RED);
                    counter--;
                    tx1.setText(Integer.toString(counter));

                    if (counter == 0) {
                        b1.setEnabled(false);
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}