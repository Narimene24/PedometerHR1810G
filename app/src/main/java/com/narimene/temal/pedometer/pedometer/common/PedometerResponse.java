package com.narimene.temal.pedometer.pedometer.common;

import fr.semantic.ecare.app.android.modules.patient.walk.minutewalk.OnRealTime;
import fr.semantic.ecare.app.android.modules.patient.walk.minutewalk.left.WalkLeftFragment;

/**
 * Mother Response class used by all type of Pedometer (only for the Real Time)
 * consequently, one type on listener will be used in {@link WalkLeftFragment#mPedometerResponse} for all type of Pedometer
 */

public class PedometerResponse {

    public static PedometerResponse newInstance(){
        return new PedometerResponse();
    }

    protected static OnRealTime mListenerRealTime;
    protected static boolean isFirstRequestRealTime = true;

    public static void setListenerRealTime(OnRealTime mListener) {
        mListenerRealTime = mListener;
    }

    public static void setIsFirstRequestRealTime(boolean mIsFirstRequestRealTime) {
        isFirstRequestRealTime = mIsFirstRequestRealTime;
    }

}
