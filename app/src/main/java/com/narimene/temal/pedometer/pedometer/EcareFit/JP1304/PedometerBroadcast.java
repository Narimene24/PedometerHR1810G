package com.narimene.temal.pedometer.pedometer.EcareFit.JP1304;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.fernandocejas.arrow.checks.Preconditions;

import fr.semantic.ecare.app.android.domain.measure.pedometer.activity.DailyActivity;
import fr.semantic.ecare.app.android.domain.measure.pedometer.sleep.SleepPeriodModel;
import fr.semantic.ecare.app.android.ui.App;

public class PedometerBroadcast {

    public static final String ACTION_GET_SOMEDAY_TOTAL_ACTIVITY = "ACTION_GET_SOMEDAY_TOTAL_ACTIVITY";
    public static final String ACTION_GET_DATA_REALTIME = "ACTION_GET_DATA_REALTIME";

    public static final String ACTION_SYNCHRONIZE_BEGIN = "ACTION_SYNCRONIZE_BEGIN";
    public static final String ACTION_SYNCHRONIZE_END = "ACTION_SYNCRONIZE_END";

    private final static String PARAM_DATA_STEPS = "steps";
    private final static String PARAM_DATA_DISTANCE = "distance";

    public static final String ACTION_SET_SLEEP_PERIOD = "ACTION_SET_SLEEP_PERIOD";
    private final static String PARAM_DATA_SLEEP_PERIOD = "sleepPeriod";

    public static IntentFilter makeIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GET_SOMEDAY_TOTAL_ACTIVITY);
        intentFilter.addAction(ACTION_SET_SLEEP_PERIOD);
        return intentFilter;
    }

    public static void broadcastDailyTotal(DailyActivity dailyActivity) {

        if(dailyActivity != null) {
            Intent intent = new Intent(ACTION_GET_SOMEDAY_TOTAL_ACTIVITY);
            intent.putExtra(PARAM_DATA_STEPS, dailyActivity.getSteps());
            intent.putExtra(PARAM_DATA_DISTANCE, dailyActivity.getDistance());
            LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
        }
    }

    public static void broadcastDailyRealTime(DailyActivity dailyActivity) {

        if(dailyActivity != null) {
            Intent intent = new Intent(ACTION_GET_DATA_REALTIME);
            intent.putExtra(PARAM_DATA_STEPS, dailyActivity.getSteps());
            intent.putExtra(PARAM_DATA_DISTANCE, dailyActivity.getDistance());
            LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
        }
    }

    public static void broadcastSynchronizeBegin() {
        Intent intent = new Intent(ACTION_SYNCHRONIZE_BEGIN);
        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
    }

    public static void broadcastSynchronizeEnd() {
        Intent intent = new Intent(ACTION_SYNCHRONIZE_END);
        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
    }

    public static void broadcastSleepPeriod(SleepPeriodModel period) {
        Preconditions.checkArgument(period != null, "sleep period cannot be null");

        Intent intent = new Intent(ACTION_SET_SLEEP_PERIOD);
        intent.putExtra(PARAM_DATA_SLEEP_PERIOD, period);
        LocalBroadcastManager.getInstance(App.getContext()).sendBroadcast(intent);
    }

    /**
     * Get the step number value
     * @param intent the intent that stores the broadcast
     * @return the step number
     */
    public static int getSteps(Intent intent) {
        return intent.getIntExtra(PARAM_DATA_STEPS, 0);
    }

    /**
     * Get the distance value
     * @param intent the intent that stores the broadcast
     * @return the distance
     */
    public static int getDistance(Intent intent) {
        return intent.getIntExtra(PARAM_DATA_DISTANCE, 0);
    }

    /**
     * Get the sleep period
     * @param intent the intent that stores the broadcast
     * @return the distance
     */
    public static SleepPeriodModel getSleepPeriod(Intent intent) {
        return intent.getParcelableExtra(PARAM_DATA_SLEEP_PERIOD);
    }
}
