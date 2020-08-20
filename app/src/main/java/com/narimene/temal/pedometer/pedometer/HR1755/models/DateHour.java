package com.narimene.temal.pedometer.pedometer.HR1755.models;

public class DateHour extends EmptyDate {
    protected int hourStart;
    protected int minuteStart;
    protected int secondStart;

    public static DateHour getInstance(){
        return new DateHour();
    }

    public int getHourStart() {
        return hourStart;
    }

    public void setHourStart(int hourStart) {
        this.hourStart = hourStart;
    }

    public int getMinuteStart() {
        return minuteStart;
    }

    public void setMinuteStart(int minuteStart) {
        this.minuteStart = minuteStart;
    }

    public int getSecondStart() {
        return secondStart;
    }

    public void setSecondStart(int secondStart) {
        this.secondStart = secondStart;
    }

    @Override
    public String toString() {
        return "Date{"     +
                " "        + year +
                "/"        + month +
                "/"        + dayOfWeek +
                ", heure=" + hourStart +
                ":"        + minuteStart +
                ":"        + secondStart +
                '}';
    }
}
