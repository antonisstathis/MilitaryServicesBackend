package com.militaryservices.app.enums;

public enum Active {
    ACTIVE,FREE_OF_DUTY;

    public static String getFreeOfDuty() {
        return FREE_OF_DUTY.name().toLowerCase().replace("_"," ");
    }
}
