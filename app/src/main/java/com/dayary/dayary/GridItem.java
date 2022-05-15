package com.dayary.dayary;

import android.graphics.Bitmap;

public class GridItem {
    private int i;
    private String year;
    private String month;
    private String day;
    private String URL;

    public GridItem(int i, String year, String month, String day, String URL) {
        this.i = i;
        this.year = year;
        this.month = month;
        this.day = day;
        this.URL = URL;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getURL() {
        return URL;
    }

    public int getI() {
        return i;
    }
}
