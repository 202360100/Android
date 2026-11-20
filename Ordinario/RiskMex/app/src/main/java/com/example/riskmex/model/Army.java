package com.example.riskmex.model;

public class Army {

    private int infantry;
    private int cavalry;
    private int artillery;

    public Army() {
        infantry = 0;
        cavalry = 0;
        artillery = 0;
    }

    public int getInfantry() {
        return infantry;
    }

    public int getCavalry() {
        return cavalry;
    }

    public int getArtillery() {
        return artillery;
    }

    public int getTotalUnits() {
        return infantry + cavalry + artillery;
    }

    public int getUnits(UnitType type) {
        switch (type) {
            case INFANTRY:
                return infantry;
            case CAVALRY:
                return cavalry;
            case ARTILLERY:
                return artillery;
            default:
                return 0;
        }
    }

    public void addUnits(UnitType type, int amount) {
        if (amount <= 0) return;

        switch (type) {
            case INFANTRY:
                infantry += amount;
                break;
            case CAVALRY:
                cavalry += amount;
                break;
            case ARTILLERY:
                artillery += amount;
                break;
        }
    }

    public boolean removeUnits(UnitType type, int amount) {
        if (amount <= 0 || getUnits(type) < amount) {
            return false;
        }

        switch (type) {
            case INFANTRY:
                infantry -= amount;
                break;
            case CAVALRY:
                cavalry -= amount;
                break;
            case ARTILLERY:
                artillery -= amount;
                break;
        }

        return true;
    }

    public boolean hasUnits() {
        return getTotalUnits() > 0;
    }
}
