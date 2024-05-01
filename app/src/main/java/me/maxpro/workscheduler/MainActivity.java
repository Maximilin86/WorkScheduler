package me.maxpro.workscheduler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

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

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WSSession session = WSSession.getInstance();

        binding.welcomeText.setText("Здравствуйте, " + session.displayName);

        Class<? extends Fragment> fragmentClass =
                session.role == WSSession.Role.USER ?
                        MainUserFragment.class :
                        MainAdminFragment.class;

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.main_fragment, fragmentClass, new Bundle(), MAIN_FRAGMENT)
                .commit();

    }
}