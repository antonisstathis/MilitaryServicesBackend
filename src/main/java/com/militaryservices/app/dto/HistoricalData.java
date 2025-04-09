package com.militaryservices.app.dto;

public class HistoricalData {
    private int soldierId;
    private long numberOfServices;

    public HistoricalData() {

    }

    public HistoricalData(int soldierId, long numberOfServices) {
        this.soldierId = soldierId;
        this.numberOfServices = numberOfServices;
    }

    public int getSoldierId() {
        return soldierId;
    }

    public long getNumberOfServices() {
        return numberOfServices;
    }

    public void setSoldierId(int soldierId) {
        this.soldierId = soldierId;
    }

    public void setNumberOfServices(long numberOfServices) {
        this.numberOfServices = numberOfServices;
    }
}
