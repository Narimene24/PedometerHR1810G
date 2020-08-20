package com.narimene.temal.pedometer.pedometer.EcareFit.JP1304.modes;

import android.os.Handler;
import android.widget.Toast;

import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.lang.ref.WeakReference;
import java.util.Date;

import fr.semantic.ecare.app.android.components.sensor.pedometer.EcareFit.JP1304.PedometerBleGattCallback;
import fr.semantic.ecare.app.android.components.sensor.pedometer.EcareFit.JP1304.PedometerCommand;
import fr.semantic.ecare.app.android.components.sensor.pedometer.EcareFit.JP1304.PedometerResponseJp1304;
import fr.semantic.ecare.app.android.domain.measure.pedometer.activity.DailyActivity;
import fr.semantic.ecare.app.android.domain.measure.pedometer.sleep.DailySleep;
import fr.semantic.ecare.app.android.utils.Log;
import fr.semantic.ecare.app.android.utils.time.DateFormat;
import fr.semantic.ecare.app.android.utils.time.DateUtils;
import fr.semantic.ecare.app.android.utils.ui.Bip;
import fr.semantic.ecare.app.android.utils.ui.ToastUtils;

/**
 * class Notify Pedometer EcareFit JP1304 for Synchronize Mode only
 *
 * @version 1.3.0
 * @since 1.3.0
 */
public class SynchronizeBleNotifyCallback extends BleNotifyCallback {
    public final static String TAG = SynchronizeBleNotifyCallback.class.getSimpleName();

    private WeakReference<PedometerBleGattCallback> mActivity = null;

    public SynchronizeBleNotifyCallback(PedometerBleGattCallback activity) {
        mActivity = new WeakReference<>(activity);
    }

    public PedometerBleGattCallback getActivity(){
        return mActivity.get();
    }

    public BleDevice getDevice(){
        return getActivity().getDevice();
    }

    public int getDaysAgo(){
        return getActivity().getDaysAgo();
    }

    public DailyActivity getDailyActivity(){
        return getActivity().getDailyActivity();
    }

    public DailySleep getDailySleep(){
        return getActivity().getDailySleep();
    }

    @Override
    public void onNotifySuccess() {
        Log.i(TAG, "onNotifySuccess Synchronize mode");
        getActivity().setTime(getDevice());
    }

    @Override
    public void onNotifyFailure(BleException exception) {
        Log.e(TAG, "onNotifyFailure Synchronize mode: " + exception.getDescription());
    }

    @Override
    public void onCharacteristicChanged(byte[] data) {
        Log.d(TAG, "onCharacteristicChanged Synchronize mode");

        switch (data[0]) {
            case PedometerCommand.CMD_GET_DATA:
                boolean isDataComplete = PedometerResponseJp1304.readData(data, getDaysAgo(), getDailyActivity(), getDailySleep());
                // Log.v(TAG, "Get data: day=" + mDaysAgo + " isDataComplete=" + isDataComplete);

                if (isDataComplete) {
                    String date = DateFormat.format(DateUtils.getDaysAgo(getDaysAgo()), DateFormat.dd_MM_yyyy);
                    Log.d(TAG, "Get data: mDaysAgo=" + getDaysAgo() + " date=" + date);
                    ToastUtils.toast("Jour " + date + " synchronisé, patientez svp !", Toast.LENGTH_SHORT, false);

                    getActivity().saveNewMeasures(getDailyActivity(), getDailySleep());

                    if (getDaysAgo() > 0) {
                        getActivity().setDaysAgo(getDaysAgo() - 1);
                        getActivity().getData(getDevice(), getDaysAgo());
                    } else {
                        Log.i(TAG, "Get data: END");
                        ToastUtils.toast("Synchronisation terminée", Toast.LENGTH_SHORT, false);
                        getActivity().vibrate(getDevice());
                        Bip.play();
                        getActivity().endOfPedometerSynchronization();

                        /**
                         * the pedometer does not respond, if it is vibrating
                         */
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().changeToCommandMode();
                            }
                        }, 6000);
                    }
                }
                break;
            case PedometerCommand.CMD_SET_TIME:
                Log.d(TAG, "Time set");
                getActivity().setTimeFormat(getDevice());
                break;
            case PedometerCommand.CMD_SET_TIME_FORMAT:
                Log.d(TAG, "Time format set");
                ToastUtils.toast("Horloge mise à jour", Toast.LENGTH_SHORT, false);
                getActivity().setUnitDistance(getDevice());
                break;
            case PedometerCommand.CMD_SET_UNIT_DISTANCE:
                Log.d(TAG, "Unit distance set");
                getActivity().setFootsteepGoal(getDevice());
                break;
            case PedometerCommand.CMD_SET_FOOTSTEP_GOAL:
                Log.d(TAG, "Footsteep goal set");
                ToastUtils.toast("Objectif envoyé", Toast.LENGTH_SHORT, false);
                getActivity().setPersonalData(getDevice());
                break;
            case PedometerCommand.CMD_SET_PERSONAL_DATA:
                Log.d(TAG, "Personal data set");
                getActivity().startSync(getDevice());
                break;

            case PedometerCommand.CMD_VIBRATE:
                Log.d(TAG, "Vibrating");
                // TODO: works only with E-care Fit pedometer and not with J-Style pedometer
                getActivity().setSleepModeByPress(getDevice());
                break;

            case PedometerCommand.CMD_SET_SLEEP_MODE_BY_PRESS:
                Log.d(TAG, "Sleep mode by press set");
                getActivity().setSleepRange(getDevice());
                break;
            case PedometerCommand.CMD_SET_SLEEP_RANGE:
                Log.d(TAG, "Sleep range set");
                ToastUtils.toast("Période de sommeil mise à jour", Toast.LENGTH_SHORT);
                getActivity().setSyncInProgress(false);
                break;

            case PedometerCommand.CMD_GET_TIME:
                Date date = PedometerResponseJp1304.readTime(data);
                Log.d(TAG, "Get time: " + date);
                break;

            case PedometerCommand.CMD_GET_BATTERY_LEVEL:
                int batteryLevel = PedometerResponseJp1304.readBatteryLevel(data);
                Log.d(TAG, "Battery level = " + batteryLevel);
                ToastUtils.toast("Batterie chargée", Toast.LENGTH_SHORT, false);
                break;
        }
    }
}
