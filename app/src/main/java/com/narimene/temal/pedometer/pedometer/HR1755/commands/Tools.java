package com.narimene.temal.pedometer.pedometer.HR1755.commands;

import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.SleepDetailed;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDetailed;

public class Tools {

    public static boolean checkWithAcquisitionDate(Object lastSample, int mDaysAgo) throws ParseException {
        Date acquisitionDate = getAcquisitionDate(mDaysAgo);
        Log.i("Acquisition day",  acquisitionDate.toString());

        Date sampleDate = getSampleDate(lastSample);

        long diff = acquisitionDate.getTime() - sampleDate.getTime();
        Log.i("Difference days", (diff / (1000 * 60 * 60 * 24)) + " days old.");

        if (diff <= 0){
            return false; // continue
        }else {
            return true; // stop
        }
    }

    public static void getOnlyNecessarySample(List allSampleList, int mDaysAgo){
        Date acquisitionDate = getAcquisitionDate(mDaysAgo);
        int finalItem = 0;

        for (int i = allSampleList.size() - 1 ; i >= 0 ; i--){
            Date sampleDate = getSampleDate(allSampleList.get(i));
            long diff = acquisitionDate.getTime() - sampleDate.getTime();

            if (diff <= 0){
                Log.i("Last Simple to save " + i , sampleDate.toString() );
                Log.i("First Sample to remove " + (i+1), allSampleList.get(i + 1).toString());
                finalItem = (i+1);
                break;
            }
        }
        for (int i = allSampleList.size() - 1 ; i >= finalItem ; i--){
            allSampleList.remove(i);
        }
    }

    public static Date getAcquisitionDate(int mDaysAgo){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, - (mDaysAgo));
        cal.set(Calendar.HOUR_OF_DAY , 0);cal.set(Calendar.MINUTE , 0);cal.set(Calendar.SECOND , 0);cal.set(Calendar.MILLISECOND , 0);
        return cal.getTime();
    }

    public static Calendar getAcquisitionCalendar(int mDaysAgo){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, - (mDaysAgo));
        cal.set(Calendar.HOUR_OF_DAY , 0);cal.set(Calendar.MINUTE , 0);cal.set(Calendar.SECOND , 0);cal.set(Calendar.MILLISECOND , 0);
        return cal;
    }

    public static Date getSampleDate(Object lastSample){
        Calendar cal = Calendar.getInstance();

        if (lastSample instanceof StepDetailed){
            cal.set((((StepDetailed) lastSample).getYear() + 2000), ((StepDetailed) lastSample).getMonth() - 1, ((StepDetailed) lastSample).getDayOfWeek());
            cal.set(Calendar.HOUR_OF_DAY, ((StepDetailed) lastSample).getHourStart());cal.set(Calendar.MINUTE, ((StepDetailed) lastSample).getMinuteStart());
            cal.set(Calendar.SECOND, ((StepDetailed) lastSample).getSecondStart());cal.set(Calendar.MILLISECOND, 0);
        }else if (lastSample instanceof SleepDetailed){
            cal.set((((SleepDetailed) lastSample).getYear() + 2000), ((SleepDetailed) lastSample).getMonth() - 1, ((SleepDetailed) lastSample).getDayOfWeek());
            cal.set(Calendar.HOUR_OF_DAY, ((SleepDetailed) lastSample).getHourStart());cal.set(Calendar.MINUTE, ((SleepDetailed) lastSample).getMinuteStart());
            cal.set(Calendar.SECOND, ((SleepDetailed) lastSample).getSecondStart());cal.set(Calendar.MILLISECOND, 0);
        }
        return cal.getTime();
    }

    public static Calendar getSampleCalendar(Object lastSample){
        Calendar cal = Calendar.getInstance();

        if (lastSample instanceof StepDetailed){
            cal.set((((StepDetailed) lastSample).getYear() + 2000), ((StepDetailed) lastSample).getMonth() - 1, ((StepDetailed) lastSample).getDayOfWeek());
            cal.set(Calendar.HOUR_OF_DAY, ((StepDetailed) lastSample).getHourStart());cal.set(Calendar.MINUTE, ((StepDetailed) lastSample).getMinuteStart());
            cal.set(Calendar.SECOND, ((StepDetailed) lastSample).getSecondStart());cal.set(Calendar.MILLISECOND, 0);
        }else if (lastSample instanceof SleepDetailed){
            cal.set((((SleepDetailed) lastSample).getYear() + 2000), ((SleepDetailed) lastSample).getMonth() - 1, ((SleepDetailed) lastSample).getDayOfWeek());
            cal.set(Calendar.HOUR_OF_DAY, ((SleepDetailed) lastSample).getHourStart());cal.set(Calendar.MINUTE, ((SleepDetailed) lastSample).getMinuteStart());
            cal.set(Calendar.SECOND, ((SleepDetailed) lastSample).getSecondStart());cal.set(Calendar.MILLISECOND, 0);
        }
        return cal;
    }
}
