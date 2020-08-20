package com.narimene.temal.pedometer.pedometer.HR1755.commands;

import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.ActivityPeriod;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.AutoHeartRateData;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.BasicParametersDevice;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.DateHour;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.ExerciseData;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.HRVData;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.HeartRateDetailed;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.RealTimeData;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.SleepDebugging;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.SleepDetailed;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.SoftwareVersion;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDataDay;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDetailed;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.UserPersonalData;
import fr.semantic.ecare.app.android.components.sensor.pedometer.common.PedometerResponse;
import fr.semantic.ecare.app.android.components.sensor.utils.Convert;
import fr.semantic.ecare.app.android.modules.patient.walk.minutewalk.left.WalkLeftFragment;

/**
 * Class used to load Data from the Pedometer HR1755 J-style
 */

public class PedometerResponseHR1755 extends PedometerResponse{


    public static PedometerResponseHR1755 newInstance(){
        return new PedometerResponseHR1755();
    }

    /**
     * Get Time method
     */
    public static String getTime(byte[] commandReceived) {
        DateHour date = DateHour.getInstance();

        date.setYear       (Integer.parseInt(byteHexToString(commandReceived[3])));
        date.setMonth      (Integer.parseInt(byteHexToString(commandReceived[2])));
        date.setDayOfWeek  (Integer.parseInt(byteHexToString(commandReceived[1])));
        date.setHourStart  (Integer.parseInt(byteHexToString(commandReceived[4])));
        date.setMinuteStart(Integer.parseInt(byteHexToString(commandReceived[5])));
        date.setSecondStart(Integer.parseInt(byteHexToString(commandReceived[6])));

        return date.toString();
    }

    //==============================================================================================
    // Methods for Steps
    //==============================================================================================
    /**
     * Necessary variables to load Steps && Sleeps
     */
    private static final int END_MESSAGE = -1;

    private static OnLoadDataPedometerHR1755 mListener = null;

    public static OnLoadDataPedometerHR1755 getListener() {
        return mListener;
    }

    public static void setListener(OnLoadDataPedometerHR1755 mListener) {
        PedometerResponseHR1755.mListener = mListener;
    }

    public static void setLastNecessarySampleStepsIsLoaded(boolean lastNecessarySampleStepsIsLoaded) {
        PedometerResponseHR1755.lastNecessarySampleStepsIsLoaded = lastNecessarySampleStepsIsLoaded;
    }

    public static void setLastNecessarySampleSleepIsLoaded(boolean lastNecessarySampleSleepIsLoaded) {
        PedometerResponseHR1755.lastNecessarySampleSleepIsLoaded = lastNecessarySampleSleepIsLoaded;
    }

    /**
     * Necessary variables to Steps (not details Steps)
     */
    private static List<Byte> allStepsMessages = new ArrayList<Byte>();
    private static List<StepDataDay> globalListAllStepsActivityObject = new ArrayList<StepDataDay>();

    public static void getAllStepsData(byte[] commandReceived){

        int indexEndMessages = commandReceived.length;

        switch (commandReceived[indexEndMessages - 1 ]){

            case END_MESSAGE:
                putCommandsInArrayList(commandReceived, allStepsMessages);
                int len = allStepsMessages.size();
                int numberOfDay = (( len - 2 ) / 27); //enlever tjrs 2, la taille de la trame est de 27 bytes
                int indexCompter = 0;

                for (int i = 0 ; i < numberOfDay ; i++){
                    StepDataDay day = StepDataDay.getInstance();

                    day.setDayId(allStepsMessages.get(1 + indexCompter));
                    day.setYear(byteHexToInteger(allStepsMessages.get(2+indexCompter)));
                    day.setMonth(byteHexToInteger(allStepsMessages.get(3+indexCompter)));
                    day.setDayOfWeek(byteHexToInteger(allStepsMessages.get(4+indexCompter)));

                    day.setSteps(getSumDataInForBytes(allStepsMessages.get(8+indexCompter), allStepsMessages.get(7+indexCompter),
                            allStepsMessages.get(6+indexCompter), allStepsMessages.get(5+indexCompter)));

                    day.setActivityTime(getSumDataInForBytes(allStepsMessages.get(12+indexCompter), allStepsMessages.get(11+indexCompter),
                            allStepsMessages.get(10+indexCompter), allStepsMessages.get(9+indexCompter))); // all Seconds

                    day.setDetailedActivityTime(getDetailedTime(allStepsMessages.get(12+indexCompter), allStepsMessages.get(11+indexCompter),
                            allStepsMessages.get(10+indexCompter), allStepsMessages.get(9+indexCompter))); // hour, min, sec

                    day.setDistance((getSumDataInForBytes(allStepsMessages.get(16+indexCompter), allStepsMessages.get(15+indexCompter),
                            allStepsMessages.get(14+indexCompter), allStepsMessages.get(13+indexCompter))) / 100); // to KM

                    day.setCalories((getSumDataInForBytes(allStepsMessages.get(20+indexCompter), allStepsMessages.get(19+indexCompter),
                            allStepsMessages.get(18+indexCompter), allStepsMessages.get(17+indexCompter))) / 100);  // to KCAL

                    day.setTarget(getSumDataInTwoBytes(allStepsMessages.get(22+indexCompter), allStepsMessages.get(21+indexCompter)));

                    day.setIntensiveMinutes(getSumDataInForBytes(allStepsMessages.get(26+indexCompter), allStepsMessages.get(25+indexCompter),
                            allStepsMessages.get(24+indexCompter), allStepsMessages.get(23+indexCompter)));

                    globalListAllStepsActivityObject.add(day);
                    indexCompter += 27;
                }

                allStepsMessages.clear();
                if (mListener != null)
                    mListener.onLoadingStepsDaysIsFinish(globalListAllStepsActivityObject);

                return ;

            case -22: // command remove all steps // EA en Hexa
                allStepsMessages.clear();
                return ;

            default: // quand on a une trame qui va etre surement suivie par une autre apres
                putCommandsInArrayList(commandReceived, allStepsMessages);
        }
        return ;
    }

    /**
     * Necessary variables to load details Steps Data
     */

    // Attention a cette méthode, les derniere trame de retour n'indique pas que c'est la derniere comme le reste des commandes
    // on recoit des trames de 200 bytes , l'information est sur 25 bytes, 200/25 = 8 infos par trame
    // le code passe plusieurs fois pour un seul appuye sur sendMessage
    private static final int FRAME_LENGTH_DETAILS_STEP      = 50;
    private static List<Byte> detailedStepsDataByteList     = new ArrayList<Byte>();
    private static List<StepDetailed> detailedStepsDataList = new ArrayList<StepDetailed>();
    private static boolean lastNecessarySampleStepsIsLoaded       = false;
    private static int counterFrameReceivedFromDevice        = 0;

    public static void getDetailedStepsData(byte[] commandReceived, int mDaysAgo) {
        counterFrameReceivedFromDevice++;
        putCommandsInArrayList(commandReceived, detailedStepsDataByteList);

        int length = commandReceived.length ;
        int numberOfFrame;

        if (length != 200 && commandReceived[length - 1] == END_MESSAGE) { // derniere trame qu'on va recevoir

            if (length < 27){ // il n'y a plus d'informations
                Log.i("Fin " , "List finie");
                return ;
            }
            numberOfFrame = ((length - 2) / 25); //derniere trame avec information

        }else // trame sans indication de fin
            numberOfFrame = length / 25;

        if (!lastNecessarySampleStepsIsLoaded)
            loadDetailsStepsData(numberOfFrame);

        detailedStepsDataByteList.clear();

        try {
            //
            if (lastNecessarySampleStepsIsLoaded){return ;}
            if (Tools.checkWithAcquisitionDate(detailedStepsDataList.get(detailedStepsDataList.size() - 1), mDaysAgo)){
                lastNecessarySampleStepsIsLoaded = true;
                counterFrameReceivedFromDevice = 0;
                Tools.getOnlyNecessarySample(detailedStepsDataList, mDaysAgo);
                if (mListener != null)
                    mListener.onLoadingStepsIsFinish(detailedStepsDataList);
            }
        } catch (ParseException e) {e.printStackTrace();}

        // demander la suite des data si les premiere 50 trames ne suffisent pas
        if (counterFrameReceivedFromDevice == FRAME_LENGTH_DETAILS_STEP && !lastNecessarySampleStepsIsLoaded){
            counterFrameReceivedFromDevice = 0;
            if (mListener != null)
                mListener.onInsufficientLoadingSteps();
        }
    }

    public static void loadDetailsStepsData(int numberOfFrames) {

        int indexCounter = 0;
        for (int i = 0; i < numberOfFrames; i++) {
            StepDetailed sample = StepDetailed.getInstance();

            sample.setYear(byteHexToInteger       (detailedStepsDataByteList.get(3 + indexCounter)));
            sample.setMonth(byteHexToInteger      (detailedStepsDataByteList.get(4 + indexCounter)));
            sample.setDayOfWeek(byteHexToInteger  (detailedStepsDataByteList.get(5 + indexCounter)));
            sample.setHourStart(byteHexToInteger  (detailedStepsDataByteList.get(6 + indexCounter)));
            sample.setMinuteStart(byteHexToInteger(detailedStepsDataByteList.get(7 + indexCounter)));
            sample.setSecondStart(byteHexToInteger(detailedStepsDataByteList.get(8 + indexCounter)));

            sample.setCalories((getSumDataInTwoBytes(detailedStepsDataByteList.get(12 + indexCounter),
                    detailedStepsDataByteList.get(11 + indexCounter)) * 1.0) / 100);  // to KCAL

            sample.setDistance((getSumDataInTwoBytes(detailedStepsDataByteList.get(14 + indexCounter),
                    detailedStepsDataByteList.get(13 + indexCounter)) * 1.0) / 100); // to KM

            int[] detailsPerMinute = new int[10];
            int sumSteps = 0;

            for (int j = 15 + indexCounter, z = 0; j < 25 + indexCounter && z < 10; j++, z++) {
                detailsPerMinute[z] = unsignedToBytes(detailedStepsDataByteList.get(j));
                sumSteps += detailsPerMinute[z];
            }

            sample.setSteps(sumSteps);
            sample.setStepsPerMinute(detailsPerMinute);

            indexCounter += 25;
            detailedStepsDataList.add(sample);
        }
    }

    /**
     * Necessary variables to load Sleep Data
     */

    private static final int FRAME_LENGTH_DETAILS_SLEEP = 50;
    private static List<Byte> detailedSleepDataByteList = new ArrayList<Byte>();
    private static List<SleepDetailed> detailedSleepDataList = new ArrayList<SleepDetailed>();
    private static boolean lastNecessarySampleSleepIsLoaded = false; //170
    private static int counterFrameReceivedFromDeviceSleep = 0;

    public static void getDetailedSleepData(byte[] commandReceived, int mDaysAgo){
        counterFrameReceivedFromDeviceSleep++;
        putCommandsInArrayList(commandReceived, detailedSleepDataByteList);

        int length = commandReceived.length ;
        int numberOfFrame;

        if (length != 170 && commandReceived[length - 1] == END_MESSAGE) { // derniere trame qu'on va recevoir

            if (length < 36){ // il n'y a plus d'informations
                Log.i("Fin " , "List finie");
                return ;
            }
            numberOfFrame = ((length - 2) / 34); //derniere trame avec information

        }else // trame sans indication de fin
            numberOfFrame = length / 34;

        if (!lastNecessarySampleSleepIsLoaded)
            loadDetailsSleepData(numberOfFrame);

        detailedSleepDataByteList.clear();

        try {
            if (lastNecessarySampleSleepIsLoaded){return ;}
            if (Tools.checkWithAcquisitionDate(detailedSleepDataList.get(detailedSleepDataList.size() - 1), mDaysAgo)){
                lastNecessarySampleSleepIsLoaded = true;
                counterFrameReceivedFromDeviceSleep = 0;
                Tools.getOnlyNecessarySample(detailedSleepDataList, mDaysAgo);
                if (mListener != null)
                    mListener.onLoadingSleepIsFinish(detailedSleepDataList);
            }
        } catch (ParseException e) {e.printStackTrace();}

        // demander la suite des data si les premiere 50 trames ne suffisent pas
        if (counterFrameReceivedFromDeviceSleep == FRAME_LENGTH_DETAILS_SLEEP && !lastNecessarySampleSleepIsLoaded){
            counterFrameReceivedFromDeviceSleep = 0;
            if (mListener != null)
                mListener.onInsufficientLoadingSleep();
        }
    }

    public static void loadDetailsSleepData(int numberOfFrames) {
        int indexCounter = 0;

        for (int i = 0; i < numberOfFrames; i++) {
            SleepDetailed sample = SleepDetailed.getInstance();

            sample.setYear       (byteHexToInteger(detailedSleepDataByteList.get(3+indexCounter)));
            sample.setMonth      (byteHexToInteger(detailedSleepDataByteList.get(4+indexCounter)));
            sample.setDayOfWeek  (byteHexToInteger(detailedSleepDataByteList.get(5+indexCounter)));
            sample.setHourStart  (byteHexToInteger(detailedSleepDataByteList.get(6+indexCounter)));
            sample.setMinuteStart(byteHexToInteger(detailedSleepDataByteList.get(7+indexCounter)));
            sample.setSecondStart(byteHexToInteger(detailedSleepDataByteList.get(8+indexCounter)));

            sample.setLengthSleepData(unsignedToBytes(detailedSleepDataByteList.get(9+indexCounter)));

            int [] detailedPerFiveMinutes = new int[24];

            for (int j= 10+indexCounter, z = 0 ; j < 34 + indexCounter &&  z < 24 ; j++ , z++){
                detailedPerFiveMinutes[z] = unsignedToBytes(detailedSleepDataByteList.get(j));
            }
            sample.setSleepPerFiveMinute(detailedPerFiveMinutes);
            detailedSleepDataList.add(sample);
            indexCounter += 34;
        }
    }

    //==============================================================================================
    // Methods for Real Time Data
    //==============================================================================================

    public static void getRealTime(byte[] commandReceived){
        RealTimeData data = RealTimeData.getInstance();

        data.setSteps(getSumDataInForBytes(  commandReceived[4]
                , commandReceived[3]
                , commandReceived[2]
                , commandReceived[1]));

        data.setCalories(((getSumDataInForBytes(commandReceived[8],
                commandReceived[7],
                commandReceived[6],
                commandReceived[5]))
                * 1.0)/ 100);
        data.setDistance((((getSumDataInForBytes(commandReceived[12],
                commandReceived[11],
                commandReceived[10],
                commandReceived[9]))
                * 1.0)/ 100) * 1000);

        double secondsTimeActivity = (getSumDataInForBytes( commandReceived[16],
                commandReceived[15],
                commandReceived[14],
                commandReceived[13]));
        data.setTimeActivity(splitToComponentTimes(secondsTimeActivity));

        double secondsTimeFastActivity = (getSumDataInForBytes(commandReceived[20],
                commandReceived[19],
                commandReceived[18],
                commandReceived[17]));
        data.setTimeFastActivity(splitToComponentTimes(secondsTimeFastActivity));

        data.setHeartRate(unsignedToBytes(commandReceived[21]));

        /**
         * {@link WalkLeftFragment#onStartRealTime()}
         */
        if (mListenerRealTime != null && isFirstRequestRealTime) {
            isFirstRequestRealTime = false;
            mListenerRealTime.onStartRealTime();
        }

        /**
         * {@link WalkLeftFragment#onUpdateRealTime(RealTimeData)} ()}
         */
        else if (mListenerRealTime != null && !isFirstRequestRealTime) {
            mListenerRealTime.onUpdateRealTime(data);
        }

    }


    //==============================================================================================
    // Methods for Heart Rate data
    //==============================================================================================

    public static List<Byte> allDetailsHeartRateData = new ArrayList<Byte>();
    public static String getDetailedHeartRateData(byte[] commandReceived){
        List<HeartRateDetailed> globalListAllDetailedHeartRateObject = new ArrayList<HeartRateDetailed>();

        int indexEndMessages = commandReceived.length;
        switch (commandReceived[indexEndMessages - 1 ]){
            case END_MESSAGE:
                putCommandsInArrayList(commandReceived, allDetailsHeartRateData);
                int len = allDetailsHeartRateData.size();
                int numberOfFrame = (( len - 2 ) / 24); //enlever tjrs 2, la taille de la trame est de 24 bytes
                int indexCompter = 0;

                for (int i = 0 ; i < numberOfFrame ; i++){
                    HeartRateDetailed sample = HeartRateDetailed.getInstance();

                    //sample.setSampleId(allStepsMessages.get(1 + indexCompter));
                    sample.setYear       (byteHexToInteger(allDetailsHeartRateData.get(3+indexCompter)));
                    sample.setMonth      (byteHexToInteger(allDetailsHeartRateData.get(4+indexCompter)));
                    sample.setDayOfWeek  (byteHexToInteger(allDetailsHeartRateData.get(5+indexCompter)));
                    sample.setHourStart  (byteHexToInteger(allDetailsHeartRateData.get(6+indexCompter)));
                    sample.setMinuteStart(byteHexToInteger(allDetailsHeartRateData.get(7+indexCompter)));
                    sample.setSecondStart(byteHexToInteger(allDetailsHeartRateData.get(8+indexCompter)));


                    int [] detailedPerFiveMinutes = new int[15];

                    for (int j= 9+indexCompter, z = 0 ; j < 24 + indexCompter &&  z < 15 ; j++ , z++){
                        detailedPerFiveMinutes[z] = unsignedToBytes(allDetailsHeartRateData.get(j));
                    }
                    sample.setHeartRateMinutes(detailedPerFiveMinutes);
                    globalListAllDetailedHeartRateObject.add(sample);
                    indexCompter += 24;
                }

                String returnedText = "";
                for (HeartRateDetailed mSample : globalListAllDetailedHeartRateObject){
                    returnedText += mSample.toString() + "\n";
                }
                System.out.println(returnedText);
                allDetailsHeartRateData.clear();
                globalListAllDetailedHeartRateObject.clear();
                return "La liste sera affiché dans le logCat du Pc";

            case -21: // command remove all steps // EB
                allDetailsHeartRateData.clear();
                return "Supression OK";

            default:
                putCommandsInArrayList(commandReceived, allDetailsHeartRateData);
        }
        return "";

    }

    public static String getSingleHRData(byte[] commandReceived){

        return "";
    }

    public static List<Byte> allHRVData= new ArrayList<Byte>();
    public static String getHRVData(byte[] commandReceived){

        List<HRVData> globalListAllHRVObject = new ArrayList<HRVData>();

        int indexEndMessages = commandReceived.length;
        switch (commandReceived[indexEndMessages - 1 ]){

            case END_MESSAGE:
                putCommandsInArrayList(commandReceived, allHRVData);
                int len = allHRVData.size();
                int numberOfFrame = (( len - 2 ) / 15); //enlever tjrs 2, la taille de la trame est de 15 bytes
                int indexCompter = 0;

                for (int i = 0 ; i < numberOfFrame ; i++){
                    HRVData sample = HRVData.getInstance();

                    //sample.setSampleId(allStepsMessages.get(1 + indexCompter));
                    sample.setYear       (byteHexToInteger(allHRVData.get(3+indexCompter)));
                    sample.setMonth      (byteHexToInteger(allHRVData.get(4+indexCompter)));
                    sample.setDayOfWeek  (byteHexToInteger(allHRVData.get(5+indexCompter)));
                    sample.setHourStart  (byteHexToInteger(allHRVData.get(6+indexCompter)));
                    sample.setMinuteStart(byteHexToInteger(allHRVData.get(7+indexCompter)));
                    sample.setSecondStart(byteHexToInteger(allHRVData.get(8+indexCompter)));
                    // 9th parameter ? don' know ?
                    sample.setHRV(unsignedToBytes(allHRVData.get(10+indexCompter)));
                    sample.setHR(unsignedToBytes(allHRVData.get(11+indexCompter)));
                    sample.setVascularOcclusion(unsignedToBytes(allHRVData.get(12+indexCompter)));
                    sample.setHighRate(unsignedToBytes(allHRVData.get(13+indexCompter)));
                    sample.setLowRate(unsignedToBytes(allHRVData.get(15+indexCompter)));

                    globalListAllHRVObject.add(sample);
                    indexCompter += 15;
                }

                String returnedText = "";
                for (HRVData mSample : globalListAllHRVObject){
                    returnedText += mSample.toString() + "\n";
                }
                System.out.println(returnedText);
                allHRVData.clear();
                globalListAllHRVObject.clear();
                return "La liste sera affiché dans le logCat du Pc";

            case -21: // command remove all steps // EB
                allHRVData.clear();
                return "Supression OK";

            default: // quand on a une trame qui va etre surement suivie par une autre apres
                putCommandsInArrayList(commandReceived, allHRVData);
        }
        return "";
    }

    public static String getAlarmsData(byte[] commandReceived){

        return "";
    }


    // Attention a cette méthode, les derniere trame de retour n'indique pas que c'est la derniere comme le reste des commandes
    // on recoit des trames de 190 bytes , l'information est sur 19 bytes, 190/19 = 10 infos par trame
    public static List<Byte> allSleepDebuggingData= new ArrayList<Byte>();

    public static String getSleepDebuggingData(byte[] commandReceived){
        putCommandsInArrayList(commandReceived, allSleepDebuggingData);
        List<SleepDebugging> globalListAllSleepDebuggingObject = new ArrayList<SleepDebugging>();
        int len = commandReceived.length / 19;
        int indexCompter = 0;

        if (commandReceived.length % 19 != 0){
            return "Erreur de taille de tram , sur cette commande il le constructeur ne donne pas un message qui indique la fin de la trame";
        }

        for (int i=0 ; i < len ; i++) {

            SleepDebugging sample = SleepDebugging.getInstance();

            //sample.setSampleId(allStepsMessages.get(1 + indexCompter));
            sample.setYear(byteHexToInteger(allSleepDebuggingData.get(3 + indexCompter)));
            sample.setMonth(byteHexToInteger(allSleepDebuggingData.get(4 + indexCompter)));
            sample.setDayOfWeek(byteHexToInteger(allSleepDebuggingData.get(5 + indexCompter)));
            sample.setHourStart(byteHexToInteger(allSleepDebuggingData.get(6 + indexCompter)));
            sample.setMinuteStart(byteHexToInteger(allSleepDebuggingData.get(7 + indexCompter)));
            sample.setSecondStart(byteHexToInteger(allSleepDebuggingData.get(8 + indexCompter)));

            int[] detailedPerFiveMinutes = new int[10];

            for (int j = 9 + indexCompter, z = 0; j < 19 + indexCompter && z < 10; j++, z++) {
                detailedPerFiveMinutes[z] = unsignedToBytes(allSleepDebuggingData.get(j));
            }
            sample.setSleepDebugingPerMinute(detailedPerFiveMinutes);
            indexCompter += 19;
            globalListAllSleepDebuggingObject.add(sample);
        }

        String returnedText = "";
        for (SleepDebugging mSample : globalListAllSleepDebuggingObject){
            returnedText += mSample.toString() + "\n";
        }
        System.out.println(returnedText);
        allSleepDebuggingData.clear();
        globalListAllSleepDebuggingObject.clear();

        return "Voir sur logCat du pc svp" ;
    }

    public static String getGPSData(byte[] commandReceived){

        return "";
    }

    public static List<Byte> allExerciseData= new ArrayList<Byte>();
    public static String getExerciseData(byte[] commandReceived){

        List<ExerciseData> globalListExerciseObject = new ArrayList<ExerciseData>();

        int indexEndMessages = commandReceived.length;
        switch (commandReceived[indexEndMessages - 1 ]){

            case END_MESSAGE:
                putCommandsInArrayList(commandReceived, allExerciseData);
                int len = allExerciseData.size();
                int numberOfFrame = (( len - 2 ) / 25);
                int indexCompter = 0;

                for (int i = 0 ; i < numberOfFrame ; i++){
                    ExerciseData sample = ExerciseData.getInstance();

                    sample.setYear       (byteHexToInteger(allExerciseData.get(3+indexCompter)));
                    sample.setMonth      (byteHexToInteger(allExerciseData.get(4+indexCompter)));
                    sample.setDayOfWeek  (byteHexToInteger(allExerciseData.get(5+indexCompter)));
                    sample.setHourStart  (byteHexToInteger(allExerciseData.get(6+indexCompter)));
                    sample.setMinuteStart(byteHexToInteger(allExerciseData.get(7+indexCompter)));
                    sample.setSecondStart(byteHexToInteger(allExerciseData.get(8+indexCompter)));
                    sample.setTypeExercise(unsignedToBytes(allExerciseData.get(9+indexCompter)));
                    sample.setHeartRate   (unsignedToBytes(allExerciseData.get(10+indexCompter)));
                    sample.setExerciseTime(getSumDataInTwoBytesAndConvertToTime(allExerciseData.get(12 + indexCompter), allExerciseData.get(11 + indexCompter)));
                    sample.setSteps(getSumDataInTwoBytes(allExerciseData.get(14 + indexCompter), allExerciseData.get(13 + indexCompter)));
                    sample.setExerciseSpeed(unsignedToBytes(allExerciseData.get(15 +indexCompter))
                            + "'"
                            + unsignedToBytes(allExerciseData.get(16 +indexCompter)));

                    sample.setCalories(Convert.fourByteToInt(allExerciseData.get(20+indexCompter), allExerciseData.get(19+indexCompter),
                            allExerciseData.get(18+indexCompter), allExerciseData.get(17+indexCompter)) );

                    sample.setDistance(Convert.fourByteToInt(allExerciseData.get(24+indexCompter), allExerciseData.get(23+indexCompter),
                            allExerciseData.get(22+indexCompter), allExerciseData.get(21+indexCompter))); // to KM

                    globalListExerciseObject.add(sample);
                    indexCompter += 25;
                }

                String returnedText = "";
                for (ExerciseData mSample : globalListExerciseObject){
                    returnedText += mSample.toString() + "\n";
                }
                System.out.println(returnedText);
                allExerciseData.clear();
                globalListExerciseObject.clear();
                return "La liste sera affichée dans le logCat du Pc";

            case -21:
                allExerciseData.clear();
                return "Supression OK";
            default:
                putCommandsInArrayList(commandReceived, allExerciseData);
        }
        return "";
    }

    public static String enableGPS(byte[] commandReceived){
        return "regarder l'ecran de la montre pour voir, la trame d'activation et desactivation est la meme";
    }

    public static String checkCommandFromDevice(byte[] commandReceived){
        switch (unsignedToBytes(commandReceived[1])){
            case 2:
                return "le device demande l'ouverture de l'appreil photo";
            case 4:
                return "le device est entrain de recherche de la tablette";
            case 1:
                return "le device demande de gerer l'appel entrant ";
            case 3:
                return "le device demande de controler la musique";
            default:
                return "";
        }
    }

    public static String getPersonalData(byte[] commandReceived){
        UserPersonalData user = new UserPersonalData();

        user.setGender         (unsignedToBytes(commandReceived[1]));
        user.setAge            (unsignedToBytes(commandReceived[2]));
        user.setHeight         (unsignedToBytes(commandReceived[3]));
        user.setWeight         (unsignedToBytes(commandReceived[4]));
        user.setStrideLength   (unsignedToBytes(commandReceived[5]));

        return user.toString();
    }


    public static String getBasicData(byte[] commandReceived){

        BasicParametersDevice basicParameters = new BasicParametersDevice();

        basicParameters.setDistance_unit   (unsignedToBytes(commandReceived[1]));
        basicParameters.setHour_unit       (unsignedToBytes(commandReceived[2]));
        basicParameters.setWrist_sens      (unsignedToBytes(commandReceived[3]));
        basicParameters.setChoice_hand     (unsignedToBytes(commandReceived[4]));
        basicParameters.setPosition_display(unsignedToBytes(commandReceived[5]));
        basicParameters.setANCS            (unsignedToBytes(commandReceived[6]));

        return basicParameters.toString();
    }

    public static String getTargetSteps(byte[] commandReceived){
        double steps = getSumDataInForBytes(commandReceived[4],
                commandReceived[3],
                commandReceived[2],
                commandReceived[1]);
        return steps + " steps";
    }

    public static String getBatteryLevel(byte[] commandReceived){
        int batteryLevel = unsignedToBytes(commandReceived[1]);
        return "Taux de charge de la batterie : " + batteryLevel + "%";
    }

    public static String getSoftwareVersion(byte[] commandReceived){
        SoftwareVersion softwareVersion = SoftwareVersion.getInstance();

        softwareVersion.setHight              (unsignedToBytes(commandReceived[1]));
        softwareVersion.setMedium             (unsignedToBytes(commandReceived[2]));
        softwareVersion.setLow                (unsignedToBytes(commandReceived[3]));
        softwareVersion.setHyperLow           (unsignedToBytes(commandReceived[4]));
        softwareVersion.getDate().setYear     (Integer.parseInt(byteHexToString(commandReceived[5])));
        softwareVersion.getDate().setMonth    (Integer.parseInt(byteHexToString(commandReceived[6])));
        softwareVersion.getDate().setDayOfWeek(Integer.parseInt(byteHexToString(commandReceived[7])));

        return softwareVersion.toString();
    }

    public static String getGetAutoHeartRateCheckPeriod(byte[] commandReceived){

        if (unsignedToBytes(commandReceived[1]) == 0) {
            return " Mode Off";
        }
        AutoHeartRateData data = AutoHeartRateData.getInstance();

        data.setMode       (unsignedToBytes(commandReceived[1]));
        data.setStartHour  (Integer.parseInt(byteHexToString(commandReceived[2])));
        data.setStartMinute(Integer.parseInt(byteHexToString(commandReceived[3])));
        data.setEndHour    (Integer.parseInt(byteHexToString(commandReceived[4])));
        data.setEndMinute  (Integer.parseInt(byteHexToString(commandReceived[5])));
        data.setDaysText   (activateDaysForWeek(commandReceived[6]));

        if (unsignedToBytes(commandReceived[1]) == 1){
            data.setInterval(-1);
            return data.toString();
        }else {
            data.setInterval(getSumDataInTwoBytes(commandReceived[7], commandReceived[8]));
            return data.toString();
        }
    }

    public static String setAlarms(byte[] commandReceived){
        int alarmNumber = unsignedToBytes(commandReceived[1]) + 1;
        return "Commande reçu Alarme " + alarmNumber;
    }

    public static String getActivityPeriod(byte[] commandReceived){
        ActivityPeriod activity = ActivityPeriod.getInstance();

        activity.setStartHour       (Integer.parseInt(byteHexToString(commandReceived[1])));
        activity.setStartMinute     (Integer.parseInt(byteHexToString(commandReceived[2])));
        activity.setEndHour         (Integer.parseInt(byteHexToString(commandReceived[3])));
        activity.setEndMinute       (Integer.parseInt(byteHexToString(commandReceived[4])));
        activity.setDaysText        (activateDaysForWeek(commandReceived[5]));
        activity.setRemindPeriod    (unsignedToBytes(commandReceived[6]));
        activity.setMinExerciseSteps(unsignedToBytes(commandReceived[7]));

        return activity.toString();
    }

    //==============================================================================================
    // Static service Methods
    //==============================================================================================
    public static double getSumDataInForBytes(byte byte4, byte byte3, byte byte2, byte byte1){
        return (unsignedToBytes(byte4) * 16777216) + (unsignedToBytes(byte3)* 65536 )+ (unsignedToBytes(byte2) * 256 )+ unsignedToBytes(byte1);
    }

    public static int getSumDataInTwoBytes (byte byte2, byte byte1){
        return (unsignedToBytes(byte2) * 256 )+ unsignedToBytes(byte1);
    }

    public static void putCommandsInArrayList(byte[] command, List<Byte> AllCommands){
        for (int i=0 ; i < command.length; i++){
            AllCommands.add(command[i]);
        }
    }

    public static int byteHexToInteger(byte byteHex){
     return Integer.parseInt(Integer.toHexString(byteHex));
    }

    public static String byteHexToString(byte command){
        return formatingString(Integer.toHexString(command));
    }

    public static int[] getDetailedTime(byte byte4, byte byte3, byte byte2, byte byte1 ){
        return splitToComponentTimes(getSumDataInForBytes(byte4, byte3, byte2, byte1));
    }

    public static int[] getSumDataInTwoBytesAndConvertToTime (byte byte2, byte byte1){
        return splitToComponentTimes((unsignedToBytes(byte2) * 256 ) + unsignedToBytes(byte1));
    }

    public static int[] splitToComponentTimes(double biggy)
    {
        int hours = (int) biggy / 3600;
        int remainder = (int) biggy - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        int[] ints = {hours , mins , secs};
        return ints;
    }

    public static String formatingString(String number) {

        if (number.length() == 0) {
            number = "00";
        } else if (number.length() == 1) {
            number = "0" + number;
        }
        return number;
    }

    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    public static String activateDaysForWeek(byte data){
        String days = String.format("%7s", Integer.toBinaryString(data)).replace(' ', '0');

        boolean [] allDays = new boolean[7];

        for (int i = 0; i < days.length() ; i++){
            allDays[i] = (days.charAt(i)  == '1' ) ? true : false;
        }
        String answerDays= "";

        if (allDays[0]) answerDays += "Samedi ,";
        if (allDays[1]) answerDays += "Vendredi ,";
        if (allDays[2]) answerDays += "Jeudi ,";
        if (allDays[3]) answerDays += "Mercredi ,";
        if (allDays[4]) answerDays += "Mardi ,";
        if (allDays[5]) answerDays += "Lundi ,";
        if (allDays[6]) answerDays += "Dimanche ,";

        return answerDays;
    }



}
