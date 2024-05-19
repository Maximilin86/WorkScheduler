package me.maxpro.workscheduler.ui.control.wrap;

import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SelectElementWidget {


    public final Spinner view;
    final List<BiConsumer<String, String>> itemSelectCallbacks = new ArrayList<>();
    private String lastItemId = "";

    public SelectElementWidget(Spinner view) {
        this.view = view;
    }

    public void selectItemIdSilent(String itemId) {
        if(!(this.view.getAdapter() instanceof ArrayAdapter)) return;
        ArrayAdapter<Element> adapter = (ArrayAdapter<Element>) this.view.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Element item = adapter.getItem(i);
            if(item == null || !item.id.equals(itemId)) continue;
            lastItemId = itemId;  // не вызывать события
            this.view.setSelection(i);
            break;
        }
    }


    public static class Element {

        private final String id;
        private final String displayName;

        public Element(String id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        @NonNull
        @Override
        public String toString() {
            return this.displayName;
        }
    }
    
    public void setItems(Pair<String, String> ...items) {
        Element[] arr = new Element[items.length];
        for (int i = 0; i < items.length; i++) {
            arr[i] = new Element(items[i].first, items[i].second);
        }

        ArrayAdapter<Element> adapter = new ArrayAdapter<Element>(view.getContext(), android.R.layout.simple_spinner_item, arr);
        this.view.setAdapter(adapter);
        this.view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Element selectedItem = (Element) view.getSelectedItem();
                handleItemChanged(lastItemId, selectedItem.id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                handleItemChanged(lastItemId, "");
            }
        });
    }

    private void handleItemChanged(String from, String to) {
        if(from.equals(to)) return;
        lastItemId = to;
        for (BiConsumer<String, String> callback : itemSelectCallbacks) callback.accept(from, to);
    }


    public void onChangeValue(BiConsumer<String, String> callback) {
        itemSelectCallbacks.add(callback);
    }

}
