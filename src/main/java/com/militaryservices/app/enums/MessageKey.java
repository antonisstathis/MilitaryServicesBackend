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
    SOLDIER_SAVED("soldiersaved");


    private final String key;

    MessageKey(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
