package com.example.riskmex.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class CombatManager {

    private final Random random;

    public CombatManager() {
        this.random = new Random();
    }

    public CombatManager(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Random no puede ser null.");
        }

        this.random = random;
    }

    public CombatResult rollCombat(
            Army attackerArmy,
            Army defenderArmy,
            List<UnitType> attackerUnits,
            List<UnitType> defenderUnits
    ) {
        validateCombat(
                attackerArmy,
                defenderArmy,
                attackerUnits,
                defenderUnits
        );

        List<CombatResult.DieResult> attackerDice =
                createDice(attackerUnits, true);

        List<CombatResult.DieResult> defenderDice =
                createDice(defenderUnits, false);

        sortDice(attackerDice);
        sortDice(defenderDice);

        List<CombatResult.Comparison> comparisons = new ArrayList<>();

        int comparisonCount = Math.min(
                attackerDice.size(),
                defenderDice.size()
        );

        for (int i = 0; i < comparisonCount; i++) {
            CombatResult.DieResult attackerDie = attackerDice.get(i);
            CombatResult.DieResult defenderDie = defenderDice.get(i);

            // En caso de empate, pierde el atacante.
            boolean attackerLost =
                    attackerDie.getFinalValue()
                            <= defenderDie.getFinalValue();

            comparisons.add(
                    new CombatResult.Comparison(
                            attackerDie,
                            defenderDie,
                            attackerLost
                    )
            );
        }

        return new CombatResult(
                attackerDice,
                defenderDice,
                comparisons
        );
    }

    private void validateCombat(
            Army attackerArmy,
            Army defenderArmy,
            List<UnitType> attackerUnits,
            List<UnitType> defenderUnits
    ) {
        if (attackerArmy == null || defenderArmy == null) {
            throw new IllegalArgumentException(
                    "Los ejércitos no pueden ser null."
            );
        }

        if (attackerUnits == null || defenderUnits == null) {
            throw new IllegalArgumentException(
                    "Debes seleccionar las unidades de ambos jugadores."
            );
        }

        if (attackerUnits.isEmpty() || attackerUnits.size() > 3) {
            throw new IllegalArgumentException(
                    "El atacante debe seleccionar entre 1 y 3 unidades."
            );
        }

        if (defenderUnits.isEmpty() || defenderUnits.size() > 2) {
            throw new IllegalArgumentException(
                    "El defensor debe seleccionar entre 1 y 2 unidades."
            );
        }

        validateSelectedUnits(attackerArmy, attackerUnits);
        validateSelectedUnits(defenderArmy, defenderUnits);
    }

    private void validateSelectedUnits(
            Army army,
            List<UnitType> selectedUnits
    ) {
        int infantry = 0;
        int cavalry = 0;
        int artillery = 0;

        for (UnitType type : selectedUnits) {
            if (type == null) {
                throw new IllegalArgumentException(
                        "No se puede seleccionar una unidad vacía."
                );
            }

            switch (type) {
                case INFANTRY:
                    infantry++;
                    break;

                case CAVALRY:
                    cavalry++;
                    break;

                case ARTILLERY:
                    artillery++;
                    break;
            }
        }

        if (infantry > army.getUnits(UnitType.INFANTRY)
                || cavalry > army.getUnits(UnitType.CAVALRY)
                || artillery > army.getUnits(UnitType.ARTILLERY)) {

            throw new IllegalArgumentException(
                    "Has seleccionado más unidades de las disponibles."
            );
        }
    }

    private List<CombatResult.DieResult> createDice(
            List<UnitType> selectedUnits,
            boolean attacking
    ) {
        List<CombatResult.DieResult> dice = new ArrayList<>();

        for (UnitType type : selectedUnits) {
            int diceValue = random.nextInt(6) + 1;

            int modifier;

            if (attacking) {
                modifier = type.getAttackBonus();
            } else {
                modifier = type.getDefenseBonus();
            }

            dice.add(
                    new CombatResult.DieResult(
                            type,
                            diceValue,
                            modifier
                    )
            );
        }

        return dice;
    }

    private void sortDice(List<CombatResult.DieResult> dice) {
        Collections.sort(
                dice,
                new Comparator<CombatResult.DieResult>() {
                    @Override
                    public int compare(
                            CombatResult.DieResult first,
                            CombatResult.DieResult second
                    ) {
                        return Integer.compare(
                                second.getFinalValue(),
                                first.getFinalValue()
                        );
                    }
                }
        );
    }
}
