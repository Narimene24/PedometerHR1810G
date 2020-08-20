package com.narimene.temal.pedometer.pedometer.EcareFit.JP1304;

import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.util.Timer;
import java.util.TimerTask;

import fr.semantic.ecare.app.android.components.sensor.NewMeasureBroadcast;
import fr.semantic.ecare.app.android.components.sensor.SensorBleGattCallback;
import fr.semantic.ecare.app.android.components.sensor.SensorType;
import fr.semantic.ecare.app.android.components.sensor.pedometer.EcareFit.JP1304.modes.CommandBleNotifyCallback;
import fr.semantic.ecare.app.android.components.sensor.pedometer.EcareFit.JP1304.modes.SynchronizeBleNotifyCallback;
import fr.semantic.ecare.app.android.domain.data.SleepPeriodRepository;
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
import fr.semantic.ecare.app.android.domain.measure.pedometer.sleep.SleepPeriodModel;
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


/**
 * Service that handles Bluetooth communications with J-Style JP-1304 and EcareFit pedometers.
 *
 * @version 1.3.0
 * @since 1.1.0
 */
public class PedometerBleGattCallback extends SensorBleGattCallback {

    public final static String TAG = PedometerBleGattCallback.class.getSimpleName();

    private SensorType mSensorType = SensorType.PEDOMETER;

    public static final String SERVICE_UUID = "0000FFF0-0000-1000-8000-00805f9b34fb";
    public static final String RX_CHAR_UUID = "0000FFF7-0000-1000-8000-00805f9b34fb";
    private static final String TX_CHAR_UUID = "0000FFF6-0000-1000-8000-00805f9b34fb";

    public static int DEFAULT_AGE = 40;
    public static int DEFAULT_HEIGHT = 170;
    public static int DEFAULT_WEIGHT = 70;
    public static int DEFAULT_FOOTSTEP_WIDTH = 70;

    private PedometerMode mMode = PedometerMode.SYNCHRONIZE_DATA;

    private static final int TIMER_INTERVAL = 1500;
    private Timer mResendCommandTimer;
    private TimerTask mResendCommandTimerTask;
    private byte[] mLastCommand;

    private int mDaysAgo = 0;

    private String mPatientId;
    private int mFootstepGoal = 1000;
    private int mWeight = 0;
    private int mHeight = 0;
    private int mFootstepWidth = 0;
    private int mAge = 0;

    private SleepPeriodModel mSleepPeriod;

    private DailyActivity mDailyActivity;
    private DailySleep mDailySleep;

    private boolean mSyncInProgress = false;

    private BleNotifyCallback mBleNotifyCallback;

    public PedometerBleGattCallback(Sensor.SensorModel sensorModel) {
        super(sensorModel);
        initializationNecessaryData();
    }

    public PedometerBleGattCallback(Sensor.SensorModel sensorModel, int mCounterConnexion) {
        super(sensorModel, mCounterConnexion);
        initializationNecessaryData();
    }

    public void initializationNecessaryData(){
        PatientModel patient = PatientActivity.getSelectedPatient();
        mPatientId = patient.getIdServer();

        mDaysAgo = PatientService.computeNumberOfDaysOfPedometerDataToRetrieve(patient) - 1;
        mAge = new PatientAgeComputor().setPatient(patient).computeAge();
        mWeight = (int)(MeasureService.loadMostRecentMeasure(mPatientId, MeasureType.WEIGHT)
                .or(new MeasureModel().setValue(DEFAULT_WEIGHT))
                .getValue());
        mHeight = patient.getHeight().or(DEFAULT_HEIGHT);
        mFootstepWidth = patient.getFootstepSize().or(DEFAULT_FOOTSTEP_WIDTH);

        float defaultFootstepGoal = VrefService.getDefaultVref(VrefType.ACTIVITY_BY_DAY.getIdServer())
                .or(Float.valueOf(PedometerModel.DEFAULT_FOOTSTEP_GOAL));

        mFootstepGoal = (int)(VrefRepository.loadMostRecent(mPatientId, VrefType.ACTIVITY_BY_DAY.getIdServer())
                .or(new VrefModel().setValue(defaultFootstepGoal)))
                .getValue();

        mSleepPeriod = SleepPeriodRepository.loadByPatientId(mPatientId);

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

    public void setDaysAgo(int mDaysAgo) {
        this.mDaysAgo = mDaysAgo;
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

    public DailyActivity getDailyActivity() {
        return mDailyActivity;
    }

    public DailySleep getDailySleep() {
        return mDailySleep;
    }

    public void setSyncInProgress(boolean mSyncInProgress) {
        this.mSyncInProgress = mSyncInProgress;
    }

    //==============================================================================================
    // BroadCast Receiver
    //==============================================================================================

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(PedometerBroadcast.ACTION_SET_SLEEP_PERIOD)) {
                Log.d(TAG, "Send sleep parameters to pedometer: mSyncInProgress="+ String.valueOf(mSyncInProgress));
                mSleepPeriod = SleepPeriodRepository.loadByPatientId(mPatientId);
                if(!mSyncInProgress && mDevice != null)
                    setSleepModeByPress(mDevice);
            }
        }
    };

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
                            PedometerResponseJp1304.setIsFirstRequestRealTime(true);
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

    public void registerReceiver() {
        Log.d(TAG, "registerReceiver()");
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(App.getContext());
        manager.registerReceiver(mReceiver, PedometerBroadcast.makeIntentFilter());
        App.getContext().registerReceiver(mReceiverCommandsRealTime, new IntentFilter(Tools.REAL_TIME_PARAM));
        App.getContext().registerReceiver(mReceiverAvailablePedometer, new IntentFilter(Tools.PEDOMETER_AVAILABLE));
    }

    public void unregisterReceiver() {
        Log.d(TAG, "unregisterReceiver()");
        try {
            mDevice = null;
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(App.getContext());
            manager.unregisterReceiver(mReceiver);
            App.getContext().unregisterReceiver(mReceiverCommandsRealTime);
            App.getContext().unregisterReceiver(mReceiverAvailablePedometer);
        }
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
    protected void processWriteFailure(BleException exception) {
        //        scheduleResendCommand(device);
    }

    private void scheduleResendCommand(final BleDevice device)  {

//        cancelResendCommand();

        mResendCommandTimer = new Timer();
        mResendCommandTimerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "Resend last command");
                writeRXCharacteristic(device,mLastCommand);
                mResendCommandTimerTask = null;
            }
        };

        mResendCommandTimer.schedule(mResendCommandTimerTask, TIMER_INTERVAL);
    }

    private void cancelResendCommand() {
        if(mResendCommandTimer != null) {
            mResendCommandTimer.cancel();
            mResendCommandTimer = null;
        }
    }

    public void saveNewMeasures(DailyActivity dailyActivity, DailySleep dailySleep) {
        Log.i(TAG, "New measures received");

        dailyActivity.setPatientId(PatientActivity.getSelectedPatient().getIdServer());
        dailyActivity.setPhysicianId("");
        DailyActivityService.replace(dailyActivity);
        NewMeasureBroadcast.broadcastDisplayActivity(dailyActivity);

        dailySleep.setPatientId(PatientActivity.getSelectedPatient().getIdServer());
        dailySleep.setPhysicianId("");
        DailySleepService.replace(dailySleep);
    }

    public void startSync(final BleDevice device) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "startSync()");
                mSyncInProgress = true;
                getData(device,mDaysAgo);
            }
        }, 1000);

    }

    public void startRealTime(BleDevice device) {
        Log.d(TAG, "startRealTimeBroadcast()");
        mLastCommand = PedometerCommand.startRealTime();
        writeRXCharacteristic(device,mLastCommand);
    }

    public void stopRealTime(BleDevice device) {
        Log.d(TAG, "stopRealTime()");
        mLastCommand = PedometerCommand.stopRealTime();
        writeRXCharacteristic(device,mLastCommand);
    }

    public void getData(BleDevice device, int day) {
        Log.d(TAG, "getData(): day=" + day);
        mDailyActivity = new DailyActivity();
        mDailySleep = new DailySleep();
        mLastCommand = PedometerCommand.getData(day);
        writeRXCharacteristic(device,mLastCommand);
    }

    public void setSleepPeriod(BleDevice device, SleepPeriodModel sleepPeriod) {
        Log.d(TAG, "setSleepPeriod()");

        // First, set the sleep mode by press, then send the sleep period
        mSleepPeriod = sleepPeriod;
        setSleepModeByPress(device);
    }

    public void setSleepModeByPress(BleDevice device) {
        Log.d(TAG, "setSleepModeByPress(): enable=" + mSleepPeriod.isSetSleepModeByLongPressEnabled());
        boolean enable = mSleepPeriod.isSetSleepModeByLongPressEnabled();
        mLastCommand = PedometerCommand.setSleepModeByPress(enable);
        writeRXCharacteristic(device,mLastCommand);
    }

    public void setSleepRange(BleDevice device) {
        Log.d(TAG, "setSleepRange(): mSleepPeriod=" + mSleepPeriod);
        mLastCommand = PedometerCommand.setSleepPeriod(
                mSleepPeriod.getStartHour(), mSleepPeriod.getStartMinute(),
                mSleepPeriod.getEndHour(),   mSleepPeriod.getEndMinute()
        );
        writeRXCharacteristic(device,mLastCommand);
    }

    public void setTimeFormat(BleDevice device) {
        Log.d(TAG, "setTimeFormat()");
        mLastCommand = PedometerCommand.setTimeFormat();
        writeRXCharacteristic(device,mLastCommand);
    }

    public void setTime(BleDevice device) {
        Log.d(TAG, "setTime()");
        mLastCommand = PedometerCommand.setTime();
        writeRXCharacteristic(device,mLastCommand);
    }

    public void getBatteryLevel(BleDevice device) {
        Log.d(TAG, "getBatteryLevel()");
        mLastCommand = PedometerCommand.getBatteryLevel();
        writeRXCharacteristic(device,mLastCommand);
    }

    public void setFootsteepGoal(BleDevice device) {
        Log.d(TAG, "setFootsteepGoal() goal=" + mFootstepGoal);
        mLastCommand = PedometerCommand.setFootstepGoal(mFootstepGoal);
        writeRXCharacteristic(device,mLastCommand);
    }

    public void setPersonalData(BleDevice device) {
        Log.d(TAG, "setPersonalData()"
                + " mAge=" + mAge
                + " height=" + mHeight
                + " weight=" + mWeight
                + " footstepWidth=" + mFootstepWidth
        );
        mLastCommand = PedometerCommand.setPersonalData(mAge, mHeight, mWeight, mFootstepWidth);
        writeRXCharacteristic(device,mLastCommand);
    }

    public void setUnitDistance(BleDevice device) {
        Log.d(TAG, "setUnitDistance() unit=KM");
        mLastCommand = PedometerCommand.setUnitDistance(PedometerCommand.UNIT_KM);
        writeRXCharacteristic(device,mLastCommand);
    }

    public void vibrate(BleDevice device) {
        Log.d(TAG, "vibrate()");
        mLastCommand = PedometerCommand.vibrate();
        writeRXCharacteristic(device,mLastCommand);
    }

    public void endOfPedometerSynchronization() {
        Log.d(TAG, "endOfPedometerSynchronization()");

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        boolean sendChangesInstantly = preferences.getBoolean(Preferences.SEND_CHANGES_INSTANTLY_ENABLED, true);
        boolean hasNetwork = NetworkState.isConnected(App.getContext());
        if(sendChangesInstantly && hasNetwork)
            new SendPedometerMeasuresTask().execute();

        ActivityBroadcast.broadcastRefresh();
    }

    //==============================================================================================
    // Changing Pedometer Mode functions
    //==============================================================================================

    public void preparePedometerModes(){
        //Map<String, PedometerMode> patientsIdPedometerMode = PatientStatePedometer.getPatientsPedometerModeMap();

        //if(patientsIdPedometerMode.containsKey(mPatientId))
          //  mMode = patientsIdPedometerMode.get(mPatientId);

        //if (mMode.equals(PedometerMode.SYNCHRONIZE_DATA))
            changeToSynchronizeMode();
        //else if (mMode.equals(PedometerMode.COMMAND))
            //changeToCommandMode();
    }

    public void changeToCommandMode(){
        removeNotifyCallBack();
        mBleNotifyCallback = new CommandBleNotifyCallback(this);
        mMode = PedometerMode.COMMAND;
        //PatientStatePedometer.addPatientPedometerMode(mPatientId, mMode);
        setModePedometer();
    }

    public void changeToSynchronizeMode(){
        removeNotifyCallBack();
        mBleNotifyCallback = new SynchronizeBleNotifyCallback(this);
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
        if (mBleNotifyCallback instanceof CommandBleNotifyCallback)
            return true;
        else
            return false;
    }
}