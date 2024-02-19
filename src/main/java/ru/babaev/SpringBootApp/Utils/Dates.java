package ru.babaev.SpringBootApp.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Dates {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String getCurrentDate(){
        return simpleDateFormat.format(new Date());
    }

    public static String getCurrentDatePlusNDays(int n){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, n);
        return simpleDateFormat.format(calendar.getTime());
    }
}
