package com.militaryservices.app.dto;

public enum Fired {
    IN_OPERATION,FIRED;

    public static String getInOperation() {
        return IN_OPERATION.name().toLowerCase().replace("_"," ");
    }

    public static String getFired() {
        return FIRED.name().toLowerCase().replace("_"," ");
    }

    public static String getFired(boolean fired) {
        if(fired)
            return getFired();
        else
            return getInOperation();
    }
}
