package com.militaryservices.app.enums;

public enum MessageKey {
    TOKEN_TAMPERED("tokentampered"),
    SOLDIER_UPDATED("soldierupdated"),
    UNAUTHORIZED("unauthorized"),
    SERVICES_DELETED("servicesdeleted"),
    ADD_SERVICES("addservices"),
    ADD_SERVICES_REJECTED("addservicesrejected");

    private final String key;

    MessageKey(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
