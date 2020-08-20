package com.narimene.temal.pedometer.pedometer.HR1755.models;

public class BasicParametersDevice {
    private int distance_unit;
    private int hour_unit;
    private int wrist_sens;
    private int choice_hand;
    private int position_display;
    private int ANCS;

    public BasicParametersDevice(){};

    public BasicParametersDevice(int distance_unit, int hour_unit, int wrist_sens, int choice_hand, int position_display, int ANCS) {
        this.distance_unit = distance_unit;
        this.hour_unit = hour_unit;
        this.wrist_sens = wrist_sens;
        this.choice_hand = choice_hand;
        this.position_display = position_display;
        this.ANCS = ANCS;
    }

    public static BasicParametersDevice getInstance(int distance_unit, int hour_unit, int wrist_sens, int choice_hand, int position_display, int ANCS){
        return new BasicParametersDevice(distance_unit, hour_unit, wrist_sens, choice_hand, position_display, ANCS);
    }

    public int getDistance_unit() {
        return distance_unit;
    }

    public void setDistance_unit(int distance_unit) {
        this.distance_unit = distance_unit;
    }

    public int getHour_unit() {
        return hour_unit;
    }

    public void setHour_unit(int hour_unit) {
        this.hour_unit = hour_unit;
    }

    public int getWrist_sens() {
        return wrist_sens;
    }

    public void setWrist_sens(int wrist_sens) {
        this.wrist_sens = wrist_sens;
    }

    public int getChoice_hand() {
        return choice_hand;
    }

    public void setChoice_hand(int choice_hand) {
        this.choice_hand = choice_hand;
    }

    public int getPosition_display() {
        return position_display;
    }

    public void setPosition_display(int position_display) {this.position_display = position_display;}

    public int getANCS() {
        return ANCS;
    }

    public void setANCS(int ANCS) {
        this.ANCS = ANCS;
    }

    @Override
    public String toString() {

        String distanceUnit   ="";if (distance_unit    == 0){ distanceUnit   = "Km"       ; }else { distanceUnit   = "Mile"       ; }
        String hourUnit       ="";if (hour_unit        == 0){ hourUnit       = "24H"      ; }else { hourUnit       = "12H"        ; }
        String wristSens      ="";if (wrist_sens       == 0){ wristSens      = "Désactivé"; }else { wristSens      ="Activé"      ; }
        String handChoice     ="";if (choice_hand      == 0){ handChoice     = "Droite"   ; }else { handChoice     ="Gauche"      ; }
        String positionDiplay ="";if (position_display == 0){ positionDiplay = "Verticale"; }else { positionDiplay = "Horizentale"; }
        String ancs           ="";if (ANCS             == 0){ ancs           = "Désactivé"; }else { ancs           = "Activé"     ; }

        return "BasicParametersDevice{" +   distance_unit  +
                "distance_unit= "       + distanceUnit +
                ", hour_unit="          + hourUnit +
                ", wrist_sens= "        + wristSens +
                ", choice_hand= "       + handChoice +
                ", position_display= "  + positionDiplay +
                ", ANCS= "              + ancs +
                '}';
    }
}
