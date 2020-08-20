package com.narimene.temal.pedometer.pedometer.HR1755.models;

public class ActivityPeriod {

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int [] daysWeek = new int[7];
    private int remindPeriod;
    private int minExerciseSteps;
    private int state;
    private String daysText;


    public static ActivityPeriod getInstance(){
        return new ActivityPeriod();
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

    public int getRemindPeriod() {
        return remindPeriod;
    }

    public void setRemindPeriod(int remindPeriod) {
        this.remindPeriod = remindPeriod;
    }

    public int getMinExerciseSteps() {
        return minExerciseSteps;
    }

    public void setMinExerciseSteps(int minExerciseSteps) {
        this.minExerciseSteps = minExerciseSteps;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDaysText() {
        return daysText;
    }

    public void setDaysText(String daysText) {
        this.daysText = daysText;
    }

    @Override
    public String toString() {
        return "ActivityPeriod{" +
                "startHour=" + startHour +
                ", startMinute=" + startMinute +
                ", endHour=" + endHour +
                ", endMinute=" + endMinute +
                ", remindPeriod=" + remindPeriod +
                ", minExerciseSteps=" + minExerciseSteps +
                ", daysText='" + daysText + '\'' +
                '}';
    }
}
