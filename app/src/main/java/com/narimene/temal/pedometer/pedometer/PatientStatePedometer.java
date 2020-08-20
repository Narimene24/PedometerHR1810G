package com.narimene.temal.pedometer.pedometer;


import java.util.HashMap;
import java.util.Map;

import fr.semantic.ecare.app.android.components.sensor.pedometer.EcareFit.JP1304.PedometerMode;


/**
 * Class used to save a state of Pedometer sensor according the id Patient
 */

public class PatientStatePedometer {

    /**
     * patientsPedometerModeMap is a static Map used to save the state of all Patients
     */
    private static Map<String, PedometerMode> patientsPedometerModeMap = new HashMap<>();


    public static Map<String, PedometerMode> getPatientsPedometerModeMap() {
        return patientsPedometerModeMap;
    }

    /**
     * used to add a state pedometer , if the key id exists so we will replace only the mode
     * @param patientId : is the ID of Patient
     * @param mode is the last mode Pedometer according the Id Patient
     */
    public static void addPatientPedometerMode(String patientId, PedometerMode mode){
        int iterator = 0;

        for (Map.Entry entry : patientsPedometerModeMap.entrySet()){
            if (entry.getKey().equals(patientId)) {
                patientsPedometerModeMap.remove(entry);
                break;
            }
            iterator++;
        }
        patientsPedometerModeMap.put(patientId, mode);
    }
}
