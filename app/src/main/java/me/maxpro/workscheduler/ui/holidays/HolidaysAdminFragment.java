package me.maxpro.workscheduler.ui.holidays;

import android.os.Bundle;

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

import me.maxpro.workscheduler.HolidaysActivity;
import me.maxpro.workscheduler.client.ClientUtils;
import me.maxpro.workscheduler.client.WSClient;
import me.maxpro.workscheduler.client.data.Holiday;
import me.maxpro.workscheduler.client.data.Order;
import me.maxpro.workscheduler.client.data.User;
import me.maxpro.workscheduler.databinding.FragmentHolidaysAdminBinding;
import me.maxpro.workscheduler.utils.WSSession;

public class HolidaysAdminFragment extends Fragment {

    private FragmentHolidaysAdminBinding binding;
    private Date date;
    private HolidaysActivity parent;
    private Boolean selectedValue = null;

    public HolidaysAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHolidaysAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        date = new Date(getArguments().getLong("date"));
        parent = (HolidaysActivity) getActivity();
        Objects.requireNonNull(parent);

        List<Item> items = new ArrayList<>();
        items.add(new Item(false, "Выходной"));
        items.add(new Item(true, "Рабочий"));
        selectedValue = null;

        ArrayAdapter<Item> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                items);
        binding.dayType.setAdapter(arrayAdapter);

        updateValue();

        binding.dayType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Item item = (Item) parentView.getSelectedItem();
                if(!Objects.equals(item.value, selectedValue)) {
                    setDayType(date, item.value, "");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                setDayType(date, null, "");
            }
        });
    }

    private void updateValue() {
        Holiday holiday = parent.getHoliday(date);
        selectItemSilent(HolidaysActivity.isWorkDay(date, holiday));
    }

    private void selectItemSilent(boolean isWorkDay) {
        if(!(binding.dayType.getAdapter() instanceof ArrayAdapter)) return;
        ArrayAdapter<Item> adapter = (ArrayAdapter<Item>) binding.dayType.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Item item = adapter.getItem(i);
            if(item == null || !Objects.equals(item.value, isWorkDay)) continue;
            selectedValue = isWorkDay;  // не вызывать события
            binding.dayType.setSelection(i);
            break;
        }
    }

    private void setDayType(Date date, Boolean isWorkDay, String comment) {
        parent.updateDayType(date, new Holiday(isWorkDay, comment));  // todo: removeme
//        binding.dayType.setEnabled(false);
//        WSSession session = WSSession.getInstance();
//        WSClient.setOrder(session.token, date, isWorkDay, comment)
//                .thenCompose(s -> {
//                    return WSClient.getOrders(session.token, date);
//                })
//                .whenCompleteAsync((orders, throwable) -> {
//                    binding.dayType.setEnabled(true);
//                    if(throwable == null) {  // при успехе
//                        selectedValue = ((Item) binding.dayType.getSelectedItem()).value;
//                        parent.updateDayType(date, orders);
//                    } else {  // при ошибке
//                        selectItemIdSilent(selectedAllDayUserId);  // возвращаем старое значение
//                        ClientUtils.showNetworkError(getContext(), throwable, () -> {});
//                    }
//                }, WSClient.MAIN);
    }

    public static class Item {

        final boolean value;
        final String displayName;

        public Item(boolean value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }


        @NonNull
        @Override
        public String toString() { return displayName; }
    }

}