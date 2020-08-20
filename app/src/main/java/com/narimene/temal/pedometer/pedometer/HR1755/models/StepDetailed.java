package com.narimene.temal.pedometer.pedometer.HR1755.models;

import java.util.Arrays;

public class StepDetailed {

    private int SampleId;
    private int year;
    private int month;
    private int dayOfWeek;
    private int hourStart;
    private int minuteStart;
    private int secondStart;
    private double steps;
    private double distance;
    private double calories;
    private int[] stepsPerMinute = new int[10]; // par 10 mins

    public static StepDetailed getInstance(){
        return new StepDetailed();
    }

    public int getSampleId() {
        return SampleId;
    }

    public void setSampleId(int sampleId) {
        SampleId = sampleId;
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

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
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

    public int[] getStepsPerMinute() {
        return stepsPerMinute;
    }

    public void setStepsPerMinute(int[] stepsPerMinute) {
        this.stepsPerMinute = stepsPerMinute;
    }

    @Override
    public String toString() {
        return "StepDetailed{" +
                ", year=" + year +
                ", month=" + month +
                ", dayOfWeek=" + dayOfWeek +
                ", hourStart=" + hourStart +
                ", minuteStart=" + minuteStart +
                ", secondStart=" + secondStart +
                ", steps=" + steps +
                ", distance=" + distance +
                ", calories=" + calories +
                ", stepsPerMinute=" + Arrays.toString(stepsPerMinute) +
                '}';
    }
}
