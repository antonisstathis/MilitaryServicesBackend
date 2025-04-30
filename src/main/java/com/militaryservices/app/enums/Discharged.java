package com.militaryservices.app.enums;

public enum Discharged {
    IN_OPERATION,DISCHARGED;

    public static String getInOperation() {
        return IN_OPERATION.name().toLowerCase().replace("_"," ");
    }

    public static String getDischarged() {
        return DISCHARGED.name().toLowerCase();
    }

    public static String getDischarged(boolean discharged) {
        if(discharged)
            return getDischarged();
        else
            return getInOperation();
    }
}
