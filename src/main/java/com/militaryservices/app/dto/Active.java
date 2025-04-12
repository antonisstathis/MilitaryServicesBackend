package com.militaryservices.app.dto;

public enum Active {
    ACTIVE,FREE_OF_DUTY;

    public static String getFreeOfDuty() {
        return FREE_OF_DUTY.name().toLowerCase().replace("_"," ");
    }
}
