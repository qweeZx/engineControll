package com.example.myapplication;

import android.text.format.DateFormat;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Класс для преобразования XAxis в время
 */
public class HourAxisValueFormatter extends ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

    }
}
