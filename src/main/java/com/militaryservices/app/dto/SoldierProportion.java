package com.militaryservices.app.dto;

public class SoldierProportion {

    private int soldId;
    private float proportion;

    public SoldierProportion(int soldId, float proportion) {
        this.soldId = soldId;
        this.proportion = proportion;
    }

    public int getSoldId() {
        return soldId;
    }

    public float getProportion() {
        return proportion;
    }

    public void setSoldId(int soldId) {
        this.soldId = soldId;
    }

    public void setProportion(float proportion) {
        this.proportion = proportion;
    }
}
