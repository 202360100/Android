package com.example.riskmex.model;

public enum UnitType {

    INFANTRY("Infantería", 0, 0),
    CAVALRY("Caballería", 1, -1),
    ARTILLERY("Artillería", 1, 1);

    private final String displayName;
    private final int attackBonus;
    private final int defenseBonus;

    UnitType(String displayName, int attackBonus, int defenseBonus) {
        this.displayName = displayName;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }
}
