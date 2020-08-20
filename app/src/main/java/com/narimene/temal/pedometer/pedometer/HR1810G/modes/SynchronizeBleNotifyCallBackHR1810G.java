package com.narimene.temal.pedometer.pedometer.HR1810G.modes;

import android.os.AsyncTask;
import android.widget.Toast;

import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import fr.semantic.ecare.app.android.components.sensor.pedometer.ConversionData;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.SleepDetailed;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.SleepSampleFiveMinute;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDataDay;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDetailed;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepSampleMinute;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755HR1810GCommand;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.PedometerHR1810GBleGattCallBack;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.commands.OnLoadDataPedometerHR1810G;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.commands.PedometerResponseHR1810G;
import fr.semantic.ecare.app.android.domain.measure.MeasureModel;
import fr.semantic.ecare.app.android.domain.measure.pedometer.activity.DailyActivity;
import fr.semantic.ecare.app.android.domain.measure.pedometer.sleep.DailySleep;
import fr.semantic.ecare.app.android.utils.Log;
import fr.semantic.ecare.app.android.utils.ui.Bip;
import fr.semantic.ecare.app.android.utils.ui.ToastUtils;

public class SynchronizeBleNotifyCallBackHR1810G  extends BleNotifyCallback implements OnLoadDataPedometerHR1810G {

    /**
     * class Notify Pedometer HR1810G for Synchronize Mode only
     *
     * @version 1.3.0
     * @since 1.3.0
     */
        public final static String TAG = SynchronizeBleNotifyCallBackHR1810G.class.getSimpleName();

        private WeakReference<PedometerHR1810GBleGattCallBack> mActivity = null;
        private SynchronizeBleNotifyCallBackHR1810G.ProgressTask mProgress = null;

        private PedometerResponseHR1810G commands = null;
        private ConversionData converter = null;

        private boolean continueRequestSteps = true;
        private boolean continueRequestSleep = true;
        private boolean continueRequestTemperature = true;


        private List<DailyActivity> activityList ;
        private List<DailySleep> sleepList;
        private List<Byte> globalTemperatureList ;
        private List<MeasureModel> temperatureList;


        public SynchronizeBleNotifyCallBackHR1810G(PedometerHR1810GBleGattCallBack activity) {
            this.mActivity = new WeakReference<PedometerHR1810GBleGattCallBack>(activity);
            activityList = new ArrayList<>();
            sleepList    = new ArrayList<>();
            temperatureList    = new ArrayList<>();
            globalTemperatureList = new ArrayList<>();
        }

        public PedometerHR1810GBleGattCallBack getActivity(){
            return mActivity.get();
        }

        public BleDevice getDevice(){
            return getActivity().getDevice();
        }

        public int getDaysAgo(){
            return getActivity().getDaysAgo();
        }

        @Override
        public void onNotifySuccess() {
            Log.i(TAG, "onNotifySuccess Synchronize mode");
            getActivity().setTime(getDevice());
            getActivity().vibrate(getDevice(), 1);
        }

        @Override
        public void onNotifyFailure(BleException exception) {
            Log.e(TAG, "onNotifyFailure Synchronize mode: " + exception.getDescription());
        }

        @Override
        public void onCharacteristicChanged(byte[] data) {
            Log.d(TAG, "onCharacteristicChanged Synchronize mode");

            switch (data[0]) {

                case HR1755HR1810GCommand.CMD_SET_TIME:
                    Log.d(TAG, "Time set");
                    getActivity().setBasicParameters(getDevice());
                    break;

                case HR1755HR1810GCommand.CMD_SET_BASIC_PARAMETERS:
                    Log.d(TAG, "Basic parameters set");
                    getActivity().setFootsteepGoal(getDevice());
                    break;

                case HR1755HR1810GCommand.CMD_SET_FOOTSTEP_GOAL:
                    Log.d(TAG, "Footsteep goal set");
                    ToastUtils.toast("Objectif envoyé", Toast.LENGTH_SHORT, false);
                    getActivity().setPersonalData(getDevice());
                    break;

                case HR1755HR1810GCommand.CMD_SET_PERSONAL_DATA:
                    Log.d(TAG, "Personal data set");
                    ToastUtils.toast("Données personnelles à jour", Toast.LENGTH_SHORT, false);
                    prepareParametersToLoadActivityAndSleep();
                    getActivity().getDetailsActivity(getDevice());
                    break;

                case HR1755HR1810GCommand.CMD_GET_DETAILED_STEPS_DATA:
                    if (continueRequestSteps) {
                        Log.d(TAG, "Reception Details Steps data");
                        PedometerResponseHR1810G.getDetailedStepsData(data, getDaysAgo());
                    }
                    break;

                case HR1755HR1810GCommand.CMD_GET_ALL_STEPS_DATA:
                    PedometerResponseHR1810G.getAllStepsData(data);
                    break;

                case HR1755HR1810GCommand.CMD_GET_DETAILED_SLEEP_DATA:
                    if (continueRequestSleep) {
                        Log.i(TAG, "HR1755HR1810GCommand.CMD_GET_DETAILED_SLEEP_DATA");
                        PedometerResponseHR1810G.getDetailedSleepData(data, getDaysAgo());
                    }
                    break;


                case HR1755HR1810GCommand.CMD_GET_TEMPERATURE:

                    Log.i(TAG, "CMD_GET_TEMPERATURE");

                    for (int i=0; i< data.length; i++){
                        globalTemperatureList.add(data[i]);
                    }

                    if (data[data.length - 1] == -1){
                        android.util.Log.i(TAG, "Temperature Finish ");
                        try {
                            PedometerResponseHR1810G.getTemperatureMeasure(globalTemperatureList, mActivity.get().getPatientId() );
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    break;

                case HR1755HR1810GCommand.CMD_START_REAL_TIME: // with / without Temerature

                    // we need to activate or desactivate byte 1 for Heart Rate and byte 2 for Temperature in REAL TIME
                     //   if (data[2] == HR1755HR1810GCommand.CMD_ACTIVATE_REAL_TIME_TEMPERATURE_HR){

                       //     Log.i(TAG, "CMD_ACTIVATE_REAL_TIME_TEMPERATURE_HR");
                         //   PedometerResponseHR1810G.getRealTimeTemperature(data);
                        //} else
                          //  Log.i(TAG, "CMD_ACTIVATE_REAL_TIME_WITHOUT_TEMPERATURE_HR");
                            //PedometerResponseHR1810G.getRealTime(data);
                    //break;

            }
        }

        //==============================================================================================
        // OnLoadData Interface CallBacks, Synchronize Mode only
        //==============================================================================================
        @Override
        public void onInsufficientLoadingTemperature() {
            getActivity().getActivityData(getDevice(),getDaysAgo());
        }

        @Override
         public void onLoadingTemperatureIsFinish(List treatmentList) {
             Log.i(TAG, "onLoadingStepsIsFinish");

             mProgress = new SynchronizeBleNotifyCallBackHR1810G.ProgressTask(getActivity(), this, getDevice(), 1);
             Object[] objects = new Object[1];
             objects[0] = treatmentList;
             mProgress.execute(objects);
    }



        @Override
        public void onInsufficientLoadingSteps() {
            getActivity().getActivityData(getDevice(),getDaysAgo());
        }



        @Override
        public void onLoadingStepsIsFinish(List treatmentList) {
            Log.i(TAG, "onLoadingStepsIsFinish");

            mProgress = new SynchronizeBleNotifyCallBackHR1810G.ProgressTask(getActivity(), this, getDevice(), 1);
            Object[] objects = new Object[1];
            objects[0] = treatmentList;
            mProgress.execute(objects);
        }

        @Override
        public void onLoadingStepsDaysIsFinish(List<StepDataDay> listStepsAllDays) {
            Log.i(TAG, "onLoadingStepsDaysIsFinish");

            mProgress = new SynchronizeBleNotifyCallBackHR1810G.ProgressTask(getActivity(), this, getDevice(), 2);
            Object[] objects = new Object[1];
            objects[0] = listStepsAllDays;
            mProgress.execute(objects);
        }

        @Override
        public void onTreatmentStepsIsFinish() {
            Log.i(TAG, "onTreatmentStepsIsFinish");

            mProgress = new SynchronizeBleNotifyCallBackHR1810G.ProgressTask(getActivity(), this, getDevice(), 3);
            mProgress.execute();
        }

        @Override
        public void onInsufficientLoadingSleep() {
            getActivity().getAllSleepData(getDevice());
        }

        @Override
        public void onLoadingSleepIsFinish(List treatmentList) {
            Log.i(TAG, "onLoadingSleepIsFinish");

            mProgress = new SynchronizeBleNotifyCallBackHR1810G.ProgressTask(getActivity(), this, getDevice(), 4);
            Object[] objects = new Object[1];
            objects[0] = treatmentList;
            mProgress.execute(objects);
        }

        @Override
        public void onTreatmentSleepIsFinish() {
            Log.i(TAG, "onTreatmentSleepIsFinish");

            mProgress = new SynchronizeBleNotifyCallBackHR1810G.ProgressTask(getActivity(), this, getDevice(), 5);
            mProgress.execute();
            getActivity().endOfPedometerSynchronization();
            removeListener();
            //getActivity().changeToCommandMode();
        }

        public static class ProgressTask extends AsyncTask<Object, Integer, Boolean> {

            private WeakReference<PedometerHR1810GBleGattCallBack> mActivity = null;
            private WeakReference<SynchronizeBleNotifyCallBackHR1810G> mSecondActivity = null;
            private BleDevice device;
            private int level;

            public ProgressTask(PedometerHR1810GBleGattCallBack activity, SynchronizeBleNotifyCallBackHR1810G secondActivity, BleDevice device, int level) {
                this.mActivity       = new WeakReference<>(activity);
                this.mSecondActivity = new WeakReference<>(secondActivity);
                this.device = device;
                this.level = level;
            }

            @Override
            protected Boolean doInBackground(Object... objects) {

                switch (level){
                    case 1:
                        List<StepDetailed> treatmentListSteps = (List<StepDetailed>) objects[0];
                        return doLevelOne(treatmentListSteps);
                    case 2:
                        List<StepDataDay> listStepsAllDays    = (List<StepDataDay>) objects[0];
                        return doLevelTwo(listStepsAllDays);
                    case 3:
                        return doLevelThree();
                    case 4:
                        List<SleepDetailed> treatmentListSleep = (List<SleepDetailed>) objects[0];
                        return doLevelFourth(treatmentListSleep);
                    case 5:
                        return doLevelFive();

                    default:
                        return null;
                }
            }

            public boolean doLevelOne(List<StepDetailed> treatmentList){

                try {
                    mSecondActivity.get().resetRequestStepsParameters();
                    ToastUtils.toast("Début synchronisation Activités ...", Toast.LENGTH_SHORT, false);
                    List<StepSampleMinute> listStepsSampleMinute = new ArrayList<>();
                    ConversionData.conversionToMinuteStepSamples(treatmentList, listStepsSampleMinute);

                    ConversionData.chargingDailyActivityHR1810G(mActivity.get().getDaysAgo(), listStepsSampleMinute, mSecondActivity.get().activityList, this);
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }

            public boolean doLevelTwo(List<StepDataDay> listStepsAllDays){
                try {
                    ConversionData.comparingArrayListHR1810G(mSecondActivity.get().activityList, listStepsAllDays);
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }

            public boolean doLevelThree(){
                try{
                    mActivity.get().saveStepsMeasures(mSecondActivity.get().activityList);
                    Log.i(TAG, "Get STEPS: END");
                    ToastUtils.toast("Synchronisation Activité terminée", Toast.LENGTH_SHORT, false);
                    mActivity.get().vibrate(device , 1);
                    Bip.play();
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }




            public boolean doLevelFourth(List<SleepDetailed> treatmentList){
                try {
                    mSecondActivity.get().resetRequestSleepParameters();
                    ToastUtils.toast("Début synchronisation Sommeil ...", Toast.LENGTH_SHORT, false);

                    List<SleepSampleFiveMinute> listSleepSampleFiveMinutes = new ArrayList<>();
                    ConversionData.conversionToFiveMinuteSleepSamples(treatmentList, listSleepSampleFiveMinutes);

                    try {
                        ConversionData.chargingDailySleepHR1810F(mActivity.get().getDaysAgo(), listSleepSampleFiveMinutes, mSecondActivity.get().sleepList, this );
                    } catch (Exception e) {e.printStackTrace();}
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }


            public boolean doLevelFive(){
                try {
                    mActivity.get().saveSleepMeasures(mSecondActivity.get().sleepList);
                    Log.i(TAG, "Get SLEEP: END");
                    ToastUtils.toast("Synchronisation Sommeil terminée", Toast.LENGTH_SHORT, false);
                    mActivity.get().vibrate(device , 1);
                    Bip.play();
                    return true;
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
            }
            public void ProgressUpdate(Integer percentage, int typeMessage){
                switch (typeMessage){
                    case 1:
                        ToastUtils.toast(percentage + "% Activité terminé", Toast.LENGTH_SHORT, false);
                        break;

                    case 2:
                        ToastUtils.toast(percentage + "% Sommeil terminé", Toast.LENGTH_SHORT, false);
                        break;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean){
                    switch (level){
                        case 1:
                            mActivity.get().getAllActivity(device);
                            return;
                        case 3:
                            mActivity.get().getAllSleepData(device);
                            return;
                        case 5:
                            mActivity.get().getAllTemperatureData(device);
                            return;
                    }
                }else {
                    Log.e(mActivity.get().TAG, "Erreur dans l'asyncTask");
                }
            }
        }

        //==============================================================================================
        // Necessary function to set parameters before / after operations (Only Synchronize mode)
        //==============================================================================================

        public void resetRequestStepsParameters(){
            continueRequestSteps = false;
            HR1755HR1810GCommand.setRequestCounterStepsDetails(1) ;
            PedometerResponseHR1810G.setLastNecessarySampleStepsIsLoaded(false);
        }

        public void resetRequestSleepParameters(){
            continueRequestSleep = false;
            HR1755HR1810GCommand.setRequestCounterSleepDetails(1);
            PedometerResponseHR1810G.setLastNecessarySampleSleepIsLoaded(false);
        }

    public void resetRequestTemperatureParameters(){
        continueRequestSleep = false;
        HR1755HR1810GCommand.setRequestCounterSleepDetails(1);
        PedometerResponseHR1810G.setLastNecessarySampleSleepIsLoaded(false);
    }




        public void prepareParametersToLoadActivityAndSleep(){
            continueRequestSleep = true ;
            continueRequestSteps = true ;
            activityList.clear();
            sleepList.clear();
            prepareListener();
        }

        public void prepareListener(){
            commands  = PedometerResponseHR1810G.newInstance();
            converter = ConversionData.newInstance();
            commands.setListenerHR1810G(this);
            converter.setListenerHR1810G(this);
        }

        public void removeListener(){
            commands.setListenerHR1810G(null);
            converter.setListenerHR1810G(null);
        }
    }

