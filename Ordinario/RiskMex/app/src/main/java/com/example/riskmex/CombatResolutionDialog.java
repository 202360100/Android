package com.example.riskmex;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.riskmex.model.CombatResult;
import com.example.riskmex.model.GameManager;
import com.example.riskmex.model.Player;
import com.example.riskmex.model.Territory;
import com.example.riskmex.model.UnitType;

import java.util.ArrayList;

public class CombatResolutionDialog {

    public interface OnResolutionFinishedListener {
        void onResolutionFinished();
    }

    private final Context context;
    private final GameManager gameManager;
    private final Territory attackerTerritory;
    private final Territory defenderTerritory;
    private final CombatResult result;
    private final OnResolutionFinishedListener listener;

    private int attackerLossesRemaining;
    private int defenderLossesRemaining;

    private Player attackerPlayer;
    private Player defenderPlayer;

    public CombatResolutionDialog(
            Context context,
            GameManager gameManager,
            Territory attackerTerritory,
            Territory defenderTerritory,
            CombatResult result,
            OnResolutionFinishedListener listener
    ) {
        this.context = context;
        this.gameManager = gameManager;
        this.attackerTerritory = attackerTerritory;
        this.defenderTerritory = defenderTerritory;
        this.result = result;
        this.listener = listener;

        attackerPlayer = attackerTerritory.getOwner();
        defenderPlayer = defenderTerritory.getOwner();

        attackerLossesRemaining = result.getAttackerCasualties();
        defenderLossesRemaining = result.getDefenderCasualties();
    }

    public void start() {

        if (attackerPlayer == null || defenderPlayer == null) {
            finish();
            return;
        }

        if (attackerPlayer == defenderPlayer) {
            finish();
            return;
        }

        applyAttackerLosses();
    }

    private void applyAttackerLosses() {

        if (attackerLossesRemaining <= 0) {
            applyDefenderLosses();
            return;
        }

        showCasualtySelection(
                attackerPlayer,
                attackerTerritory,
                true
        );
    }

    private void applyDefenderLosses() {

        if (defenderLossesRemaining <= 0) {
            checkConquest();
            return;
        }

        showCasualtySelection(
                defenderPlayer,
                defenderTerritory,
                false
        );
    }

    // --------------------------------------------------
    // SELECCIÓN DE BAJAS
    // --------------------------------------------------

    private void showCasualtySelection(
            Player player,
            Territory territory,
            boolean isAttacker
    ) {

        if (territory.getArmy().getTotalUnits() <= 0) {

            if (isAttacker) {
                attackerLossesRemaining = 0;
                applyDefenderLosses();
            } else {
                defenderLossesRemaining = 0;
                checkConquest();
            }

            return;
        }

        UnitType[] availableTypes = getAvailableTypes(territory);

        if (availableTypes.length == 0) {

            if (isAttacker) {
                attackerLossesRemaining = 0;
                applyDefenderLosses();
            } else {
                defenderLossesRemaining = 0;
                checkConquest();
            }

            return;
        }

        String title = isAttacker
                ? "BAJA DEL ATACANTE"
                : "BAJA DEL DEFENSOR";

        String message =
                player.getName()
                        + ", selecciona la unidad que perderás.\n\n"
                        + "Estado: " + territory.getName()
                        + "\nBajas pendientes: "
                        + (isAttacker
                        ? attackerLossesRemaining
                        : defenderLossesRemaining);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(15), dp(20), dp(15));
        layout.setBackgroundColor(0xFF172A3A);

        TextView information = new TextView(context);
        information.setText(message);
        information.setTextSize(16);
        information.setTextColor(0xFFFFFFFF);
        information.setPadding(0, 0, 0, dp(15));

        layout.addView(information);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layout)
                .setCancelable(false)
                .create();

        for (UnitType type : availableTypes) {

            int availableUnits =
                    territory.getArmy().getUnits(type);

            TextView option = new TextView(context);

            option.setText(
                    type.getDisplayName()
                            + "  (Disponibles: "
                            + availableUnits + ")"
            );

            option.setTextColor(0xFFFFFFFF);
            option.setTextSize(16);
            option.setTypeface(null, Typeface.BOLD);
            option.setGravity(Gravity.CENTER);

            GradientDrawable background = new GradientDrawable();
            background.setColor(0xFF304454);
            background.setCornerRadius(dp(12));

            option.setBackground(background);

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dp(52)
                    );

            params.setMargins(0, dp(5), 0, dp(5));

            layout.addView(option, params);

            option.setOnClickListener(v -> {

                boolean removed = territory.getArmy().removeUnits(
                        type,
                        1
                );

                if (!removed) {
                    Toast.makeText(
                            context,
                            "No se pudo aplicar la baja.",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                dialog.dismiss();

                if (isAttacker) {
                    attackerLossesRemaining--;
                    applyAttackerLosses();
                } else {
                    defenderLossesRemaining--;
                    applyDefenderLosses();
                }
            });
        }

        dialog.show();
    }

    private UnitType[] getAvailableTypes(Territory territory) {

        ArrayList<UnitType> available = new ArrayList<>();

        for (UnitType type : UnitType.values()) {

            if (territory.getArmy().getUnits(type) > 0) {
                available.add(type);
            }
        }

        return available.toArray(new UnitType[0]);
    }

    // --------------------------------------------------
    // CONQUISTA
    // --------------------------------------------------

    private void checkConquest() {

        if (defenderTerritory.getArmy().getTotalUnits() > 0) {

            showFinalMessage(
                    "El estado continúa bajo el control de "
                            + defenderPlayer.getName() + "."
            );

            return;
        }

        boolean conquered = gameManager.conquerTerritory(
                defenderTerritory,
                attackerPlayer
        );

        if (!conquered) {

            showFinalMessage(
                    "No fue posible completar la conquista."
            );

            return;
        }

        showTransferSelection();
    }

    // --------------------------------------------------
    // TRANSFERENCIA DE TROPAS
    // --------------------------------------------------

    private void showTransferSelection() {

        int maximumTransfer =
                attackerTerritory.getArmy().getTotalUnits() - 1;

        if (maximumTransfer <= 0) {

            showFinalMessage(
                    attackerPlayer.getName()
                            + " ha conquistado "
                            + defenderTerritory.getName()
                            + ", pero no tiene tropas disponibles "
                            + "para trasladar."
            );

            return;
        }

        int[] selected = {0, 0, 0};

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(12), dp(20), dp(12));
        layout.setBackgroundColor(0xFF172A3A);

        TextView information = new TextView(context);

        information.setText(
                "¡Estado conquistado!\n\n"
                        + attackerPlayer.getName()
                        + " ahora controla "
                        + defenderTerritory.getName()
                        + ".\n\nSelecciona las tropas que deseas trasladar. "
                        + "Debes dejar al menos una unidad en "
                        + attackerTerritory.getName()
                        + ".\n\nMáximo disponible: "
                        + maximumTransfer
        );

        information.setTextSize(15);
        information.setTextColor(0xFFFFFFFF);
        information.setPadding(0, 0, 0, dp(15));

        layout.addView(information);

        TextView totalText = new TextView(context);
        totalText.setTextSize(16);
        totalText.setTypeface(null, Typeface.BOLD);
        totalText.setTextColor(0xFFFFC857);
        totalText.setGravity(Gravity.CENTER);
        totalText.setPadding(0, dp(8), 0, dp(12));

        layout.addView(totalText);

        addTransferRow(
                layout,
                "Infantería",
                UnitType.INFANTRY,
                0,
                selected,
                maximumTransfer,
                totalText
        );

        addTransferRow(
                layout,
                "Caballería",
                UnitType.CAVALRY,
                1,
                selected,
                maximumTransfer,
                totalText
        );

        addTransferRow(
                layout,
                "Artillería",
                UnitType.ARTILLERY,
                2,
                selected,
                maximumTransfer,
                totalText
        );

        updateTransferTotal(
                selected,
                maximumTransfer,
                totalText
        );

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("TRANSFERIR TROPAS")
                .setView(layout)
                .setPositiveButton("CONFIRMAR", null)
                .setNegativeButton("CANCELAR", (d, which) -> {
                    showFinalMessage(
                            "El estado fue conquistado. "
                                    + "No se trasladaron tropas."
                    );
                })
                .setCancelable(false)
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        int total = selected[0]
                                + selected[1]
                                + selected[2];

                        if (total <= 0) {

                            Toast.makeText(
                                    context,
                                    "Selecciona al menos una unidad.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        boolean success = transferSelectedUnits(selected);

                        if (!success) {

                            Toast.makeText(
                                    context,
                                    "No se pudo realizar la transferencia.",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        dialog.dismiss();

                        showFinalMessage(
                                "Conquista y transferencia completadas.\n\n"
                                        + "Estado conquistado: "
                                        + defenderTerritory.getName()
                                        + "\nTropas trasladadas: "
                                        + total
                        );
                    });
        });

        dialog.show();
    }

    private void addTransferRow(
            LinearLayout layout,
            String label,
            UnitType type,
            int index,
            int[] selected,
            int maximumTransfer,
            TextView totalText
    ) {

        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(5), 0, dp(5));

        TextView name = new TextView(context);

        name.setText(
                label + "\nDisponibles: "
                        + attackerTerritory.getArmy().getUnits(type)
        );

        name.setTextColor(0xFFFFFFFF);
        name.setTextSize(14);

        row.addView(
                name,
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView amountText = new TextView(context);
        amountText.setText("0");
        amountText.setTextColor(0xFFFFC857);
        amountText.setTextSize(17);
        amountText.setGravity(Gravity.CENTER);
        amountText.setMinWidth(dp(30));

        Button minus = new Button(context);
        minus.setText("-");

        Button plus = new Button(context);
        plus.setText("+");

        minus.setOnClickListener(v -> {

            if (selected[index] > 0) {

                selected[index]--;

                amountText.setText(String.valueOf(selected[index]));

                updateTransferTotal(
                        selected,
                        maximumTransfer,
                        totalText
                );
            }
        });

        plus.setOnClickListener(v -> {

            int available =
                    attackerTerritory.getArmy().getUnits(type);

            int total = selected[0]
                    + selected[1]
                    + selected[2];

            if (selected[index] < available
                    && total < maximumTransfer) {

                selected[index]++;

                amountText.setText(String.valueOf(selected[index]));

                updateTransferTotal(
                        selected,
                        maximumTransfer,
                        totalText
                );
            }
        });

        row.addView(minus);
        row.addView(amountText);
        row.addView(plus);

        layout.addView(row);
    }

    private void updateTransferTotal(
            int[] selected,
            int maximum,
            TextView totalText
    ) {

        int total = selected[0] + selected[1] + selected[2];

        totalText.setText(
                "Seleccionadas: " + total + " / " + maximum
        );
    }

    private boolean transferSelectedUnits(int[] selected) {

        UnitType[] types = {
                UnitType.INFANTRY,
                UnitType.CAVALRY,
                UnitType.ARTILLERY
        };

        for (int i = 0; i < types.length; i++) {

            if (selected[i] > 0) {

                boolean success = gameManager.transferUnits(
                        attackerTerritory,
                        defenderTerritory,
                        types[i],
                        selected[i]
                );

                if (!success) {
                    return false;
                }
            }
        }

        return true;
    }

    // --------------------------------------------------
    // FINALIZACIÓN
    // --------------------------------------------------

    private void showFinalMessage(String message) {

        new AlertDialog.Builder(context)
                .setTitle("RESOLUCIÓN DEL COMBATE")
                .setMessage(message)
                .setPositiveButton(
                        "ACEPTAR",
                        (dialog, which) -> finish()
                )
                .setCancelable(false)
                .show();
    }

    private void finish() {

        if (listener != null) {
            listener.onResolutionFinished();
        }
    }

    private int dp(int value) {

        return (int) (value * context.getResources()
                .getDisplayMetrics().density);
    }
}
