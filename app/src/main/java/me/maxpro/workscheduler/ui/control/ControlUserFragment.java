package me.maxpro.workscheduler.ui.control;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
import me.maxpro.workscheduler.client.ClientUtils;
import me.maxpro.workscheduler.client.WSClient;
import me.maxpro.workscheduler.client.data.Desire;
import me.maxpro.workscheduler.client.data.Order;
import me.maxpro.workscheduler.utils.WSSession;
import me.maxpro.workscheduler.databinding.FragmentControlUserBinding;

public class ControlUserFragment extends Fragment implements ControlFragment {

    private FragmentControlUserBinding binding;
    private @Nullable Desire.Type selectedDesire = null;
    private Date date;
    private ControlActivity parent;

    public ControlUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentControlUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        date = new Date(getArguments().getLong("date"));
        parent = (ControlActivity) getActivity();
        Objects.requireNonNull(parent);

        ControlViewModel controlViewModel =
                new ViewModelProvider(this).get(ControlViewModel.class);


        List<Desire> items = new ArrayList<>();
        items.add(new Desire(null, ""));
        for (Desire.Type type : Desire.Type.values()) {
            items.add(new Desire(type, ""));
        }

        ArrayAdapter<Desire> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                items);
        binding.dayDesire.setAdapter(arrayAdapter);

        Desire desire = parent.getDesire(date);
        if(desire != null) {
            selectDesireSilent(desire.desire);
        }

        List<Order> orders = parent.getOrders(date);
        assert orders.size() <= 1;
        String orderText = orders.isEmpty() ? "Не назначен" : orders.get(0).order.getDisplayName();
        binding.orderText.setText(orderText);


        binding.dayDesire.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Desire desire = (Desire) parentView.getSelectedItem();
                if(desire.desire != selectedDesire) {
                    setDesire(date, desire);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                setDesire(date, new Desire(null, ""));
            }
        });
    }

    private void setDesire(Date date, Desire desire) {
        WSSession session = WSSession.getInstance();
        WSClient.setDesire(session.token, date, desire.desire, "")
                .whenCompleteAsync((s, throwable) -> {
                    if(throwable == null) {  // при успехе
                        selectedDesire = desire.desire;
                        parent.setDesire(date, desire);
                    } else {  // при ошибке
                        selectDesireSilent(selectedDesire);  // возвращаем старое значение
                        ClientUtils.showNetworkError(getContext(), throwable, () -> {});
                    }
                }, WSClient.MAIN);
    }

    public void selectDesireSilent(@Nullable Desire.Type type) {
        if(!(binding.dayDesire.getAdapter() instanceof ArrayAdapter)) return;
        ArrayAdapter<Desire> adapter = (ArrayAdapter<Desire>) binding.dayDesire.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Desire desire = adapter.getItem(i);
            if(desire == null || desire.desire != type) continue;
            selectedDesire = type;  // не вызывать события
            binding.dayDesire.setSelection(i);
            break;
        }
    }

}