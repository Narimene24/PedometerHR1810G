package com.narimene.temal.pedometer.pedometer.HR1755.modes;

import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.exception.BleException;

import java.lang.ref.WeakReference;

import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.PedometerHR1755BleGattCallback;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.commands.PedometerResponseHR1755;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755HR1810GCommand;
import fr.semantic.ecare.app.android.modules.patient.walk.minutewalk.Tools;
import fr.semantic.ecare.app.android.modules.patient.walk.minutewalk.left.WalkLeftFragment;
import fr.semantic.ecare.app.android.ui.App;
import fr.semantic.ecare.app.android.utils.Log;

/**
 * BleNotify HR1755 only for Command mode Pedometer
 *
 *  @version 1.3.0
 * @since 1.3.0
 */
public class CommandBleNotifyCallbackHR1755 extends BleNotifyCallback {
    public static final String TAG = CommandBleNotifyCallbackHR1755.class.getSimpleName();

    private WeakReference<PedometerHR1755BleGattCallback> mActivity = null;

    public CommandBleNotifyCallbackHR1755(PedometerHR1755BleGattCallback activity) {
        mActivity = new WeakReference<>(activity);
    }

    /**
     * check if the REAL TIME exercise is running
     * {@link WalkLeftFragment#onRunRealTimeReceiver}
     */
    @Override
    public void onNotifySuccess() {
        Log.i(TAG, "onNotifySuccess Command mode");
        mActivity.get().vibrate(mActivity.get().getDevice(), 1);
        App.getContext().sendBroadcast(Tools.getIntentStateRealTimeExercise());
    }

    @Override
    public void onNotifyFailure(BleException exception) {
        Log.e(TAG, "onNotifyFailure Command mode: " + exception.getDescription());
    }

    @Override
    public void onCharacteristicChanged(byte[] data) {
        Log.d(TAG, "onCharacteristicChanged Command mode");

        switch (data[0]) {
            case HR1755HR1810GCommand.CMD_START_REAL_TIME:
                Log.i(TAG, "HR1755HR1810GCommand.CMD_START_OR_STOP_REAL_TIME");
                PedometerResponseHR1755.getRealTime(data);
                break;

            case HR1755HR1810GCommand.CMD_SET_PERSONAL_DATA:
                Log.i(TAG, "Foot step changed");
                mActivity.get().vibrate(mActivity.get().getDevice() , 1);
        }
    }
}
