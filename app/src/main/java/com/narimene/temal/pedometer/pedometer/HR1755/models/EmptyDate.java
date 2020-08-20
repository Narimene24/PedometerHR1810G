package com.narimene.temal.pedometer.pedometer.HR1755.models;

public class EmptyDate {
    protected int year;
    protected int month;
    protected int dayOfWeek;

    public static EmptyDate getInstance(){
        return new EmptyDate();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        return "EmptyDate{" +
                "year=" + year +
                ", month=" + month +
                ", dayOfWeek=" + dayOfWeek +
                '}';
    }
}
