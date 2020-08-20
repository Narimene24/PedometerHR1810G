package com.narimene.temal.pedometer.pedometer.EcareFit.JP1304.modes;

import android.widget.Toast;

import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;

import java.lang.ref.WeakReference;

import fr.semantic.ecare.app.android.components.sensor.pedometer.EcareFit.JP1304.PedometerBleGattCallback;
import fr.semantic.ecare.app.android.components.sensor.pedometer.EcareFit.JP1304.PedometerCommand;
import fr.semantic.ecare.app.android.components.sensor.pedometer.EcareFit.JP1304.PedometerResponseJp1304;
import fr.semantic.ecare.app.android.modules.patient.walk.minutewalk.Tools;
import fr.semantic.ecare.app.android.modules.patient.walk.minutewalk.left.WalkLeftFragment;
import fr.semantic.ecare.app.android.ui.App;
import fr.semantic.ecare.app.android.utils.Log;
import fr.semantic.ecare.app.android.utils.ui.ToastUtils;

/**
 * BleNotify Pedometer EcareFit JP1304 only for Command mode Pedometer
 *
 * @version 1.3.0
 * @since 1.3.0
 */
public class CommandBleNotifyCallback extends BleNotifyCallback {
    public static final String TAG = CommandBleNotifyCallback.class.getSimpleName();

    private WeakReference<PedometerBleGattCallback> mActivity = null;

    public CommandBleNotifyCallback(PedometerBleGattCallback activity) {
        mActivity = new WeakReference<>(activity);
    }

    public PedometerBleGattCallback getActivity(){
        return mActivity.get();
    }

    public BleDevice getDevice(){
        return getActivity().getDevice();
    }

    /**
     * check if the REAL TIME exercise is running
     * {@link WalkLeftFragment#onRunRealTimeReceiver}
     */
    @Override
    public void onNotifySuccess() {
        Log.i(TAG, "onNotifySuccess Command mode");
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
            case PedometerCommand.CMD_START_REAL_TIME:
                Log.i(TAG, "Pedometer.CMD_START_OR_STOP_REAL_TIME");
                PedometerResponseJp1304.getRealTime(data);
                break;

            case PedometerCommand.CMD_SET_PERSONAL_DATA:
                Log.i(TAG, "Personal data set");
                mActivity.get().vibrate(mActivity.get().getDevice());
                break;

            case PedometerCommand.CMD_SET_SLEEP_MODE_BY_PRESS:
                Log.i(TAG, "Sleep mode by press set");
                getActivity().setSleepRange(getDevice());
                break;
            case PedometerCommand.CMD_SET_SLEEP_RANGE:
                Log.i(TAG, "Sleep range set");
                ToastUtils.toast("Période de sommeil mise à jour", Toast.LENGTH_SHORT);
                mActivity.get().vibrate(mActivity.get().getDevice());
                break;
        }
    }
}
