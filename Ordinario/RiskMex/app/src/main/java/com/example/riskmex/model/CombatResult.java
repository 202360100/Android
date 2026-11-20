package com.example.riskmex.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CombatResult {

    public static class DieResult {
        private final UnitType unitType;
        private final int diceValue;
        private final int modifier;
        private final int finalValue;

        public DieResult(UnitType unitType, int diceValue, int modifier) {
            this.unitType = unitType;
            this.diceValue = diceValue;
            this.modifier = modifier;
            this.finalValue = diceValue + modifier;
        }

        public UnitType getUnitType() {
            return unitType;
        }

        public int getDiceValue() {
            return diceValue;
        }

        public int getModifier() {
            return modifier;
        }

        public int getFinalValue() {
            return finalValue;
        }
    }

    public static class Comparison {
        private final DieResult attackerDie;
        private final DieResult defenderDie;
        private final boolean attackerLost;

        public Comparison(
                DieResult attackerDie,
                DieResult defenderDie,
                boolean attackerLost
        ) {
            this.attackerDie = attackerDie;
            this.defenderDie = defenderDie;
            this.attackerLost = attackerLost;
        }

        public DieResult getAttackerDie() {
            return attackerDie;
        }

        public DieResult getDefenderDie() {
            return defenderDie;
        }

        public boolean didAttackerLose() {
            return attackerLost;
        }

        public boolean didDefenderLose() {
            return !attackerLost;
        }
    }

    private final List<DieResult> attackerDice;
    private final List<DieResult> defenderDice;
    private final List<Comparison> comparisons;
    private final int attackerCasualties;
    private final int defenderCasualties;

    public CombatResult(
            List<DieResult> attackerDice,
            List<DieResult> defenderDice,
            List<Comparison> comparisons
    ) {
        this.attackerDice = Collections.unmodifiableList(
                new ArrayList<>(attackerDice)
        );

        this.defenderDice = Collections.unmodifiableList(
                new ArrayList<>(defenderDice)
        );

        this.comparisons = Collections.unmodifiableList(
                new ArrayList<>(comparisons)
        );

        int attackerLosses = 0;
        int defenderLosses = 0;

        for (Comparison comparison : comparisons) {
            if (comparison.didAttackerLose()) {
                attackerLosses++;
            } else {
                defenderLosses++;
            }
        }

        this.attackerCasualties = attackerLosses;
        this.defenderCasualties = defenderLosses;
    }

    public List<DieResult> getAttackerDice() {
        return attackerDice;
    }

    public List<DieResult> getDefenderDice() {
        return defenderDice;
    }

    public List<Comparison> getComparisons() {
        return comparisons;
    }

    public int getAttackerCasualties() {
        return attackerCasualties;
    }

    public int getDefenderCasualties() {
        return defenderCasualties;
    }
}
