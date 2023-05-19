package com.smart.medilation.model;

public class DateModel {
    private String day;
    private String date;

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    private long timeInMillis;

    public boolean isSelected = false;

    public DateModel(String day, String date, long timeInMillis) {
        this.day = day;
        this.date = date;
        this.timeInMillis = timeInMillis;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}

