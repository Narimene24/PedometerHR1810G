package com.narimene.temal.pedometer.pedometer.HR1755.modes;

import android.os.AsyncTask;
import android.widget.Toast;

import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import fr.semantic.ecare.app.android.components.sensor.pedometer.ConversionData;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.PedometerHR1755BleGattCallback;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.commands.OnLoadDataPedometerHR1755;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.commands.PedometerResponseHR1755;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.SleepDetailed;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.SleepSampleFiveMinute;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDataDay;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDetailed;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepSampleMinute;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755HR1810GCommand;
import fr.semantic.ecare.app.android.domain.measure.pedometer.activity.DailyActivity;
import fr.semantic.ecare.app.android.domain.measure.pedometer.sleep.DailySleep;
import fr.semantic.ecare.app.android.utils.Log;
import fr.semantic.ecare.app.android.utils.ui.Bip;
import fr.semantic.ecare.app.android.utils.ui.ToastUtils;

/**
 * class Notify Pedometer HR1755 for Synchronize Mode only
 *
 * @version 1.3.0
 * @since 1.3.0
 */
public class SynchronizeBleNotifyCallbackHR1755 extends BleNotifyCallback implements OnLoadDataPedometerHR1755 {
    public final static String TAG = SynchronizeBleNotifyCallbackHR1755.class.getSimpleName();

    private WeakReference<PedometerHR1755BleGattCallback> mActivity = null;
    private ProgressTask mProgress = null;

    private PedometerResponseHR1755 commands = null;
    private ConversionData converter = null;

    private boolean continueRequestSteps = true;
    private boolean continueRequestSleep = true;

    private List<DailyActivity> activityList ;
    private List<DailySleep> sleepList;

    public SynchronizeBleNotifyCallbackHR1755(PedometerHR1755BleGattCallback activity) {
        this.mActivity = new WeakReference<PedometerHR1755BleGattCallback>(activity);
        activityList = new ArrayList<>();
        sleepList    = new ArrayList<>();
    }

    public PedometerHR1755BleGattCallback getActivity(){
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
                    PedometerResponseHR1755.getDetailedStepsData(data, getDaysAgo());
                }
                break;

            case HR1755HR1810GCommand.CMD_GET_ALL_STEPS_DATA:
                PedometerResponseHR1755.getAllStepsData(data);
                break;

            case HR1755HR1810GCommand.CMD_GET_DETAILED_SLEEP_DATA:
                if (continueRequestSleep) {
                    Log.i(TAG, "HR1755HR1810GCommand.CMD_GET_DETAILED_SLEEP_DATA");
                    PedometerResponseHR1755.getDetailedSleepData(data, getDaysAgo());
                }
                break;

        }
    }

    //==============================================================================================
    // OnLoadData Interface CallBacks, Synchronize Mode only
    //==============================================================================================

    @Override
    public void onInsufficientLoadingSteps() {
        getActivity().getActivityData(getDevice(),getDaysAgo());
    }

    @Override
    public void onLoadingStepsIsFinish(List treatmentList) {
        Log.i(TAG, "onLoadingStepsIsFinish");

        mProgress = new ProgressTask(getActivity(), this, getDevice(), 1);
        Object[] objects = new Object[1];
        objects[0] = treatmentList;
        mProgress.execute(objects);
    }

    @Override
    public void onLoadingStepsDaysIsFinish(List<StepDataDay> listStepsAllDays) {
        Log.i(TAG, "onLoadingStepsDaysIsFinish");

        mProgress = new ProgressTask(getActivity(), this, getDevice(), 2);
        Object[] objects = new Object[1];
        objects[0] = listStepsAllDays;
        mProgress.execute(objects);
    }

    @Override
    public void onTreatmentStepsIsFinish() {
        Log.i(TAG, "onTreatmentStepsIsFinish");

        mProgress = new ProgressTask(getActivity(), this, getDevice(), 3);
        mProgress.execute();
    }

    @Override
    public void onInsufficientLoadingSleep() {
        getActivity().getAllSleepData(getDevice());
    }

    @Override
    public void onLoadingSleepIsFinish(List treatmentList) {
        Log.i(TAG, "onLoadingSleepIsFinish");

        mProgress = new ProgressTask(getActivity(), this, getDevice(), 4);
        Object[] objects = new Object[1];
        objects[0] = treatmentList;
        mProgress.execute(objects);
    }

    @Override
    public void onTreatmentSleepIsFinish() {
        Log.i(TAG, "onTreatmentSleepIsFinish");

        mProgress = new ProgressTask(getActivity(), this, getDevice(), 5);
        mProgress.execute();
        getActivity().endOfPedometerSynchronization();
        removeListener();
        getActivity().changeToCommandMode();
    }

    public static class ProgressTask extends AsyncTask<Object, Integer, Boolean> {

        private WeakReference<PedometerHR1755BleGattCallback> mActivity = null;
        private WeakReference<SynchronizeBleNotifyCallbackHR1755> mSecondActivity = null;
        private BleDevice device;
        private int level;

        public ProgressTask(PedometerHR1755BleGattCallback activity, SynchronizeBleNotifyCallbackHR1755 secondActivity, BleDevice device, int level) {
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

                ConversionData.chargingDailyActivityHR1755(mActivity.get().getDaysAgo(), listStepsSampleMinute, mSecondActivity.get().activityList, this);
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        public boolean doLevelTwo(List<StepDataDay> listStepsAllDays){
            try {
                ConversionData.comparingArrayListHR1755(mSecondActivity.get().activityList, listStepsAllDays);
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
                    ConversionData.chargingDailySleepHR1755(mActivity.get().getDaysAgo(), listSleepSampleFiveMinutes, mSecondActivity.get().sleepList, this );
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
        PedometerResponseHR1755.setLastNecessarySampleStepsIsLoaded(false);
    }

    public void resetRequestSleepParameters(){
        continueRequestSleep = false;
        HR1755HR1810GCommand.setRequestCounterSleepDetails(1);
        PedometerResponseHR1755.setLastNecessarySampleSleepIsLoaded(false);
    }

    public void prepareParametersToLoadActivityAndSleep(){
        continueRequestSleep = true ;
        continueRequestSteps = true ;
        activityList.clear();
        sleepList.clear();
        prepareListener();
    }

    public void prepareListener(){
        commands  = PedometerResponseHR1755.newInstance();
        converter = ConversionData.newInstance();
        commands.setListener(this);
        converter.setListener(this);
    }

    public void removeListener(){
        commands.setListener(null);
        converter.setListener(null);
    }
}