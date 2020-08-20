package com.narimene.temal.pedometer.pedometer.HR1755.models;

import java.util.Date;

/**
 * Created by chakib on 22/05/19.
 */

public class SleepSampleFiveMinute {

    private Date sampleDate;
    int qualitySleepFiveminutes;

    public SleepSampleFiveMinute(){}

    public Date getSampleDate() {
        return sampleDate;
    }

    public void setSampleDate(Date sampleDate) {
        this.sampleDate = sampleDate;
    }

    public int getQualitySleepFiveminutes() {
        return qualitySleepFiveminutes;
    }

    public void setQualitySleepFiveminutes(int qualitySleepFiveminutes) {
        this.qualitySleepFiveminutes = qualitySleepFiveminutes;
    }

    @Override
    public String toString() {
        return "SleepSampleFiveMinutes{" +
                "sampleDate=" + sampleDate.toString() +
                ", qualitySleep=" + qualitySleepFiveminutes +
                '}';
    }
}
