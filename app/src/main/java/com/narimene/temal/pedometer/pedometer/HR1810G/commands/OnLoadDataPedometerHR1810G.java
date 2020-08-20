package com.narimene.temal.pedometer.pedometer.HR1810G.commands;

import java.util.List;

import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.StepDataDay;
import fr.semantic.ecare.app.android.domain.measure.MeasureModel;

/**
 * OnLoadDataPedometerHR1755
 */

public interface OnLoadDataPedometerHR1810G {

    void onInsufficientLoadingSteps();
    void onLoadingStepsIsFinish(List treatmentList);
    void onLoadingStepsDaysIsFinish(List<StepDataDay> listStepsAllDays);
    void onTreatmentStepsIsFinish();

    void onInsufficientLoadingSleep();
    void onLoadingSleepIsFinish(List treatmentList);
    void onTreatmentSleepIsFinish();

    void onInsufficientLoadingTemperature();
    void onLoadingTemperatureIsFinish(List<MeasureModel> detailedTemperatureDataList);
}
