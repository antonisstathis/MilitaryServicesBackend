package com.militaryservices.app.enums;

public enum Situation {
    ARMED,UNARMED;

    public static String getNameOfColumn() {
        return "situation";
    }
}
