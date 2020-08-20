package com.narimene.temal.pedometer.pedometer.HR1755.models;

public class SoftwareVersion {
    private int hight;
    private int medium;
    private int low;
    private int hyperLow;
    private EmptyDate date;

    public static SoftwareVersion getInstance(){
        return new SoftwareVersion();
    }

    public SoftwareVersion(){
        this.date = EmptyDate.getInstance();
    }

    public int getHight() {
        return hight;
    }

    public void setHight(int hight) {
        this.hight = hight;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getHyperLow() {
        return hyperLow;
    }

    public void setHyperLow(int hyperLow) {
        this.hyperLow = hyperLow;
    }

    public EmptyDate getDate() {
        return date;
    }

    public void setDate(EmptyDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SoftwareVersion{" +
                "V" + hight +
                "." + medium +
                "." + low +
                "." + hyperLow +
                ", date=" + date.toString() +
                '}';
    }
}
