package com.militaryservices.app.dto;

import java.util.Map;

public class CountServices {

    Map<Integer,HistoricalData> hdForArmed;
    Map<Integer,HistoricalData>  hdForUnarmed;
    Map<Integer,HistoricalData> hdForOut;

    public CountServices(Map<Integer, HistoricalData> hdForArmed, Map<Integer, HistoricalData> hdForUnarmed, Map<Integer, HistoricalData> hdForOut) {
        this.hdForArmed = hdForArmed;
        this.hdForUnarmed = hdForUnarmed;
        this.hdForOut = hdForOut;
    }

    public Map<Integer, HistoricalData> getHdForArmed() {
        return hdForArmed;
    }

    public Map<Integer, HistoricalData> getHdForUnarmed() {
        return hdForUnarmed;
    }

    public Map<Integer, HistoricalData> getHdForOut() {
        return hdForOut;
    }
}
