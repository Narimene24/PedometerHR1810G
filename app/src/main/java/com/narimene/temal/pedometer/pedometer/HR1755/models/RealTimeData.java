package com.narimene.temal.pedometer.pedometer.HR1755.models;

import java.util.Arrays;

public class RealTimeData {
    double steps;
    double calories;
    double distance;
    int [] timeActivity     = new int[3];
    int [] timeFastActivity = new int[3];
    int heartRate;
    float temperature ;


    public static RealTimeData newInstance(){
        RealTimeData data = new RealTimeData();
        data.setDistance(0); data.setSteps(0);
        return data;
    }

    public static RealTimeData getInstance(){
        return new RealTimeData();
    }

    public double getSteps() {
        return steps;
    }

    public void setSteps(double steps) {
        this.steps = steps;
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

    public int[] getTimeActivity() {
        return timeActivity;
    }

    public void setTimeActivity(int[] timeActivity) {
        this.timeActivity = timeActivity;
    }

    public int[] getTimeFastActivity() {
        return timeFastActivity;
    }

    public void setTimeFastActivity(int[] timeFastActivity) {
        this.timeFastActivity = timeFastActivity;
    }



    public float getTemperature() {
        return temperature;
    }
    public void setTemperature(float decimalTemperature) {
    }


    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public static RealTimeData differenceData(RealTimeData reference, RealTimeData nextSample){
        RealTimeData returnedData = new RealTimeData();
        returnedData.setSteps(nextSample.getSteps() - reference.getSteps());
        returnedData.setDistance((nextSample.getDistance() - reference.getDistance()));
        return returnedData;
    }



    @Override
    public String toString() {
        if (this.temperature == 0) return "RealTimeData{"        +
                "steps="              + steps +
                ", calories="         + calories +
                " KCal, distance="         + distance +
                " Km, timeActivity="     + Arrays.toString(timeActivity) +
                ", timeFastActivity=" + Arrays.toString(timeFastActivity) +
                ", heartRate="        + heartRate +
                '}';

    else return "RealTimeData{"        +
                "steps="              + steps +
                ", calories="         + calories +
                " KCal, distance="         + distance +
                " Km, timeActivity="     + Arrays.toString(timeActivity) +
                ", timeFastActivity=" + Arrays.toString(timeFastActivity) +
                ", heartRate="        + heartRate +
                ", temperature real time ="        + temperature +
                '}';
    }



}
