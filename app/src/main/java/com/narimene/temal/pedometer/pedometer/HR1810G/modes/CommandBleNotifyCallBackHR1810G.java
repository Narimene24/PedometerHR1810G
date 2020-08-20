package com.narimene.temal.pedometer.pedometer.HR1810G.modes;

import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.exception.BleException;

import java.lang.ref.WeakReference;

import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755HR1810GCommand;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.PedometerHR1810GBleGattCallBack;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.commands.PedometerResponseHR1810G;
import fr.semantic.ecare.app.android.modules.patient.walk.minutewalk.Tools;
import fr.semantic.ecare.app.android.modules.patient.walk.minutewalk.left.WalkLeftFragment;
import fr.semantic.ecare.app.android.ui.App;
import fr.semantic.ecare.app.android.utils.Log;

public class CommandBleNotifyCallBackHR1810G extends BleNotifyCallback {


/**
 * BleNotify HR1755 only for Command mode Pedometer
 *
 *  @version 1.3.0
 * @since 1.3.0
 */
    public static final String TAG = fr.semantic.ecare.app.android.components.sensor.pedometer.HR1810G.PedometerHR1810GBleGattCallBack.class.getSimpleName();

    private WeakReference<PedometerHR1810GBleGattCallBack> mActivity = null;

    public CommandBleNotifyCallBackHR1810G (PedometerHR1810GBleGattCallBack activity) {
        mActivity = new WeakReference<>(activity);
    }

    /**
     * check if the REAL TIME exercise is running
     * {@link WalkLeftFragment#}
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
                PedometerResponseHR1810G.getRealTimeTemperature(data);
                break;
        }
    }
}
