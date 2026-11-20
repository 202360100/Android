package com.example.riskmex;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.riskmex.model.Army;
import com.example.riskmex.model.Player;
import com.example.riskmex.model.Territory;
import com.example.riskmex.model.UnitType;

import java.util.ArrayList;
import java.util.List;

public class CombatSetupDialog {

    public interface OnCombatReadyListener {
        void onCombatReady(
                Territory attackerTerritory,
                Territory defenderTerritory,
                List<UnitType> attackerUnits,
                List<UnitType> defenderUnits
        );
    }

    private final Context context;
    private final Player currentPlayer;
    private final Territory attackerTerritory;
    private final List<Territory> allTerritories;
    private final OnCombatReadyListener listener;

    private final UnitType[] unitTypes = {
            UnitType.INFANTRY,
            UnitType.CAVALRY,
            UnitType.ARTILLERY
    };

    private int[] attackerCounts = new int[3];
    private int[] defenderCounts = new int[3];

    private int attackerDiceCount;
    private int defenderDiceCount;

    private Territory defenderTerritory;

    public CombatSetupDialog(
            Context context,
            Player currentPlayer,
            Territory attackerTerritory,
            List<Territory> allTerritories,
            OnCombatReadyListener listener
    ) {
        this.context = context;
        this.currentPlayer = currentPlayer;
        this.attackerTerritory = attackerTerritory;
        this.allTerritories = allTerritories;
        this.listener = listener;
    }

    public void show() {
        if (attackerTerritory == null
                || attackerTerritory.getOwner() != currentPlayer) {
            return;
        }

        if (attackerTerritory.getArmy().getTotalUnits() < 2) {
            showMessage("Necesitas al menos 2 unidades para atacar.");
            return;
        }

        List<Territory> enemyNeighbors = new ArrayList<>();

        for (String neighborName : attackerTerritory.getNeighbors()) {
            for (Territory territory : allTerritories) {
                if (territory.getName().equals(neighborName)
                        && territory.getOwner() != currentPlayer
                        && territory.getArmy().getTotalUnits() > 0) {

                    enemyNeighbors.add(territory);
                    break;
                }
            }
        }

        if (enemyNeighbors.isEmpty()) {
            showMessage(
                    "Este estado no tiene territorios enemigos vecinos."
            );
            return;
        }

        showEnemySelection(enemyNeighbors);
    }

    private void showEnemySelection(List<Territory> enemies) {
        LinearLayout layout = createContainer();

        TextView title = createText(
                "SELECCIONA EL ESTADO ENEMIGO",
                18,
                Color.WHITE,
                true
        );

        layout.addView(title);

        TextView subtitle = createText(
                "Atacar desde: " + attackerTerritory.getName(),
                14,
                Color.LTGRAY,
                false
        );

        layout.addView(subtitle);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(layout)
                .create();

        for (Territory enemy : enemies) {
            String text = enemy.getName()
                    + "  |  Unidades: "
                    + enemy.getArmy().getTotalUnits();

            TextView option = createOption(text);

            option.setOnClickListener(v -> {
                defenderTerritory = enemy;
                dialog.dismiss();
                showDiceSelection();
            });

            layout.addView(option);
        }

        TextView cancel = createOption("CANCELAR");
        cancel.setOnClickListener(v -> dialog.dismiss());
        layout.addView(cancel);

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    private void showDiceSelection() {
        attackerCounts = new int[3];
        defenderCounts = new int[3];

        attackerDiceCount = 0;
        defenderDiceCount = 0;

        LinearLayout layout = createContainer();

        TextView title = createText(
                "PREPARAR COMBATE",
                20,
                Color.WHITE,
                true
        );

        layout.addView(title);

        TextView matchup = createText(
                attackerTerritory.getName()
                        + "  VS  "
                        + defenderTerritory.getName(),
                15,
                Color.LTGRAY,
                true
        );

        layout.addView(matchup);

        int maxAttackerDice = Math.min(
                3,
                attackerTerritory.getArmy().getTotalUnits() - 1
        );

        int maxDefenderDice = Math.min(
                2,
                defenderTerritory.getArmy().getTotalUnits()
        );

        TextView attackerTitle = createText(
                "UNIDADES DEL ATACANTE (máximo "
                        + maxAttackerDice + " dados)",
                15,
                Color.WHITE,
                true
        );

        layout.addView(attackerTitle);

        TextView attackerCounter = createText(
                "Dados seleccionados: 0/" + maxAttackerDice,
                14,
                Color.YELLOW,
                true
        );

        layout.addView(attackerCounter);

        for (int i = 0; i < unitTypes.length; i++) {
            final int index = i;

            addUnitSelector(
                    layout,
                    unitTypes[i],
                    attackerTerritory.getArmy(),
                    attackerCounts,
                    true,
                    maxAttackerDice,
                    attackerCounter
            );
        }

        TextView defenderTitle = createText(
                "UNIDADES DEL DEFENSOR (máximo "
                        + maxDefenderDice + " dados)",
                15,
                Color.WHITE,
                true
        );

        layout.addView(defenderTitle);

        TextView defenderCounter = createText(
                "Dados seleccionados: 0/" + maxDefenderDice,
                14,
                Color.YELLOW,
                true
        );

        layout.addView(defenderCounter);

        for (int i = 0; i < unitTypes.length; i++) {
            addUnitSelector(
                    layout,
                    unitTypes[i],
                    defenderTerritory.getArmy(),
                    defenderCounts,
                    false,
                    maxDefenderDice,
                    defenderCounter
            );
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(layout)
                .create();

        TextView confirm = createOption("CONFIRMAR ATAQUE");

        confirm.setOnClickListener(v -> {
            if (attackerDiceCount == 0 || defenderDiceCount == 0) {
                showMessage(
                        "Ambos jugadores deben seleccionar al menos un dado."
                );
                return;
            }

            List<UnitType> attackerUnits =
                    buildSelectedUnits(attackerCounts);

            List<UnitType> defenderUnits =
                    buildSelectedUnits(defenderCounts);

            dialog.dismiss();

            if (listener != null) {
                listener.onCombatReady(
                        attackerTerritory,
                        defenderTerritory,
                        attackerUnits,
                        defenderUnits
                );
            }
        });

        layout.addView(confirm);

        TextView cancel = createOption("CANCELAR");
        cancel.setOnClickListener(v -> dialog.dismiss());
        layout.addView(cancel);

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent
        );
    }

    private void addUnitSelector(
            LinearLayout parent,
            UnitType type,
            Army army,
            int[] counts,
            boolean attacking,
            int maxDice,
            TextView counter
    ) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(4, 8, 4, 8);

        TextView name = createText(
                type.getDisplayName()
                        + " (" + army.getUnits(type) + ")",
                14,
                Color.WHITE,
                false
        );

        row.addView(
                name,
                new LinearLayout.LayoutParams(
                        0,
                        dp(42),
                        1
                )
        );

        TextView minus = createSmallButton("-");
        TextView quantity = createText("0", 16, Color.WHITE, true);
        quantity.setGravity(Gravity.CENTER);

        TextView plus = createSmallButton("+");

        minus.setOnClickListener(v -> {
            int index = getUnitIndex(type);

            if (counts[index] > 0) {
                counts[index]--;

                if (attacking) {
                    attackerDiceCount--;
                } else {
                    defenderDiceCount--;
                }

                quantity.setText(String.valueOf(counts[index]));

                updateCounter(
                        counter,
                        attacking,
                        maxDice
                );
            }
        });

        plus.setOnClickListener(v -> {
            int index = getUnitIndex(type);

            int currentTotal = counts[index];
            int available = army.getUnits(type);

            int selectedDice = attacking
                    ? attackerDiceCount
                    : defenderDiceCount;

            if (currentTotal >= available) {
                return;
            }

            if (selectedDice >= maxDice) {
                return;
            }

            counts[index]++;

            if (attacking) {
                attackerDiceCount++;
            } else {
                defenderDiceCount++;
            }

            quantity.setText(String.valueOf(counts[index]));

            updateCounter(
                    counter,
                    attacking,
                    maxDice
            );
        });

        row.addView(minus, createButtonParams());
        row.addView(quantity, createButtonParams());
        row.addView(plus, createButtonParams());

        parent.addView(row);
    }

    private void updateCounter(
            TextView counter,
            boolean attacking,
            int maximum
    ) {
        int selected = attacking
                ? attackerDiceCount
                : defenderDiceCount;

        counter.setText(
                "Dados seleccionados: " + selected + "/" + maximum
        );
    }

    private List<UnitType> buildSelectedUnits(int[] counts) {
        List<UnitType> selected = new ArrayList<>();

        for (int i = 0; i < unitTypes.length; i++) {
            for (int j = 0; j < counts[i]; j++) {
                selected.add(unitTypes[i]);
            }
        }

        return selected;
    }

    private int getUnitIndex(UnitType type) {
        for (int i = 0; i < unitTypes.length; i++) {
            if (unitTypes[i] == type) {
                return i;
            }
        }

        return -1;
    }

    private LinearLayout createContainer() {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(20), dp(20), dp(20));
        layout.setBackground(createBackground("#172A3A", 18));

        return layout;
    }

    private TextView createText(
            String text,
            int size,
            int color,
            boolean bold
    ) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(0, dp(8), 0, dp(8));

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }

        return view;
    }

    private TextView createOption(String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(14);
        view.setTextColor(Color.WHITE);
        view.setTypeface(null, Typeface.BOLD);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(12), dp(12), dp(12), dp(12));
        view.setBackground(createBackground("#304454", 10));

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(48)
                );

        params.setMargins(0, dp(6), 0, dp(6));
        view.setLayoutParams(params);

        return view;
    }

    private TextView createSmallButton(String text) {
        TextView view = new TextView(context);
        view.setText(text);
        view.setTextSize(18);
        view.setTextColor(Color.WHITE);
        view.setTypeface(null, Typeface.BOLD);
        view.setGravity(Gravity.CENTER);
        view.setBackground(createBackground("#304454", 8));

        return view;
    }

    private LinearLayout.LayoutParams createButtonParams() {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        dp(42),
                        dp(42)
                );

        params.setMargins(dp(3), 0, dp(3), 0);

        return params;
    }

    private GradientDrawable createBackground(
            String color,
            int radius
    ) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(color));
        drawable.setCornerRadius(dp(radius));

        return drawable;
    }

    private int dp(int value) {
        return (int) (value * context.getResources()
                .getDisplayMetrics().density);
    }

    private void showMessage(String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("ACEPTAR", null)
                .show();
    }
}
