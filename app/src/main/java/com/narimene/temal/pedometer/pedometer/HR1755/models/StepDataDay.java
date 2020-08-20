package com.narimene.temal.pedometer.pedometer.HR1755.models;

import java.util.Arrays;

public class StepDataDay {
    private int dayId;
    private int year;
    private int month;
    private int dayOfWeek;
    private double steps;
    private double activityTime;
    private double distance;
    private double calories;
    private int target;
    private double intensiveMinutes;
    private int[] detailedActivityTime = new int[3];

    public static StepDataDay getInstance() {
        return new StepDataDay();
    }

    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
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

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }

    public double getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(double activityTime) {
        this.activityTime = activityTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public double getIntensiveMinutes() {
        return intensiveMinutes;
    }

    public void setIntensiveMinutes(double intensiveMinutes) {
        this.intensiveMinutes = intensiveMinutes;
    }

    public int[] getDetailedActivityTime() {
        return detailedActivityTime;
    }

    public void setDetailedActivityTime(int[] detailedActivityTime) {
        this.detailedActivityTime = detailedActivityTime;
    }

    @Override
    public String toString() {
        return "StepDataDay{" +
                "dayId=" + dayId +
                ", year=" + year +
                ", month=" + month +
                ", dayOfWeek=" + dayOfWeek +
                ", steps=" + steps +
                ", activityTime=" + activityTime + " seconds" +
                ", distance=" + distance + " Km" +
                ", calories=" + calories + "Kcal" +
                ", target=" + target +
                ", intensiveMinutes=" + intensiveMinutes +
                ", detailedActivityTime=" + Arrays.toString(detailedActivityTime) +
                '}';
    }
}
