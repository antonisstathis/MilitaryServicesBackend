package com.militaryservices.app.enums;

public enum MessageKey {
    TOKEN_TAMPERED("tokentampered"),
    SOLDIER_UPDATED("soldierupdated"),
    UNAUTHORIZED("unauthorized"),
    SERVICES_DELETED("servicesdeleted"),
    ADD_SERVICES("addservices"),
    ADD_SERVICES_REJECTED("addservicesrejected"),
    NEW_SERVICES_CALCULATED("newservicescalculated"),
    DISCHARGE_SOLDIER_SUCCESSFUL("dischargesoldier"),
    DISCHARGE_SOLDIER_NOT_PERMITTED("dischargesoldienotpermitted"),
    SOLDIER_SAVED("soldiersaved"),
    REG_NUMBER_ALREADY_EXISTS("regnumberexists"),
    NEW_USER_SAVED("newusersaved"),
    USER_ALREADY_EXISTS("useralreadyexists"),
    VERIFY_CRT("verifycrt");


    private final String key;

    MessageKey(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
