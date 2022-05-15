package com.dayary.dayary;

import android.graphics.Bitmap;

public class GridItem {
    private String year;
    private String month;
    private String day;
    private String URL;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public GridItem(String year, String month, String day, String URL) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.URL = URL;
    }
}
