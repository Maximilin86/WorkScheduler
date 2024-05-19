package me.maxpro.workscheduler.ui.control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.maxpro.workscheduler.ControlActivity;
import me.maxpro.workscheduler.SelectUserListActivity;
import me.maxpro.workscheduler.client.ClientUtils;
import me.maxpro.workscheduler.client.WSClient;
import me.maxpro.workscheduler.client.data.Order;
import me.maxpro.workscheduler.client.data.User;
import me.maxpro.workscheduler.databinding.FragmentControlAdminBinding;
import me.maxpro.workscheduler.utils.WSSession;

public class ControlAdminFragment extends Fragment implements ControlFragment {

    private FragmentControlAdminBinding binding;
    private int selectedAllDayUserId = -1;
    private Date date;
    private ControlActivity parent;
    private ActivityResultLauncher<Intent> intentLauncher;

    public ControlAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        intentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<>() {
            @Override
            public void onActivityResult(ActivityResult activityResult) {
                if (activityResult.getResultCode() == Activity.RESULT_OK) {
                    int[] userIds = activityResult.getData().getIntArrayExtra("selected");
                    if(userIds != null) {
                        setWorkOrders(date, userIds, "");
                    }
                }
            }
        });
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentControlAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        date = new Date(getArguments().getLong("date"));
        parent = (ControlActivity) getActivity();
        Objects.requireNonNull(parent);

        List<User> items = new ArrayList<>();
        items.add(new User(-1, "", "Не выбрано", "", "", WSSession.Role.USER));
        selectedAllDayUserId = -1;
        WSSession session = WSSession.getInstance();
        for (User user : session.users.byId.values()) {
            items.add(user);
        }

        ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                items);
        binding.allDayUser.setAdapter(arrayAdapter);

        updateWorkers();

        binding.allDayUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                User user = (User) parentView.getSelectedItem();
                if(user.id != selectedAllDayUserId) {
                    setOrder(date, user.id, Order.Type.ALL_DAY, "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                setOrder(date, -1, Order.Type.ALL_DAY, "");
            }
        });

        binding.changeWorkers.setOnClickListener(v -> {
            openUserSelectActivity();
        });

    }

    private void updateWorkers() {
        List<Order> orders = parent.getOrders(date);
        int workersCount = 0;
        for (Order order : orders) {
            if (order.order == Order.Type.ALL_DAY) {
                selectItemIdSilent(order.userId);
            } else {
                workersCount++;
            }
        }
        binding.workersCount.setText("" + workersCount);
    }

    private void openUserSelectActivity() {
        Intent a = new Intent(getContext(), SelectUserListActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

        List<Order> orders = parent.getOrders(date);

        int workersCount = 0;
        for (Order order : orders) {
            if (order.order == Order.Type.WORK) workersCount++;
        }
        int[] userIds = new int[workersCount];
        workersCount = 0;
        for (Order order : orders) {
            if (order.order == Order.Type.WORK) {
                userIds[workersCount] = order.userId;
                workersCount++;
            }
        }
        a.putExtra("selected", userIds);
        intentLauncher.launch(a);
    }

    private void setOrder(Date date, int userId, Order.Type order, String comment) {
        binding.allDayUser.setEnabled(false);
        WSSession session = WSSession.getInstance();
        WSClient.setOrder(session.token, date, userId, order, comment)
                .thenCompose(s -> {
                    return WSClient.getOrders(session.token, date);
                })
                .whenCompleteAsync((orders, throwable) -> {
                    binding.allDayUser.setEnabled(true);
                    if(throwable == null) {  // при успехе
                        selectedAllDayUserId = ((User) binding.allDayUser.getSelectedItem()).id;
                        parent.updateOrders(date, orders);
                    } else {  // при ошибке
                        selectItemIdSilent(selectedAllDayUserId);  // возвращаем старое значение
                        ClientUtils.showNetworkError(getContext(), throwable, () -> {});
                    }
                }, WSClient.MAIN);
    }

    private void setWorkOrders(Date date, int[] userIds, String comment) {
        binding.allDayUser.setEnabled(false);
        WSSession session = WSSession.getInstance();
        WSClient.setWorkOrders(session.token, date, userIds, comment)
                .thenCompose(s -> {
                    return WSClient.getOrders(session.token, date);
                })
                .whenCompleteAsync((orders, throwable) -> {
                    binding.allDayUser.setEnabled(true);
                    if(throwable == null) {  // при успехе
                        selectedAllDayUserId = ((User) binding.allDayUser.getSelectedItem()).id;
                        parent.updateOrders(date, orders);
                        updateWorkers();
                    } else {  // при ошибке
                        selectItemIdSilent(selectedAllDayUserId);  // возвращаем старое значение
                        ClientUtils.showNetworkError(getContext(), throwable, () -> {});
                    }
                }, WSClient.MAIN);
    }

    public void selectItemIdSilent(int userId) {
        if(!(binding.allDayUser.getAdapter() instanceof ArrayAdapter)) return;
        ArrayAdapter<User> adapter = (ArrayAdapter<User>) binding.allDayUser.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            User user = adapter.getItem(i);
            if(user == null || user.id != userId) continue;
            selectedAllDayUserId = userId;  // не вызывать события
            binding.allDayUser.setSelection(i);
            break;
        }
    }

}