package me.maxpro.workscheduler.ui.control.wrap;

import android.graphics.Color;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class CustomCalendarWidget {


    public final CompactCalendarView view;

    float unselectedBrightness = 0.8f;
    float selectedBrightness = 0.99f;
    final Color selectedColor = Color.valueOf(withValue(0xFFA5A5A5, unselectedBrightness));
    final Color currentDayColor = Color.valueOf(withValue(0xFFBFBFBF, unselectedBrightness));
    final List<Consumer<Date>> dayChangedCallbacks = new ArrayList<>();
    final List<Consumer<Date>> monthChangedCallbacks = new ArrayList<>();
    private Date selectedDate;

    public CustomCalendarWidget(CompactCalendarView view, Date viewDate) {
        this.view = view;
        this.init();
        this.setSelectedDate(viewDate);
    }

    private void updateIndicator(Date dateClicked) {
//        if(isSameDay(dateClicked, new Date())) {
//            view.setCurrentSelectedDayIndicatorStyle(CompactCalendarView.FILL_LARGE_INDICATOR);
//        } else {
//        }
        view.setCurrentSelectedDayIndicatorStyle(CompactCalendarView.NO_FILL_LARGE_INDICATOR);
    }
    private void init() {

        this.view.setDayColumnNames(new String[] {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"});
//        this.view.setCurrentDayBackgroundColor(currentDayColor.toArgb());
        this.view.setCurrentDayIndicatorStyle(CompactCalendarView.NO_FILL_LARGE_INDICATOR);
        this.view.setCurrentSelectedDayBackgroundColor(getCurrentColor(new Date()));
        updateIndicator(new Date());
        this.view.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {
                int color = getCurrentColor(dateClicked);
                view.setCurrentSelectedDayBackgroundColor(color);
                updateIndicator(dateClicked);
                selectedDate = dateClicked;
                for (Consumer<Date> callback : dayChangedCallbacks) callback.accept(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                view.setCurrentSelectedDayBackgroundColor(getCurrentColor(firstDayOfNewMonth));
                selectedDate = firstDayOfNewMonth;
                for (Consumer<Date> callback : dayChangedCallbacks) callback.accept(firstDayOfNewMonth);
                for (Consumer<Date> callback : monthChangedCallbacks) callback.accept(firstDayOfNewMonth);
            }
        });
    }


    public static int _setHsvComponent(int color, int idx, float value) {
        Color col = Color.valueOf(color);
        float[] HSV = new float[3];
        Color.RGBToHSV(
                (int) (col.red() * 255),
                (int) (col.green() * 255),
                (int) (col.blue() * 255),
                HSV
        );
        HSV[idx] = value;
        return Color.HSVToColor(HSV);
    }
    public static int withValue(int color, float value) {
        return _setHsvComponent(color, 2, value);
    }
    public static int withSat(int color, float value) {
        return _setHsvComponent(color, 1, value);
    }

    public boolean isSameDay(Date date1, Date date2) {
        Instant instant1 = date1.toInstant()
                .truncatedTo(ChronoUnit.DAYS);
        Instant instant2 = date2.toInstant()
                .truncatedTo(ChronoUnit.DAYS);
        return instant1.equals(instant2);
    }

    public int getCurrentColor(Date dateClicked) {
        for (Event event : this.view.getEvents(dateClicked)) {
            return withValue(event.getColor(), selectedBrightness);
        }
//        if(isSameDay(dateClicked, new Date())) return withValue(currentDayColor.toArgb(), selectedBrightness - 0.1f);
        return selectedColor.toArgb();
    }

    public void onDayChanged(Consumer<Date> callback) {
        this.dayChangedCallbacks.add(callback);
    }
    public void onMonthChanged(Consumer<Date> callback) {
        this.monthChangedCallbacks.add(callback);
    }

    public void addEvent(Date date, int color, Object data) {
        Event ev1 = new Event(
                withValue(color, unselectedBrightness),
                date.toInstant().toEpochMilli(),
                data
        );
        this.view.addEvent(ev1);
    }

    public void setSelectedDate(Date viewDate) {
        selectedDate = viewDate;
        view.setCurrentDate(viewDate);
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

}
