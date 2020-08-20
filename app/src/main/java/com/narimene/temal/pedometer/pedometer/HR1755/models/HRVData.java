package com.narimene.temal.pedometer.pedometer.HR1755.models;

public class HRVData {
    private int SampleId;
    private int year;
    private int month;
    private int dayOfWeek;
    private int hourStart;
    private int minuteStart;
    private int secondStart;
    private int HRV;
    private int HR;
    private int highRate;
    private int lowRate;
    private int vascularOcclusion;


    public static HRVData getInstance(){
        return new HRVData();
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

    public int getHRV() {
        return HRV;
    }

    public void setHRV(int HRV) {
        this.HRV = HRV;
    }

    public int getHR() {
        return HR;
    }

    public void setHR(int HR) {
        this.HR = HR;
    }

    public int getHighRate() {
        return highRate;
    }

    public void setHighRate(int highRate) {
        this.highRate = highRate;
    }

    public int getLowRate() {
        return lowRate;
    }

    public void setLowRate(int lowRate) {
        this.lowRate = lowRate;
    }

    public int getVascularOcclusion() {
        return vascularOcclusion;
    }

    public void setVascularOcclusion(int vascularOcclusion) {
        this.vascularOcclusion = vascularOcclusion;
    }

    @Override
    public String toString() {
        return "HRVData{" +
                "year=" + year +
                ", month=" + month +
                ", dayOfWeek=" + dayOfWeek +
                ", hourStart=" + hourStart +
                ", minuteStart=" + minuteStart +
                ", secondStart=" + secondStart +
                ", HRV=" + HRV +
                ", HR=" + HR +
                ", highRate=" + highRate +
                ", lowRate=" + lowRate +
                ", vascularOcclusion=" + vascularOcclusion +
                '}';
    }
}
