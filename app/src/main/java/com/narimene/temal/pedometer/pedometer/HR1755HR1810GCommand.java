package com.narimene.temal.pedometer.pedometer;

import java.util.Date;

import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.ActivityPeriod;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.AlarmDevice;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.AutoHeartRateData;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.BasicParametersDevice;
import fr.semantic.ecare.app.android.components.sensor.pedometer.HR1755.models.UserPersonalData;
import fr.semantic.ecare.app.android.components.sensor.utils.Convert;


public class HR1755HR1810GCommand {

    public static final int CMD_VIBRATE                              = 0x36;
    public static final int CMD_GET_TIME                             = 0x41;
    public static final int CMD_SET_TIME                             = 0x01;
    public static final int CMD_SET_PERSONAL_DATA                    = 0x02;
    public static final int CMD_GET_PERSONAL_DATA                    = 0x42;
    public static final int CMD_SET_BASIC_PARAMETERS                 = 0x03;
    public static final int CMD_GET_BASIC_PARAMETERS                 = 0x04;
    public static final int CMD_START_REAL_TIME                      = 0x09;
    public static final int CMD_SET_FOOTSTEP_GOAL                    = 0x0B;
    public static final int CMD_GET_TARGET_STEPS                     = 0x4B;
    public static final int CMD_GET_BATTERY_LEVEL                    = 0x13;
    public static final int CMD_GET_SOFTWARE_VERSION                 = 0x27;
    public static final int CMD_SET_RESET_FACTORY                    = 0x12;
    public static final int CMD_SET_RESET_MCU                        = 0x2E;
    public static final int CMD_SET_NAME_DEVICE                      = 0x3D;
    public static final int CMD_SET_AUTO_HEART_RATE_PERIOD           = 0x2A;
    public static final int CMD_READ_AUTO_HEART_RATE_CHECKING_PERIOD = 0x2B;
    public static final int CMD_SET_ALARMS                           = 0x23;
    public static final int CMD_SET_ACTIVITY                         = 0x25;
    public static final int CMD_GET_ACTIVITY                         = 0x26;
    public static final int CMD_GET_ALL_STEPS_DATA                   = 0x51;
    public static final int CMD_GET_DETAILED_STEPS_DATA              = 0x52;
    public static final int CMD_GET_DETAILED_SLEEP_DATA              = 0x53;
    public static final int CMD_GET_DETAILED_HEART_RATE_DATA         = 0x54;
    public static final int CMD_GET_SINGLE_HR_DATA                   = 0x55;
    public static final int CMD_GET_HRV_DATA                         = 0x56;
    public static final int CMD_GET_ALARMS_DATA                      = 0x57;
    public static final int CMD_GET_SLEEP_DEBUGGING_DATA             = 0x58;
    public static final int CMD_GET_GPS_DATA                         = 0x5A;
    public static final int CMD_GET_EXERCISE_DATA                    = 0x5C;
    public static final int CMD_ENABLE_GPS                           = 0x34;
    public static final int CMD_START_WORK_CONTROL                   = 0x19;
    public static final int CMD_TURN_ON_CAMERA                       = 0x20;
    public static final int CMD_TURN_OFF_CAMERA                      = 0x10;
    public static final int CMD_GET_TEMPERATURE                      = 0x60;
    public static final int CMD_ACTIVATE_REAL_TIME_TEMPERATURE_HR    = 0x01;
    public static final int CMD_DESACTIVATE_REAL_TIME_TEMPERATURE_HR = 0x00;








    public static byte[] getTime() {

        byte[] command = new byte[16];
        command[0]  = (byte) CMD_GET_TIME;

        command[1]  = (byte) 0;
        command[2]  = (byte) 0;
        command[3]  = (byte) 0;
        command[4]  = (byte) 0;
        command[5]  = (byte) 0;
        command[6]  = (byte) 0;
        command[7]  = (byte) 0;
        command[8]  = (byte) 0;
        command[9]  = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] vibrate(int times) {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_VIBRATE;

        command[1] = Convert.intInByte(times); // vibration

        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] setTime() {

        Date date = new Date(System.currentTimeMillis());

        byte[] command = new byte[16];
        command[0] = (byte) CMD_SET_TIME;

        command[1] = Convert.intInByte(date.getYear()-100);
        command[2] = Convert.intInByte(date.getMonth()+1);
        command[3] = Convert.intInByte(date.getDate() );
        command[4] = Convert.intInByte(date.getHours());
        command[5] = Convert.intInByte(date.getMinutes());
        command[6] = Convert.intInByte(date.getSeconds());

        command[7] = (byte)0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;
        command[15] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public final static int GENDER_MALE   = 1;
    public final static int GENDER_FEMALE = 0;

    public static byte[] setPersonalData(UserPersonalData user) {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_SET_PERSONAL_DATA;

        command[1] = (byte) user.getGender();
        command[2] = (byte) user.getAge();
        command[3] = (byte) user.getHeight();
        command[4] = (byte) user.getWeight();
        command[5] = (byte) user.getStrideLength();

        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getPersonaData() {

        byte[] command = new byte[16];
        command[0]=(byte) CMD_GET_PERSONAL_DATA;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    /**
     * Parameters only for setBasicParameters();
     */
    public static final int UNIT_KM             = 0x80;
    public static final int UNIT_ML             = 0x81;

    public static final int FORMAT_12H          = 0x81;
    public static final int FORMAT_24H          = 0x80;

    public static final int WRITE_SENSE_ENABLE  = 0x81;
    public static final int WRITE_SENSE_DISABLE = 0x80;

    public static final int LEFT_HAND           = 0x81;
    public static final int RIGHT_HAND          = 0x80;

    public static final int HORIZONTAL_DISPLAY  = 0x81;
    public static final int VERTICAL_DISPLAY    = 0x80;

    public static final int ANCS_ENABLE         = 0x81;
    public static final int ANCS_DISABLE        = 0x80;

    public static byte[] setBasicParameters(BasicParametersDevice parametersDevice) {

        byte[] command = new byte[16];
        command[0]=(byte) CMD_SET_BASIC_PARAMETERS;

        command[1] = (byte) parametersDevice.getDistance_unit();
        command[2] = (byte) parametersDevice.getHour_unit();
        command[3] = (byte) parametersDevice.getWrist_sens();
        command[4] = (byte) parametersDevice.getChoice_hand();
        command[5] = (byte) parametersDevice.getPosition_display();
        command[6] = (byte) parametersDevice.getANCS();

        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getBasicData() {

        byte[] command = new byte[16];
        command[0]=(byte) CMD_GET_BASIC_PARAMETERS;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] startRealTime() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_START_REAL_TIME;

        command[1] = (byte) 0x01;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] stopRealTime() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_START_REAL_TIME;

        command[1] = (byte) 0x00;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] setFootstepGoal(int goal) {

        int goal_byte1_high      = goal/(256*256*256);
        int goal_byte2_med       = goal/(256*256);
        int goal_byte3_low       = goal/(256);
        int goal_byte4_hyper_low = (goal) % 256;

        byte[] command = new byte[16];
        command[0] = (byte) CMD_SET_FOOTSTEP_GOAL;

        command[1] = (byte) goal_byte4_hyper_low;
        command[2] = (byte) goal_byte3_low;
        command[3] = (byte) goal_byte2_med;
        command[4] = (byte) goal_byte1_high;

        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getTargetSteps() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_TARGET_STEPS;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }


    public static byte[] getBatteryLevel() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_BATTERY_LEVEL;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getSoftwareVersion() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_SOFTWARE_VERSION;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] setResetFactory() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_SET_RESET_FACTORY;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] setResetMCU() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_SET_RESET_MCU;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] setNameDevice(int [] name) {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_SET_NAME_DEVICE;

        int length = name.length;

        for (int i=0 ; i < length && i < 15; i++){
            command[i+1] = (byte) name[i];
        }

        for (int i = length; i < 15; i++){
            command[i+1] = (byte) 0;
        }

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] setAutoHeartRatePeriod(AutoHeartRateData data) {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_SET_AUTO_HEART_RATE_PERIOD;

        if (data.getMode() == 0){
            command[1] = (byte) 0;
        }else {
            command[2] =  Convert.intInByte(data.getStartHour());                              // start Hour
            command[3] =  Convert.intInByte(data.getStartMinute());                            // start minute
            command[4] =  Convert.intInByte(data.getEndHour());                                // end Hour
            command[5] =  Convert.intInByte(data.getEndMinute());                              // end Minute
            command[6] = (byte) Convert.getSumForAllDays(data.getDaysWeek()); // days of week // all days week
            if (data.getMode() == 1){
                command[1] = (byte) 1;
                command[7] = (byte) 0;
                command[8] = (byte) 0;
            }else {
                command[1] = (byte) 2;
                command[7] = (byte) (data.getInterval() / 256);
                command[8] = (byte) (data.getInterval() %  256);
            }
        }

        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] readAutoHeartRateChekingPeriod() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_READ_AUTO_HEART_RATE_CHECKING_PERIOD;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }


    public static byte[] setAlarms(AlarmDevice alarm){

        byte[] command = new byte[37];

        for (int i=0 ; i < command.length ; i++){
            command[i] = (byte) 0;
        }

        command[0] = (byte) CMD_SET_ALARMS;

        command[1] = (byte) 0;// ??le laisser a 0 ?? il ne reste que le parametre (char) par rapport a la doc
        command[2] = (byte) alarm.getRanking(); // c'est la position de l'alarme
        command[3] = (byte) alarm.getState(); // enable / disable
        command[4] = (byte) 2; // type alarm (l'image sur l'ecran de la montre changera )
        command[5] =  Convert.intInByte(alarm.getHour());
        command[6] =  Convert.intInByte(alarm.getMinute());
        command[7] = (byte) 127; // put everyDay (que jeudi ? = 16, que mercredi ? = 8 / jeudi et mercredi 16+8 , jeudi et mercredi 11000 = 16+8 )

        return command;
    }

    public static byte[] setActivityPeriod(ActivityPeriod data) {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_SET_ACTIVITY;

        command[1] = Convert.intInByte(data.getStartHour());                               // begin hour
        command[2] = Convert.intInByte(data.getStartMinute());                             // begin minute
        command[3] = Convert.intInByte(data.getEndHour());                                 // end hour
        command[4] = Convert.intInByte(data.getEndMinute());                               // end minute
        command[5] = (byte) Convert.getSumForAllDays(data.getDaysWeek()); // enable week
        command[6] = (byte) data.getRemindPeriod();                                        // remind period
        command[7] = (byte) data.getMinExerciseSteps();
        command[8] = (byte) 1;                                                             // function will be activated all time (steps min exercise)

        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getActivityPeriod() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_ACTIVITY;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getAllStepsData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_ALL_STEPS_DATA;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] deleteAllStepsData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_ALL_STEPS_DATA;

        command[1] = (byte) 0x99; // en hexa ce n'est pas mentionné dans la doc
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    private static int requestCounterStepsDetails = 1 ;

    public static int getRequestCounterStepsDetails() {
        return requestCounterStepsDetails;
    }

    public static void setRequestCounterStepsDetails(int counterForDetailedSteps) {
        requestCounterStepsDetails = counterForDetailedSteps;
    }

    public static byte[] getDetailedStepsData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_DETAILED_STEPS_DATA;

        command[1] = (byte) requestCounterStepsDetails; //0, 1 , 2
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        requestCounterStepsDetails++;
        return command;
    }

    public static byte[] deleteAllDetailedStepsData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_DETAILED_STEPS_DATA;

        command[1] = (byte) 0x99; // en hexa ce n'est pas mentionné dans la doc
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }


    public static int getRequestCounterSleepDetails() {
        return requestCounterSleepDetails;
    }

    public static void setRequestCounterSleepDetails(int requestCounterSleepDetails) {
        HR1755HR1810GCommand.requestCounterSleepDetails = requestCounterSleepDetails;
    }

    private static int requestCounterSleepDetails = 1 ;
    public static byte[] getDetailedSleepData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_DETAILED_SLEEP_DATA;

        command[1] = (byte) requestCounterSleepDetails; //0, 1 , 2
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        requestCounterSleepDetails++;
        return command;
    }

    public static byte[] deleteAllDetailedSleepData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_DETAILED_SLEEP_DATA;

        command[1] = (byte) 0x99; // en hexa ce n'est pas mentionné dans la doc
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getdetailedHeartRateData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_DETAILED_HEART_RATE_DATA;

        command[1] = (byte) 0x00; //0, 1 , 2
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] deleteAllDetailedHeartRateData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_DETAILED_HEART_RATE_DATA;

        command[1] = (byte) 0x99; // en hexa ce n'est pas mentionné dans la doc
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getSingleHRData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_SINGLE_HR_DATA;

        command[1] = (byte) 0x00; //0, 1 , 2
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] deleteAllSingleHRData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_SINGLE_HR_DATA;

        command[1] = (byte) 0x99; // en hexa ce n'est pas mentionné dans la doc
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }



    public static byte[] getHRVData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_HRV_DATA;

        command[1] = (byte) 0x00; //0, 1 , 2
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] deleteAllHRVData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_HRV_DATA;

        command[1] = (byte) 0x99; // en hexa ce n'est pas mentionné dans la doc
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getAlarmsData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_ALARMS_DATA;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getSleepDebuggingData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_SLEEP_DEBUGGING_DATA;

        command[1] = (byte) 0x00;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getGPSData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_GPS_DATA;

        command[1] = (byte) 0x00;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] getExerciseData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_EXERCISE_DATA;

        command[1] = (byte) 0x00;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] deleteAllExerciseData() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_GET_EXERCISE_DATA;

        command[1] = (byte) 0x99; // en hexa ce n'est pas mentionné dans la doc
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] enableGPS() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_ENABLE_GPS;

        command[1] = (byte) 0x01;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] disableGPS() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_ENABLE_GPS;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] startWorkControl() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_START_WORK_CONTROL;

        command[1] = (byte) 1;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] stopWorkControl() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_START_WORK_CONTROL;

        command[1] = (byte) 0x04;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] turnOnCamera() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_TURN_ON_CAMERA;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] turnOffCamera() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_TURN_OFF_CAMERA;

        command[1] = (byte) 0;
        command[2] = (byte) 0;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }







// Commands for TEMPERATURE



    private static int requestCounterTemperatureDetails = 1 ;

    public static int getRequestCounterTemperatureDetails() {
        return requestCounterTemperatureDetails;
    }

    public static void setRequestCounterTemperatureDetails(int counterForDetailedSteps) {
        requestCounterTemperatureDetails = counterForDetailedSteps;
    }




    public static final int readTemperatureToLastPosition = 0x01;
    public static final int readTemperaturePosition =  0x02;


    public static byte[] getAllTemperatures() {

        byte[] command = new byte[16];
        command[0]  = (byte) CMD_GET_TEMPERATURE;

        command[1]  = (byte) readTemperatureToLastPosition;
        command[2]  = (byte) 0;
        command[3]  = (byte) 0;
        command[4]  = (byte) 0;
        command[5]  = (byte) 0;
        command[6]  = (byte) 0;
        command[7]  = (byte) 0;
        command[8]  = (byte) 0;
        command[9]  = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }


// Commands for REALTIME with Temperature

    public static byte[] startRealTimeWithTemperature() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_START_REAL_TIME;

        command[1] = (byte) CMD_ACTIVATE_REAL_TIME_TEMPERATURE_HR;
        command[2] = (byte) CMD_ACTIVATE_REAL_TIME_TEMPERATURE_HR;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }

    public static byte[] stopRealTimeWithTemperature() {

        byte[] command = new byte[16];
        command[0] = (byte) CMD_START_REAL_TIME;

        command[1] = (byte) CMD_DESACTIVATE_REAL_TIME_TEMPERATURE_HR;
        command[2] = (byte) CMD_DESACTIVATE_REAL_TIME_TEMPERATURE_HR;
        command[3] = (byte) 0;
        command[4] = (byte) 0;
        command[5] = (byte) 0;
        command[6] = (byte) 0;
        command[7] = (byte) 0;
        command[8] = (byte) 0;
        command[9] = (byte) 0;
        command[10] = (byte) 0;
        command[11] = (byte) 0;
        command[12] = (byte) 0;
        command[13] = (byte) 0;
        command[14] = (byte) 0;

        command[15] = Convert.get_checksum(command, 15);
        return command;
    }





}
