package com.narimene.temal.pedometer.pedometer.HR1755.models;

public class AlarmDevice {
    private int ranking;
    private int state;
    private int hour;
    private int minute;

    public int getRanking() {
        return ranking;
    }

    public AlarmDevice setRanking(int ranking) {
        this.ranking = ranking;
        return this;
    }

    public int getState() {
        return state;
    }

    public AlarmDevice setState(int state) {
        this.state = state;
        return this;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setTime(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
    }
}
