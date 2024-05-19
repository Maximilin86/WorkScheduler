package me.maxpro.workscheduler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import me.maxpro.workscheduler.databinding.ActivityMainBinding;
import me.maxpro.workscheduler.ui.main.MainAdminFragment;
import me.maxpro.workscheduler.ui.main.MainUserFragment;
import me.maxpro.workscheduler.utils.WSSession;

public class MainActivity extends AppCompatActivity {

    private static final String MAIN_FRAGMENT = "MAIN_FRAGMENT";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Планировщик смен");
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WSSession session = WSSession.getInstance();

        binding.welcomeText.setText("Здравствуйте, " + session.displayName);
        if(session.role == WSSession.Role.ADMIN) {
            binding.roleText.setVisibility(View.VISIBLE);
        }
        binding.calendarButton.setOnClickListener(view_ -> {
            openCalendarActivity();
        });

        Class<? extends Fragment> fragmentClass =
                session.role == WSSession.Role.USER ?
                        MainUserFragment.class :
                        MainAdminFragment.class;

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.main_fragment, fragmentClass, new Bundle(), MAIN_FRAGMENT)
                .commit();
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

    private void openCalendarActivity() {
        WSSession session = WSSession.getInstance();
        Intent a = new Intent(this, ControlActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        if(session.role == WSSession.Role.USER) {
            a.putExtra("start-with-next-month", true);
        } else {
            a.putExtra("can-edit-since-now", true);
        }
        startActivity(a);
    }

}