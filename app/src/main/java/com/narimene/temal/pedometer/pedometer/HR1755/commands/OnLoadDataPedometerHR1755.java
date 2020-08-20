package com.narimene.temal.pedometer.pedometer.HR1755.commands;

import java.util.List;

import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDataDay;

/**
 * OnLoadDataPedometerHR1755
 */

public interface OnLoadDataPedometerHR1755 {

    void onInsufficientLoadingSteps();
    void onLoadingStepsIsFinish(List treatmentList);
    void onLoadingStepsDaysIsFinish(List<StepDataDay> listStepsAllDays);
    void onTreatmentStepsIsFinish();

    void onInsufficientLoadingSleep();
    void onLoadingSleepIsFinish(List treatmentList);
    void onTreatmentSleepIsFinish();
}
