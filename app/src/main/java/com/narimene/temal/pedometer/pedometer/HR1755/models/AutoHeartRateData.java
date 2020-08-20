package com.narimene.temal.pedometer.pedometer.HR1755.models;

public class AutoHeartRateData {

    private int mode;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int [] daysWeek = new int[7];
    private int interval;
    private String daysText;

    public static AutoHeartRateData getInstance(){
        return new AutoHeartRateData();
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public int[] getDaysWeek() {
        return daysWeek;
    }

    public void setDaysWeek(int[] daysWeek) {
        this.daysWeek = daysWeek;
    }

    public String getDaysText() {
        return daysText;
    }

    public void setDaysText(String daysText) {
        this.daysText = daysText;
    }

    @Override
    public String toString() {
        String text = "AutoHeartRateData{" +
                "startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", endHour=" + endHour +
                ", endMinute=" + endMinute +
                ", daysText='" + daysText + '\'' +
                '}';

        if (this.interval != -1){
            text += ", interval=" + interval;
        }
        return text;
    }
}
