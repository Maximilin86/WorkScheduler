package me.maxpro.workscheduler.utils;

import android.text.format.DateFormat;

import java.util.Date;

public class RuLang {

    public static String formatMonthYear(Date date) {
        String[] names = new String[] {
                "Январь", "Февраль",
                "Март", "Апрель", "Май",
                "Июнь", "Июль", "Август",
                "Сентябрь", "Октябрь", "Ноябрь",
                "Декабрь",
        };
        return names[date.getMonth()] + " " + Integer.toString(date.getYear() + 1900);
    }
    public static String formatMonth2(Date date) {
        String[] names = new String[] {
                "Января", "Февраля",
                "Марта", "Апреля", "Мая",
                "Июня", "Июля", "Августа",
                "Сентября", "Октября", "Ноября",
                "Декабря",
        };
        return names[date.getMonth()];
    }
    public static String formatDayMonth(Date date) {
        CharSequence day = DateFormat.format("dd", date);
        return day + " " + formatMonth2(date);
    }

    public static int getRuDayOfWeek(Date day) {
        int enDayOfWeek = day.getDay();
        if(enDayOfWeek == 0) return 6;  // sunday = вс
        return enDayOfWeek - 1;
    }

}
