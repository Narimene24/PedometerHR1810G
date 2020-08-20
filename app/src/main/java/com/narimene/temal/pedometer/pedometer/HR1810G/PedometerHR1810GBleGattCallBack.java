package com.narimene.temal.pedometer.pedometer.HR1810G;

import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.List;

import fr.semantic.ecare.app.android.components.sensor.NewMeasureBroadcast;
import fr.semantic.ecare.app.android.components.sensor.SensorBleGattCallback;
import fr.semantic.ecare.app.android.components.sensor.SensorType;
import fr.semantic.ecare.app.android.components.sensor.pedometer.EcareFit.JP1304.PedometerMode;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.commands.PedometerResponseHR1755;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.BasicParametersDevice;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.UserPersonalData;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755HR1810GCommand;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.commands.PedometerResponseHR1810G;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.modes.CommandBleNotifyCallBackHR1810G;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.modes.SynchronizeBleNotifyCallBackHR1810G;
import fr.semantic.ecare.app.android.domain.measure.MeasureModel;
import fr.semantic.ecare.app.android.domain.measure.MeasureService;
import fr.semantic.ecare.app.android.domain.measure.MeasureType;
import fr.semantic.ecare.app.android.domain.measure.Sensor;
import fr.semantic.ecare.app.android.domain.measure.VrefModel;
import fr.semantic.ecare.app.android.domain.measure.VrefRepository;
import fr.semantic.ecare.app.android.domain.measure.VrefService;
import fr.semantic.ecare.app.android.domain.measure.VrefType;
import fr.semantic.ecare.app.android.domain.measure.pedometer.activity.DailyActivity;
import fr.semantic.ecare.app.android.domain.measure.pedometer.activity.DailyActivityService;
import fr.semantic.ecare.app.android.domain.measure.pedometer.sleep.DailySleep;
import fr.semantic.ecare.app.android.domain.measure.pedometer.sleep.DailySleepService;
import fr.semantic.ecare.app.android.domain.patient.PatientAgeComputor;
import fr.semantic.ecare.app.android.domain.patient.PatientModel;
import fr.semantic.ecare.app.android.domain.patient.PatientService;
import fr.semantic.ecare.app.android.domain.patient.PedometerModel;
import fr.semantic.ecare.app.android.modules.configuration.Preferences;
import fr.semantic.ecare.app.android.modules.patient.activity.ActivityBroadcast;
import fr.semantic.ecare.app.android.modules.patient.walk.minutewalk.Tools;
import fr.semantic.ecare.app.android.transmission.tasks.SendPedometerMeasuresTask;
import fr.semantic.ecare.app.android.ui.App;
import fr.semantic.ecare.app.android.ui.PatientActivity;
import fr.semantic.ecare.app.android.utils.Log;
import fr.semantic.ecare.app.android.utils.NetworkState;
import fr.semantic.ecare.app.android.utils.validation.ValidatorException;

public class PedometerHR1810GBleGattCallBack extends SensorBleGattCallback{



/**
 * Service that handles Bluetooth communications with J-Style HR-1810G pedometers.
 */

    public final static String TAG = fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.PedometerHR1810GBleGattCallBack.class.getSimpleName();

    private SensorType mSensorType = SensorType.PEDOMETER;

    public static final String SERVICE_UUID = "0000FFF0-0000-1000-8000-00805f9b34fb";
    public static final String RX_CHAR_UUID = "0000FFF7-0000-1000-8000-00805f9b34fb";
    private static final String TX_CHAR_UUID = "0000FFF6-0000-1000-8000-00805f9b34fb";

    public static int DEFAULT_AGE = 40;
    public static int DEFAULT_HEIGHT = 170;
    public static int DEFAULT_WEIGHT = 70;
    public static int DEFAULT_FOOTSTEP_WIDTH = 70;

    private static PedometerMode mMode = PedometerMode.SYNCHRONIZE_DATA;
    private byte[] mLastCommand;

    private int mDaysAgo = 0;

    private String mPatientId;
    private int mFootstepGoal = 1000;
    private int mWeight = 0;
    private int mHeight = 0;
    private int mFootstepWidth = 0;
    private int mAge = 0;

    private BleNotifyCallback mBleNotifyCallback;


    public PedometerHR1810GBleGattCallBack(Sensor.SensorModel sensorModel) {
        super(sensorModel);
        initializationNecessaryData();
    }

    public PedometerHR1810GBleGattCallBack(Sensor.SensorModel sensorModel, int mCounterConnexion) {
        super(sensorModel, mCounterConnexion);
        initializationNecessaryData();
    }

    public void initializationNecessaryData(){
        PatientModel patient = PatientActivity.getSelectedPatient();
        mPatientId = patient.getIdServer();

        mDaysAgo = PatientService.computeNumberOfDaysOfPedometerDataToRetrieve(patient) - 1;
        mAge = new PatientAgeComputor().setPatient(patient).computeAge();
        mWeight = (int) (MeasureService.loadMostRecentMeasure(mPatientId, MeasureType.WEIGHT)
                .or(new MeasureModel().setValue(DEFAULT_WEIGHT))
                .getValue());
        mHeight = patient.getHeight().or(DEFAULT_HEIGHT);
        mFootstepWidth = patient.getFootstepSize().or(DEFAULT_FOOTSTEP_WIDTH);

        float defaultFootstepGoal = VrefService.getDefaultVref(VrefType.ACTIVITY_BY_DAY.getIdServer())
                .or(Float.valueOf(PedometerModel.DEFAULT_FOOTSTEP_GOAL));

        mFootstepGoal = (int) (VrefRepository.loadMostRecent(mPatientId, VrefType.ACTIVITY_BY_DAY.getIdServer())
                .or(new VrefModel().setValue(defaultFootstepGoal)))
                .getValue();

        checkPersonalData();
    }

    @Override
    protected String getTAG() {
        return TAG;
    }

    @Override
    protected SensorType getSensorType() {
        return mSensorType;
    }

    @Override
    protected String getServiceUUID() {
        return SERVICE_UUID;
    }

    @Override
    protected String getRxCharUUID() {
        return RX_CHAR_UUID;
    }

    @Override
    protected String getTxCharUUID() {
        return TX_CHAR_UUID;
    }

    //==============================================================================================
    // Custom event methods
    //==============================================================================================

    @Override
    public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
        registerReceiver();
        super.onConnectSuccess(bleDevice, gatt, status);
    }

    @Override
    public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
        super.onDisConnected(isActiveDisConnected, device, gatt, status);
        changeToSynchronizeMode();
        unregisterReceiver();
    }

    //==============================================================================================
    // Global Functions
    //==============================================================================================

    private void checkPersonalData() {

        if(mAge > 120 || mAge < 15)
            mAge = DEFAULT_AGE;

        if(mHeight < 50 || mHeight > 250)
            mHeight = DEFAULT_HEIGHT;

        if(mWeight < 40 || mWeight > 200)
            mWeight = DEFAULT_WEIGHT;

        if(mFootstepWidth < 40 || mFootstepWidth > 130)
            mFootstepWidth = DEFAULT_FOOTSTEP_WIDTH;
    }

    public int getDaysAgo() {
        return mDaysAgo;
    }

    public BleDevice getDevice(){
        return mDevice;
    }

    public PedometerMode getMode(){
        return mMode;
    }

    public void setMode(PedometerMode mode){
        mMode = mode;
    }

    public String getPatientId(){
        return mPatientId;
    }

    //==============================================================================================
    // Receiver REAL time Data
    //==============================================================================================
    public void registerReceiver() {
        App.getContext().registerReceiver(mReceiverCommandsRealTime, new IntentFilter(Tools.REAL_TIME_PARAM));
        App.getContext().registerReceiver(mReceiverAvailablePedometer, new IntentFilter(Tools.PEDOMETER_AVAILABLE));
    }

    /**
     * Start or Stop Real time commands
     */
    private final BroadcastReceiver mReceiverCommandsRealTime = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Tools.REAL_TIME_PARAM)) {
                boolean startRealTime = intent.getBooleanExtra(Tools.REAL_TIME_STATE_PARAM , false);

                if (startRealTime)
                    startRealTime(mDevice);
                else
                    stopRealTime(mDevice);
            }
        }
    };

    /**
     * Checking if the pedometer is available, if yes then start Real Time
     */
    private final BroadcastReceiver mReceiverAvailablePedometer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Tools.PEDOMETER_AVAILABLE)) {
                if (mDevice != null && isCommandMode()) {
                    String operationRequest = intent.getStringExtra(Tools.FROM_OPERATION);

                    switch (operationRequest) {
                        case Tools.GO_REAL_TIME:
                            PedometerResponseHR1755.setIsFirstRequestRealTime(true);
                            startRealTime(mDevice);
                            break;

                        case Tools.CHANGE_STEP_WIDTH:
                            mFootstepWidth = intent.getIntExtra(Tools.STEP_WIDTH_PARAM, mFootstepWidth);
                            checkPersonalData();
                            setPersonalData(mDevice);
                            break;
                    }
                }
            }
        }
    };

    public void unregisterReceiver() {
        try {
            mDevice = null;
            App.getContext().unregisterReceiver(mReceiverCommandsRealTime);
            App.getContext().unregisterReceiver(mReceiverAvailablePedometer);}
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    //==============================================================================================
    // Methods
    //==============================================================================================

    @Override
    protected void processCharacteristic(final BleDevice device, BluetoothGatt gatt, int status) {
        preparePedometerModes();
    }

    @Override
    protected void processWriteFailure(BleException exception) {}

    //==============================================================================================
    // Necessary Operations for Synchronize mode
    //==============================================================================================

    public void endOfPedometerSynchronization() {
        Log.d(TAG, "endOfPedometerSynchronization()");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        boolean sendChangesInstantly = preferences.getBoolean(Preferences.SEND_CHANGES_INSTANTLY_ENABLED, true);
        boolean hasNetwork = NetworkState.isConnected(App.getContext());
        if(sendChangesInstantly && hasNetwork)
            new SendPedometerMeasuresTask().execute();

        ActivityBroadcast.broadcastRefresh();
    }

    public void saveStepsMeasures(List<DailyActivity> dailyActivities ) {
        for (DailyActivity dailyActivity : dailyActivities) {
            Log.i(TAG, "Save steps list");

            dailyActivity.setPatientId(PatientActivity.getSelectedPatient().getIdServer());
            dailyActivity.setPhysicianId("");
            DailyActivityService.replace(dailyActivity);
            NewMeasureBroadcast.broadcastDisplayActivity(dailyActivity);
        }
    }



    public void saveSleepMeasures(List<DailySleep> dailySleepsList ) {
        for (DailySleep dailySleep : dailySleepsList) {
            Log.i(TAG, "Save sleep list");

            dailySleep.setPatientId(PatientActivity.getSelectedPatient().getIdServer());
            dailySleep.setPhysicianId("");
            dailySleep.setIdLocal(0);
            DailySleepService.replace(dailySleep);
        }
    }

    public void saveTemperatureMeasures(List<MeasureModel> temperatureMeasureList ) throws ValidatorException {
        for (MeasureModel mTemperature : temperatureMeasureList) {
            Log.i(TAG, "Save temperature list");

            mTemperature.setPatientId(PatientActivity.getSelectedPatient().getIdServer());
            mTemperature.setPhysicianId("");
            if (PedometerResponseHR1810G.checkIfNewTemperatureMeasuresList(temperatureMeasureList)){
                MeasureService.add(mTemperature);

            }

        }
    }

    public void getDetailsActivity(BleDevice device) {
        Log.d(TAG, "startSync()");
        getActivityData(device,mDaysAgo);
    }

    public void getActivityData(BleDevice device, int day) {
        Log.d(TAG, "getData(): day=" + day);
        mLastCommand = HR1755HR1810GCommand.getDetailedStepsData();
        writeRXCharacteristic(device,mLastCommand);
    }

    public void getAllActivity(BleDevice device) {
        mLastCommand = HR1755HR1810GCommand.getAllStepsData();
        writeRXCharacteristic(device,mLastCommand);
    }

    public void getAllSleepData(BleDevice device){
        Log.i(TAG, "GetAllSleepData");
        mLastCommand = HR1755HR1810GCommand.getDetailedSleepData();
        writeRXCharacteristic(device,mLastCommand);
    }


    public void getAllTemperatureData(BleDevice device){
        Log.i(TAG, "getAllTemperatureData");
        mLastCommand = HR1755HR1810GCommand.getAllTemperatures();
        writeRXCharacteristic(device,mLastCommand);
    }

    public void setTime(BleDevice device) {
        Log.d(TAG, "setTime()");
        mLastCommand = HR1755HR1810GCommand.setTime();
        writeRXCharacteristic(device,mLastCommand);
    }

    public void setBasicParameters(BleDevice device){
        Log.d(TAG, "setBasicParameters()");
        BasicParametersDevice basicParameters = BasicParametersDevice.getInstance(
                HR1755HR1810GCommand.UNIT_KM, HR1755HR1810GCommand.FORMAT_24H, HR1755HR1810GCommand.WRITE_SENSE_ENABLE ,
                HR1755HR1810GCommand.LEFT_HAND, HR1755HR1810GCommand.HORIZONTAL_DISPLAY, HR1755HR1810GCommand.ANCS_ENABLE );
        mLastCommand = HR1755HR1810GCommand.setBasicParameters(basicParameters);
        writeRXCharacteristic(device,mLastCommand);
    }

    public void setFootsteepGoal(BleDevice device) {
        Log.d(TAG, "setFootsteepGoal() goal=" + mFootstepGoal);
        mLastCommand = HR1755HR1810GCommand.setFootstepGoal(mFootstepGoal);
        writeRXCharacteristic(device,mLastCommand);
    }

    //==============================================================================================
    // Necessary Operations for Command mode
    //==============================================================================================

    private void startRealTime(BleDevice device) {
        Log.d(TAG, "startRealTimeBroadcast()");
        mLastCommand = HR1755HR1810GCommand.startRealTime();
        writeRXCharacteristic(device,mLastCommand);
    }

    private void stopRealTime(BleDevice device) {
        Log.d(TAG, "stopRealTimeAndResetParameters()");
        mLastCommand = HR1755HR1810GCommand.stopRealTime();
        writeRXCharacteristic(device,mLastCommand);
    }



    //==============================================================================================
    // Shared operations between multiples modes
    //==============================================================================================

    public void setPersonalData(BleDevice device) {
        Log.d(TAG, "setPersonalData()"
                + " mAge=" + mAge
                + " height=" + mHeight
                + " weight=" + mWeight
                + " footstepWidth=" + mFootstepWidth
        );
        UserPersonalData personalData = UserPersonalData.getInstance(HR1755HR1810GCommand.GENDER_MALE, mAge, mHeight, mWeight, mFootstepWidth);
        mLastCommand = HR1755HR1810GCommand.setPersonalData(personalData);
        writeRXCharacteristic(device,mLastCommand);
    }

    public void vibrate(BleDevice device, int times) {
        Log.d(TAG, "vibrate()");
        mLastCommand = HR1755HR1810GCommand.vibrate(times);
        writeRXCharacteristic(device,mLastCommand);
    }

    //==============================================================================================
    // Changing Pedometer Mode functions
    //==============================================================================================

    public void preparePedometerModes(){
        //Map<String, PedometerMode> patientsIdPedometerMode = PatientStatePedometer.getPatientsPedometerModeMap();

        //if(patientsIdPedometerMode.containsKey(mPatientId))
        //mMode = patientsIdPedometerMode.get(mPatientId);

        //if (mMode.equals(PedometerMode.SYNCHRONIZE_DATA))
        changeToSynchronizeMode();
        //else if (mMode.equals(PedometerMode.COMMAND))
        //changeToCommandMode();
    }

    public void changeToCommandMode(){
        removeNotifyCallBack();
        mBleNotifyCallback = new CommandBleNotifyCallBackHR1810G(this);
        mMode = PedometerMode.COMMAND;
        //PatientStatePedometer.addPatientPedometerMode(mPatientId, mMode);
        setModePedometer();
    }

    public void changeToSynchronizeMode(){
        removeNotifyCallBack();
        mBleNotifyCallback = new SynchronizeBleNotifyCallBackHR1810G(this);
        mMode = PedometerMode.SYNCHRONIZE_DATA;
        //PatientStatePedometer.addPatientPedometerMode(mPatientId, mMode);
        setModePedometer();
    }

    public void removeNotifyCallBack(){
        if (mBleNotifyCallback != null)
            BleManager.getInstance().removeNotifyCallback(mDevice, RX_CHAR_UUID);
    }

    public void setModePedometer(){
        BleManager.getInstance().notify(mDevice, SERVICE_UUID, RX_CHAR_UUID, mBleNotifyCallback );
    }

    public boolean isCommandMode(){
        if (mBleNotifyCallback instanceof CommandBleNotifyCallBackHR1810G)
            return true;
        else
            return false;
    }

    //==============================================================================================
    // Necessary Operations for Temperature mode
    //==============================================================================================

    public void startRealTimeWithTemperatureData(BleDevice device) {
        Log.d(TAG, "startRealTimeWithTemperatureData()");
        mLastCommand = HR1755HR1810GCommand.startRealTimeWithTemperature();
        writeRXCharacteristic(device,mLastCommand);
    }



    public void stopRealTimeWithTemperatureData(BleDevice device) {
        Log.d(TAG, "stopRealTimeWithTemperatureData()");
        mLastCommand = HR1755HR1810GCommand.stopRealTimeWithTemperature();
        writeRXCharacteristic(device,mLastCommand);
    }




}