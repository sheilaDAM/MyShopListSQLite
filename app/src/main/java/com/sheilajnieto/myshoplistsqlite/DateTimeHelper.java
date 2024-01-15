package com.sheilajnieto.myshoplistsqlite;/*
@author sheila j. nieto 
@version 0.1 2024 -01 - 12
*/

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeHelper {

    public static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static Date parseDateString(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            // Manejar la excepción si la cadena no se puede convertir a Date
            e.printStackTrace();
            return null;
        }
    }
}
