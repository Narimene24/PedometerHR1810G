package com.narimene.temal.pedometer.pedometer;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.commands.OnLoadDataPedometerHR1755;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.commands.Tools;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.SleepDetailed;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.SleepSampleFiveMinute;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDataDay;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDetailed;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepSampleMinute;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.modes.SynchronizeBleNotifyCallbackHR1755;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.commands.OnLoadDataPedometerHR1810G;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.modes.SynchronizeBleNotifyCallBackHR1810G;
import fr.semantic.ecare.app.android.domain.measure.pedometer.activity.DailyActivity;
import fr.semantic.ecare.app.android.domain.measure.pedometer.sleep.DailySleep;
import fr.semantic.ecare.app.android.domain.measure.pedometer.sleep.SleepQuarterHour;

public class ConversionData {

    private static final String TAG = ConversionData.class.getSimpleName();

    private static OnLoadDataPedometerHR1755 mListenerHR1755;
    private static OnLoadDataPedometerHR1810G mListenerHR1810G;
    private static Calendar acquisitionDate;

    public static ConversionData newInstance() {
        return new ConversionData();
    }

    public static OnLoadDataPedometerHR1755 getListenerHR1755() {
        return mListenerHR1755;
    }

    public static OnLoadDataPedometerHR1810G getmListenerHR1810G() {
        return mListenerHR1810G;
    }

    public static void setListener(OnLoadDataPedometerHR1755 mListener) {
        ConversionData.mListenerHR1755 = mListener;
    }

    public static void setListenerHR1810G(OnLoadDataPedometerHR1810G mListener) {
        ConversionData.mListenerHR1810G = mListener;
    }

    public static List<StepSampleMinute> conversionToMinuteStepSamples(List<StepDetailed> treatmentList, List<StepSampleMinute> listStepsSampleMinute) {
        float totalDistance, totalCalories, totalSteps;

        for (int i = treatmentList.size() - 1; i >= 0; i--) {
            totalDistance = (float) treatmentList.get(i).getDistance();
            totalCalories = (float) treatmentList.get(i).getCalories();
            totalSteps = (float) treatmentList.get(i).getSteps();

            for (int j = 0; j < treatmentList.get(i).getStepsPerMinute().length; j++) {
                StepSampleMinute sampleMinute = new StepSampleMinute();
                sampleMinute.setSteps(treatmentList.get(i).getStepsPerMinute()[j]);
                Calendar cal = Tools.getSampleCalendar(treatmentList.get(i));
                cal.add(Calendar.MINUTE, j);
                sampleMinute.setSampleDate(cal.getTime());
                sampleMinute.setDistance(stepsPercentage(sampleMinute.getSteps(), totalSteps, totalDistance));
                sampleMinute.setCalories(stepsPercentage(sampleMinute.getSteps(), totalSteps, totalCalories));
                listStepsSampleMinute.add(sampleMinute);
                //Log.i(TAG, sampleMinute.toString());
            }
        }
        treatmentList.clear();
        return listStepsSampleMinute;
    }

    public static void conversionToFiveMinuteSleepSamples(List<SleepDetailed> treatmentList, List<SleepSampleFiveMinute> listSleepSampleFiveMinute) {
        int addedTime;
        for (int i = treatmentList.size() - 1; i >= 0; i--) {
            addedTime = 0;
            for (int j = 0; j < treatmentList.get(i).getSleepPerFiveMinute().length; j++) {
                SleepSampleFiveMinute sampleFiveMinute = new SleepSampleFiveMinute();
                sampleFiveMinute.setQualitySleepFiveminutes(treatmentList.get(i).getSleepPerFiveMinute()[j]);
                Calendar cal = Tools.getSampleCalendar(treatmentList.get(i));
                cal.add(Calendar.MINUTE, addedTime);
                addedTime += 5;
                sampleFiveMinute.setSampleDate(cal.getTime());
                listSleepSampleFiveMinute.add(sampleFiveMinute);
            }
        }
        treatmentList.clear();
    }


    public static void chargingDailyActivityHR1755(int mDaysAgo, List<StepSampleMinute> listStepsSampleMinute, List<DailyActivity> activityList, SynchronizeBleNotifyCallbackHR1755.ProgressTask mProgress) {
        int[] percentageValues = new int[3];
        percentageValues[0] = mDaysAgo / 3;
        percentageValues[1] = mDaysAgo / 2;
        percentageValues[2] = (mDaysAgo * 3) / 4;

        for (int j = 0; j <= mDaysAgo; j++) {
            Calendar startDateDaily = Tools.getAcquisitionCalendar(mDaysAgo);
            startDateDaily.add(Calendar.DATE, j);

            DailyActivity activity = new DailyActivity().initializeQuarters();

            for (StepSampleMinute sample : listStepsSampleMinute) {
                Calendar interestedDay = Tools.getAcquisitionCalendar(mDaysAgo);
                interestedDay.add(Calendar.DATE, j);

                boolean isSameDay = sample.getSampleDate().getMonth() == interestedDay.getTime().getMonth() &&
                        sample.getSampleDate().getDay() == interestedDay.getTime().getDay();
                if (!isSameDay)
                    continue;

                for (int i = 0; i < 96; i++) {
                    acquisitionDate = Tools.getAcquisitionCalendar(mDaysAgo);
                    acquisitionDate.add(Calendar.DATE, j);

                    acquisitionDate.add(Calendar.MINUTE, (i * 15));
                    Date quarterStart = acquisitionDate.getTime();

                    Calendar endDate = acquisitionDate;
                    endDate.add(Calendar.MINUTE, 15);

                    Date quarterEnd = endDate.getTime();
                    Date startSample = sample.getSampleDate();

                    boolean inQuarter = startSample.before(quarterEnd) && startSample.after(quarterStart);

                    if (inQuarter) {
                        activity.getQuarterHourList().get(i).setCalories(activity.getQuarterHourList().get(i).getCalories() + sample.getCalories());
                        activity.getQuarterHourList().get(i).setDistance(activity.getQuarterHourList().get(i).getDistance() + sample.getDistance());
                        activity.getQuarterHourList().get(i).setSteps(activity.getQuarterHourList().get(i).getSteps() + sample.getSteps());
                        break;
                    }
                }
            }
            activity.setDate(startDateDaily.getTime().getTime());
            activityList.add(activity);
            getPercentageOperationHR1755(j, percentageValues, mProgress, 1);
        }
        listStepsSampleMinute.clear();
    }

    public static void chargingDailyActivityHR1810G(int mDaysAgo, List<StepSampleMinute> listStepsSampleMinute, List<DailyActivity> activityList, SynchronizeBleNotifyCallBackHR1810G.ProgressTask mProgress) {
        int[] percentageValues = new int[3];
        percentageValues[0] = mDaysAgo / 3;
        percentageValues[1] = mDaysAgo / 2;
        percentageValues[2] = (mDaysAgo * 3) / 4;

        for (int j = 0; j <= mDaysAgo; j++) {
            Calendar startDateDaily = Tools.getAcquisitionCalendar(mDaysAgo);
            startDateDaily.add(Calendar.DATE, j);

            DailyActivity activity = new DailyActivity().initializeQuarters();

            for (StepSampleMinute sample : listStepsSampleMinute) {
                Calendar interestedDay = Tools.getAcquisitionCalendar(mDaysAgo);
                interestedDay.add(Calendar.DATE, j);

                boolean isSameDay = sample.getSampleDate().getMonth() == interestedDay.getTime().getMonth() &&
                        sample.getSampleDate().getDay() == interestedDay.getTime().getDay();
                if (!isSameDay)
                    continue;

                for (int i = 0; i < 96; i++) {
                    acquisitionDate = Tools.getAcquisitionCalendar(mDaysAgo);
                    acquisitionDate.add(Calendar.DATE, j);

                    acquisitionDate.add(Calendar.MINUTE, (i * 15));
                    Date quarterStart = acquisitionDate.getTime();

                    Calendar endDate = acquisitionDate;
                    endDate.add(Calendar.MINUTE, 15);

                    Date quarterEnd = endDate.getTime();
                    Date startSample = sample.getSampleDate();

                    boolean inQuarter = startSample.before(quarterEnd) && startSample.after(quarterStart);

                    if (inQuarter) {
                        activity.getQuarterHourList().get(i).setCalories(activity.getQuarterHourList().get(i).getCalories() + sample.getCalories());
                        activity.getQuarterHourList().get(i).setDistance(activity.getQuarterHourList().get(i).getDistance() + sample.getDistance());
                        activity.getQuarterHourList().get(i).setSteps(activity.getQuarterHourList().get(i).getSteps() + sample.getSteps());
                        break;
                    }
                }
            }
            activity.setDate(startDateDaily.getTime().getTime());
            activityList.add(activity);
            getPercentageOperationHR1810G(j, percentageValues, mProgress, 1);
        }
        listStepsSampleMinute.clear();
    }

    public static void chargingDailySleepHR1755(int mDaysAgo, List<SleepSampleFiveMinute> listSleepSampleFiveMinute, List<DailySleep> sleepList, SynchronizeBleNotifyCallbackHR1755.ProgressTask mProgress) throws Exception {

        int[] percentageValues = new int[3];
        percentageValues[0] = mDaysAgo / 3;
        percentageValues[1] = mDaysAgo / 2;
        percentageValues[2] = (mDaysAgo * 3) / 4;

        for (int j = 0; j <= mDaysAgo; j++) {
            Calendar startDateDaily = Tools.getAcquisitionCalendar(mDaysAgo);
            startDateDaily.add(Calendar.DATE, j);

            DailySleep dailySleep = new DailySleep();
            dailySleep.resetDailySleep();
            dailySleep.initializeQuarters();

            for (SleepSampleFiveMinute sample : listSleepSampleFiveMinute) {
                Calendar interestedDay = Tools.getAcquisitionCalendar(mDaysAgo);
                interestedDay.add(Calendar.DATE, j);

                boolean isSameDay = sample.getSampleDate().getMonth() == interestedDay.getTime().getMonth() &&
                        sample.getSampleDate().getDay() == interestedDay.getTime().getDay();
                if (!isSameDay)
                    continue;

                for (int i = 0; i < 96; i++) {
                    acquisitionDate = Tools.getAcquisitionCalendar(mDaysAgo);
                    acquisitionDate.add(Calendar.DATE, j);

                    acquisitionDate.add(Calendar.MINUTE, (i * 15));
                    Date quarterStart = acquisitionDate.getTime();

                    Calendar endDate = acquisitionDate;
                    endDate.add(Calendar.MINUTE, 15);

                    Date quarterEnd = endDate.getTime();
                    Date startSample = sample.getSampleDate();

                    boolean inQuarter = startSample.before(quarterEnd) && startSample.after(quarterStart);

                    if (inQuarter) {

                        int k = checkMinuteInQuarter(quarterStart, startSample);

                        switch (k) {
                            case 0:
                                dailySleep.getQuarterHourList().get(i).getData()[0] += sample.getQualitySleepFiveminutes();
                                break;
                            case 1:
                                dailySleep.getQuarterHourList().get(i).getData()[1] += sample.getQualitySleepFiveminutes();
                                break;
                            case 2:
                                dailySleep.getQuarterHourList().get(i).getData()[2] += sample.getQualitySleepFiveminutes();
                                break;
                            case 3:
                                dailySleep.getQuarterHourList().get(i).getData()[3] += sample.getQualitySleepFiveminutes();
                                break;
                            case 4:
                                dailySleep.getQuarterHourList().get(i).getData()[4] += sample.getQualitySleepFiveminutes();
                                break;
                            case 5:
                                dailySleep.getQuarterHourList().get(i).getData()[5] += sample.getQualitySleepFiveminutes();
                                break;
                            case 6:
                                dailySleep.getQuarterHourList().get(i).getData()[6] += sample.getQualitySleepFiveminutes();
                                break;
                            case 7:
                                dailySleep.getQuarterHourList().get(i).getData()[7] += sample.getQualitySleepFiveminutes();
                                break;
                            default:
                                throw new Exception("vérifier la valeur de k ");
                        }
                        break;
                    }
                }
            }
            dailySleep.setDate(startDateDaily.getTime().getTime());
            for (SleepQuarterHour quarter : dailySleep.getQuarterHourList()) {
                boolean isSleepQuarter = false;
                for (int i = 0; i < 8; i++) {
                    if (quarter.getData()[i] > 0) {
                        isSleepQuarter = true;
                    }
                }
                if (isSleepQuarter) {
                    for (int i = 0; i < 8; i++) {
                        if (quarter.getData()[i] == 0) {
                            quarter.getData()[i] = 1;
                        }
                    }
                }
            }

            for (SleepQuarterHour quarter : dailySleep.getQuarterHourList()) {
                quarter.computeMean();
                dailySleep.setupDurationSleep(quarter);
            }

            dailySleep.computeQuality();
            sleepList.add(dailySleep);
            getPercentageOperationHR1755(j, percentageValues, mProgress, 2);
        }
        listSleepSampleFiveMinute.clear();
        if (mListenerHR1755 != null)
            mListenerHR1755.onTreatmentSleepIsFinish();
    }

    public static void chargingDailySleepHR1810F(int mDaysAgo, List<SleepSampleFiveMinute> listSleepSampleFiveMinute, List<DailySleep> sleepList, SynchronizeBleNotifyCallBackHR1810G.ProgressTask mProgress) throws Exception {

        int[] percentageValues = new int[3];
        percentageValues[0] = mDaysAgo / 3;
        percentageValues[1] = mDaysAgo / 2;
        percentageValues[2] = (mDaysAgo * 3) / 4;

        for (int j = 0; j <= mDaysAgo; j++) {
            Calendar startDateDaily = Tools.getAcquisitionCalendar(mDaysAgo);
            startDateDaily.add(Calendar.DATE, j);

            DailySleep dailySleep = new DailySleep();
            dailySleep.resetDailySleep();
            dailySleep.initializeQuarters();

            for (SleepSampleFiveMinute sample : listSleepSampleFiveMinute) {
                Calendar interestedDay = Tools.getAcquisitionCalendar(mDaysAgo);
                interestedDay.add(Calendar.DATE, j);

                boolean isSameDay = sample.getSampleDate().getMonth() == interestedDay.getTime().getMonth() &&
                        sample.getSampleDate().getDay() == interestedDay.getTime().getDay();
                if (!isSameDay)
                    continue;

                for (int i = 0; i < 96; i++) {
                    acquisitionDate = Tools.getAcquisitionCalendar(mDaysAgo);
                    acquisitionDate.add(Calendar.DATE, j);

                    acquisitionDate.add(Calendar.MINUTE, (i * 15));
                    Date quarterStart = acquisitionDate.getTime();

                    Calendar endDate = acquisitionDate;
                    endDate.add(Calendar.MINUTE, 15);

                    Date quarterEnd = endDate.getTime();
                    Date startSample = sample.getSampleDate();

                    boolean inQuarter = startSample.before(quarterEnd) && startSample.after(quarterStart);

                    if (inQuarter) {

                        int k = checkMinuteInQuarter(quarterStart, startSample);

                        switch (k) {
                            case 0:
                                dailySleep.getQuarterHourList().get(i).getData()[0] += sample.getQualitySleepFiveminutes();
                                break;
                            case 1:
                                dailySleep.getQuarterHourList().get(i).getData()[1] += sample.getQualitySleepFiveminutes();
                                break;
                            case 2:
                                dailySleep.getQuarterHourList().get(i).getData()[2] += sample.getQualitySleepFiveminutes();
                                break;
                            case 3:
                                dailySleep.getQuarterHourList().get(i).getData()[3] += sample.getQualitySleepFiveminutes();
                                break;
                            case 4:
                                dailySleep.getQuarterHourList().get(i).getData()[4] += sample.getQualitySleepFiveminutes();
                                break;
                            case 5:
                                dailySleep.getQuarterHourList().get(i).getData()[5] += sample.getQualitySleepFiveminutes();
                                break;
                            case 6:
                                dailySleep.getQuarterHourList().get(i).getData()[6] += sample.getQualitySleepFiveminutes();
                                break;
                            case 7:
                                dailySleep.getQuarterHourList().get(i).getData()[7] += sample.getQualitySleepFiveminutes();
                                break;
                            default:
                                throw new Exception("vérifier la valeur de k ");
                        }
                        break;
                    }
                }
            }
            dailySleep.setDate(startDateDaily.getTime().getTime());
            for (SleepQuarterHour quarter : dailySleep.getQuarterHourList()) {
                boolean isSleepQuarter = false;
                for (int i = 0; i < 8; i++) {
                    if (quarter.getData()[i] > 0) {
                        isSleepQuarter = true;
                    }
                }
                if (isSleepQuarter) {
                    for (int i = 0; i < 8; i++) {
                        if (quarter.getData()[i] == 0) {
                            quarter.getData()[i] = 1;
                        }
                    }
                }
            }

            for (SleepQuarterHour quarter : dailySleep.getQuarterHourList()) {
                quarter.computeMean();
                dailySleep.setupDurationSleep(quarter);
            }

            dailySleep.computeQuality();
            sleepList.add(dailySleep);
            getPercentageOperationHR1810G(j, percentageValues, mProgress, 2);
        }
        listSleepSampleFiveMinute.clear();
        if (mListenerHR1810G != null)
            mListenerHR1810G.onTreatmentSleepIsFinish();
    }

    public static int checkMinuteInQuarter(Date quarterStart, Date dateSample) {
        int returnedValue = 99;

        for (int k = 0; k < 8; k++) {
            Calendar startMin = resetDateFrom(quarterStart);
            startMin.add(Calendar.MINUTE, (2 * k));

            Calendar endMin = Calendar.getInstance();
            endMin.setTime(startMin.getTime());
            endMin.add(Calendar.MINUTE, 2);

            boolean betweenStartAndEndMin = dateSample.before(endMin.getTime()) && dateSample.after(startMin.getTime());

            if (betweenStartAndEndMin) {
                returnedValue = k;
                return returnedValue;
            }
        }
        return returnedValue;
    }

    public static Calendar resetDateFrom(Date fromDate) {
        Calendar returnedDate = Calendar.getInstance();
        returnedDate.setTime(fromDate);
        return returnedDate;
    }

    public static void comparingArrayListHR1755(List<DailyActivity> activityList, List<StepDataDay> listStepsAllDays) {
        Collections.reverse(activityList);
        for (int i = 0; i < activityList.size(); i++) {
            activityList.get(i).setSteps((int) listStepsAllDays.get(i).getSteps());
            activityList.get(i).setDistance((float) listStepsAllDays.get(i).getDistance() * 1000);
            activityList.get(i).setCalories((float) listStepsAllDays.get(i).getCalories());
        }
        if (mListenerHR1755 != null)
            mListenerHR1755.onTreatmentStepsIsFinish();
        listStepsAllDays.clear();
    }

    public static void comparingArrayListHR1810G(List<DailyActivity> activityList, List<StepDataDay> listStepsAllDays) {
        Collections.reverse(activityList);
        for (int i = 0; i < activityList.size(); i++) {
            activityList.get(i).setSteps((int) listStepsAllDays.get(i).getSteps());
            activityList.get(i).setDistance((float) listStepsAllDays.get(i).getDistance() * 1000);
            activityList.get(i).setCalories((float) listStepsAllDays.get(i).getCalories());
        }
        if (mListenerHR1810G != null)
            mListenerHR1810G.onTreatmentStepsIsFinish();
        listStepsAllDays.clear();
    }

    public static void getPercentageOperationHR1755(int j, int[] values, SynchronizeBleNotifyCallbackHR1755.ProgressTask mProgress, int typeMessage) {
        if (j == values[0])
            mProgress.ProgressUpdate(25, typeMessage);
        else if (j == values[1])
            mProgress.ProgressUpdate(50, typeMessage);
        else if (j == values[2])
            mProgress.ProgressUpdate(75, typeMessage);
    }

    public static void getPercentageOperationHR1810G(int j, int[] values, SynchronizeBleNotifyCallBackHR1810G.ProgressTask mProgress, int typeMessage) {
        if (j == values[0])
            mProgress.ProgressUpdate(25, typeMessage);
        else if (j == values[1])
            mProgress.ProgressUpdate(50, typeMessage);
        else if (j == values[2])
            mProgress.ProgressUpdate(75, typeMessage);
    }

    public static float stepsPercentage(int stepSample, float totalStepsTenMinutes, float totalDistanceCalories) {
        float percentageStepsCalorieSample = (float) ((stepSample * 100.0) / totalStepsTenMinutes);
        return (float) ((percentageStepsCalorieSample * totalDistanceCalories) / 100.0);
    }


    // CONVERSIONS FOR TEMPERATURE

    // Get a Decimal Temperature

    public static float getDecimalTemperature(int intTemp) {
        int hexToInt = Integer.parseInt(String.valueOf(intTemp), 16);
        float determinant = (float) 10.0;
        float decDouble = (float) (hexToInt + 100.0);
        float decTemp =  decDouble / determinant;
        return decTemp;
    }


    // Conversion of Time in String to time in long (timeStamp)


    public static long getTimeStamp(String strDate) throws ParseException {

        Date theDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(strDate);
        Timestamp ts=new Timestamp(theDate.getTime());
        return ts.getTime();

    }


    // Conversion Date to String

    public static String dateToString(int year, int month, int day , int hour, int minute, int second) {

        String sDay = Integer.toString(day);
        String sMonth = Integer.toString(month);
        String sYear = Integer.toString(year);

        String sHour = Integer.toString(hour);
        String sMinute = Integer.toString(minute);
        String sSecond = Integer.toString(second);

        if (sYear.toString().length() < 3) sYear = "20" + year; // to get Four 4 digits numbers for Year : yyyy exemple 20 to 2020
        if (isUnderTen(sMonth)) sMonth = "0" + month;     // example : 3/5/2020 to 03/05/2020
        if (isUnderTen(sDay)) sDay = "0" + day;
        if (isUnderTen(sHour)) sHour = "0" + hour;
        if (isUnderTen(sMinute)) sMinute = "0" + minute;
        if (isUnderTen(sSecond)) sSecond = "0" + second;


            return
                    sYear  +
                    "/" +  sMonth +
                    "/" +  sDay +
                    " "  + sHour +
                    ":" +  sMinute +
                    ":" +  sSecond;

    }


    // Check if we have TWO digits numbers

    public static Boolean isUnderTen(String number) {

        Boolean numb = false;
        if (number.length()<2) numb = true;

        return numb;
    }





}