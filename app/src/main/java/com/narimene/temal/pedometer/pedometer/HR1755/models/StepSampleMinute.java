package com.narimene.temal.pedometer.pedometer.HR1755.models;

import java.util.Date;

/**
 * Created by chakib on 22/05/19.
 */

public class StepSampleMinute {
    private Date sampleDate;
    private int steps;
    private float distance;
    private float calories;

    public StepSampleMinute(){}

    public Date getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(Date sampleDate) {
        this.sampleDate = sampleDate;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    @Override
    public String toString() {
        return "StepSampleMinute{" +
                "sampleDate=" + sampleDate.toString() +
                ", steps=" + steps +
                ", distance=" + distance +
                ", calorie=" + calories +
                '}';
    }
}
