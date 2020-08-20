package com.narimene.temal.pedometer.pedometer.HR1755.models;

import java.util.Arrays;

public class ExerciseData {
    private int dayId;
    private int year;
    private int month;
    private int dayOfWeek;
    private int hourStart;
    private int minuteStart;
    private int secondStart;
    private int typeExercise;
    private int heartRate;
    private int [] exerciseTime = new int[3];
    private double steps;
    private String exerciseSpeed;
    private double calories;
    private double distance;

    public static ExerciseData getInstance(){
        return new ExerciseData();
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

    public int getTypeExercise() {
        return typeExercise;
    }

    public void setTypeExercise(int typeExercise) {
        this.typeExercise = typeExercise;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public int[] getExerciseTime() {
        return exerciseTime;
    }

    public void setExerciseTime(int[] exerciseTime) {
        this.exerciseTime = exerciseTime;
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
    }

    public String getExerciseSpeed() {
        return exerciseSpeed;
    }

    public void setExerciseSpeed(String exerciseSpeed) {
        this.exerciseSpeed = exerciseSpeed;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "ExerciseData{" +
                "year=" + year +
                ", month=" + month +
                ", dayOfWeek=" + dayOfWeek +
                ", hourStart=" + hourStart +
                ", minuteStart=" + minuteStart +
                ", secondStart=" + secondStart +
                ", typeExercise=" + typeExercise +
                ", heartRate=" + heartRate +
                ", exerciseTime=" + Arrays.toString(exerciseTime) +
                ", steps=" + steps +
                ", exerciseSpeed=" + exerciseSpeed +
                ", calories=" + calories +
                ", distance=" + distance +
                '}';
    }
}
