package com.narimene.temal.pedometer.pedometer.HR1755.models;

import java.util.Arrays;

public class SleepDetailed {
    private int SampleId;
    private int year;
    private int month;
    private int dayOfWeek;
    private int hourStart;
    private int minuteStart;
    private int secondStart;
    private double lengthSleepData;
    private int[] sleepPerFiveMinute = new int[24]; // par 24*5mins


    public static SleepDetailed getInstance(){
        return new SleepDetailed();
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

    public double getLengthSleepData() {
        return lengthSleepData;
    }

    public void setLengthSleepData(double lengthSleepData) {
        this.lengthSleepData = lengthSleepData;
    }

    public int[] getSleepPerFiveMinute() {
        return sleepPerFiveMinute;
    }

    public void setSleepPerFiveMinute(int[] stepsPerMinute) {
        this.sleepPerFiveMinute = stepsPerMinute;
    }

    @Override
    public String toString() {
        return "SleepDetailed{" +
                "year=" + year +
                ", month=" + month +
                ", dayOfWeek=" + dayOfWeek +
                ", hourStart=" + hourStart +
                ", minuteStart=" + minuteStart +
                ", secondStart=" + secondStart +
                ", lengthSleepData=" + lengthSleepData +
                ", stepsPerMinute=" + Arrays.toString(sleepPerFiveMinute) +
                '}';
    }
}
