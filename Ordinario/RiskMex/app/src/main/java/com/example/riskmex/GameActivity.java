package com.example.riskmex;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.riskmex.model.Army;
import com.example.riskmex.model.CombatManager;
import com.example.riskmex.model.CombatResult;
import com.example.riskmex.model.GameManager;
import com.example.riskmex.model.Player;
import com.example.riskmex.model.Territory;
import com.example.riskmex.model.UnitType;

import java.util.List;

public class GameActivity extends AppCompatActivity {

    private GameManager gameManager;
    private CombatManager combatManager;

    private LinearLayout mainLayout;
    private LinearLayout playerContainer;

    private MexicoMapView mapView;

    private TextView turnText;
    private TextView reinforcementsText;
    private TextView territoryInfo;

    private Territory selectedTerritory;

    private int reinforcementsRemaining;

    private boolean gameFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int playerCount = getIntent().getIntExtra("PLAYER_COUNT", 2);

        gameManager = new GameManager(playerCount);
        combatManager = new CombatManager();

        reinforcementsRemaining = gameManager.calculateReinforcements(
                gameManager.getCurrentPlayer()
        );

        createScreen();
        updateScreen();
    }

    private void createScreen() {

        mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(16, 20, 16, 16);
        mainLayout.setBackgroundColor(0xFF18232D);

        TextView title = new TextView(this);
        title.setText("RISK MÉXICO");
        title.setTextSize(26);
        title.setTextColor(0xFFFFC857);
        title.setTypeface(null, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        mainLayout.addView(title);

        turnText = new TextView(this);
        turnText.setTextSize(18);
        turnText.setTextColor(0xFFFFFFFF);
        turnText.setGravity(Gravity.CENTER);
        turnText.setPadding(0, 15, 0, 5);

        mainLayout.addView(turnText);

        reinforcementsText = new TextView(this);
        reinforcementsText.setTextSize(15);
        reinforcementsText.setTextColor(0xFFFFD54F);
        reinforcementsText.setGravity(Gravity.CENTER);
        reinforcementsText.setPadding(0, 5, 0, 12);

        mainLayout.addView(reinforcementsText);

        playerContainer = new LinearLayout(this);
        playerContainer.setOrientation(LinearLayout.HORIZONTAL);
        playerContainer.setGravity(Gravity.CENTER);

        mainLayout.addView(playerContainer);

        TextView subtitle = new TextView(this);
        subtitle.setText("MAPA DE MÉXICO");
        subtitle.setTextColor(0xFFFFC857);
        subtitle.setTextSize(17);
        subtitle.setTypeface(null, Typeface.BOLD);
        subtitle.setPadding(0, 20, 0, 10);

        mainLayout.addView(subtitle);

        mapView = new MexicoMapView(
                this,
                gameManager.getTerritories()
        );

        mapView.setOnTerritorySelectedListener(territory -> {

            if (gameFinished) return;

            selectedTerritory = territory;
            showTerritoryInfo(territory);
        });

        LinearLayout.LayoutParams mapParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                );

        mainLayout.addView(mapView, mapParams);

        territoryInfo = new TextView(this);
        territoryInfo.setText(
                "Selecciona un estado para consultar su información."
        );
        territoryInfo.setTextColor(0xFFFFFFFF);
        territoryInfo.setTextSize(15);
        territoryInfo.setPadding(12, 15, 12, 15);
        territoryInfo.setBackgroundColor(0xFF263746);

        mainLayout.addView(territoryInfo);

        Button recruitButton = new Button(this);
        recruitButton.setText("RECLUTAR UNIDADES");
        recruitButton.setOnClickListener(v -> showRecruitmentOptions());

        mainLayout.addView(recruitButton);

        Button attackButton = new Button(this);
        attackButton.setText("ATACAR");
        attackButton.setOnClickListener(v -> showCombatSetup());

        mainLayout.addView(attackButton);

        Button nextTurnButton = new Button(this);
        nextTurnButton.setText("FINALIZAR TURNO");

        nextTurnButton.setOnClickListener(v -> {

            if (gameFinished || gameManager.isGameOver()) {
                return;
            }

            gameManager.nextTurn();

            selectedTerritory = null;
            mapView.setSelectedTerritory(null);

            reinforcementsRemaining =
                    gameManager.calculateReinforcements(
                            gameManager.getCurrentPlayer()
                    );

            updateScreen();
        });

        mainLayout.addView(nextTurnButton);

        setContentView(mainLayout);
    }

    private void updateScreen() {

        if (gameManager.isGameOver()) {
            Player winner = gameManager.getWinner();

            if (winner != null) {
                showVictoryDialog(winner);
            }

            return;
        }

        Player currentPlayer = gameManager.getCurrentPlayer();

        turnText.setText("Turno de: " + currentPlayer.getName());

        reinforcementsText.setText(
                "Refuerzos disponibles: " + reinforcementsRemaining
        );

        updatePlayerColors();

        mapView.invalidate();

        if (selectedTerritory == null) {
            territoryInfo.setText(
                    "Selecciona un estado para consultar su información."
            );
        } else {
            showTerritoryInfo(selectedTerritory);
        }
    }

    private void updatePlayerColors() {

        playerContainer.removeAllViews();

        for (Player player : gameManager.getPlayers()) {

            TextView playerLabel = new TextView(this);

            String status = gameManager.isEliminated(player)
                    ? "\nELIMINADO"
                    : "\n" + player.getTerritoryCount() + " estados";

            playerLabel.setText(
                    player.getName() + status
            );

            playerLabel.setTextColor(0xFFFFFFFF);
            playerLabel.setTextSize(12);
            playerLabel.setGravity(Gravity.CENTER);
            playerLabel.setPadding(8, 8, 8, 8);
            playerLabel.setBackgroundColor(
                    gameManager.isEliminated(player)
                            ? 0xFF555555
                            : player.getColor()
            );

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );

            params.setMargins(3, 3, 3, 3);

            playerContainer.addView(playerLabel, params);
        }
    }

    private void showTerritoryInfo(Territory territory) {

        Player owner = territory.getOwner();
        Army army = territory.getArmy();

        String ownerName = owner != null
                ? owner.getName()
                : "Sin propietario";

        String information =
                "ESTADO: " + territory.getName()
                        + "\nPROPIETARIO: " + ownerName
                        + "\n\nINFANTERÍA: " + army.getInfantry()
                        + "\nCABALLERÍA: " + army.getCavalry()
                        + "\nARTILLERÍA: " + army.getArtillery()
                        + "\nTOTAL DE TROPAS: " + army.getTotalUnits();

        territoryInfo.setText(information);
    }

    // --------------------------------------------------
    // COMBATE
    // --------------------------------------------------

    private void showCombatSetup() {

        if (gameFinished) return;

        if (selectedTerritory == null) {
            Toast.makeText(
                    this,
                    "Primero selecciona un estado.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Player currentPlayer = gameManager.getCurrentPlayer();

        if (selectedTerritory.getOwner() != currentPlayer) {
            Toast.makeText(
                    this,
                    "Debes seleccionar uno de tus propios estados.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        CombatSetupDialog combatDialog = new CombatSetupDialog(
                this,
                currentPlayer,
                selectedTerritory,
                gameManager.getTerritories(),
                this::executeCombat
        );

        combatDialog.show();
    }

    private void executeCombat(
            Territory attackerTerritory,
            Territory defenderTerritory,
            List<UnitType> attackerUnits,
            List<UnitType> defenderUnits
    ) {

        if (gameFinished) return;

        try {

            if (attackerTerritory.getOwner()
                    != gameManager.getCurrentPlayer()) {
                Toast.makeText(
                        this,
                        "Ya no puedes atacar desde ese estado.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (defenderTerritory.getOwner()
                    == gameManager.getCurrentPlayer()) {
                Toast.makeText(
                        this,
                        "No puedes atacar un estado propio.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (!attackerTerritory.isNeighbor(
                    defenderTerritory.getName()
            )) {
                Toast.makeText(
                        this,
                        "Los estados no son vecinos.",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            CombatResult result = combatManager.rollCombat(
                    attackerTerritory.getArmy(),
                    defenderTerritory.getArmy(),
                    attackerUnits,
                    defenderUnits
            );

            showCombatResult(
                    attackerTerritory,
                    defenderTerritory,
                    result
            );

        } catch (IllegalArgumentException exception) {

            Toast.makeText(
                    this,
                    exception.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void showCombatResult(
            Territory attacker,
            Territory defender,
            CombatResult result
    ) {

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(12), dp(18), dp(12));
        content.setBackgroundColor(0xFF172A3A);

        TextView matchup = new TextView(this);
        matchup.setText(
                attacker.getName() + "  VS  " + defender.getName()
        );
        matchup.setTextSize(17);
        matchup.setTypeface(null, Typeface.BOLD);
        matchup.setTextColor(0xFFFFC857);
        matchup.setGravity(Gravity.CENTER);
        matchup.setPadding(0, dp(8), 0, dp(15));

        content.addView(matchup);

        addCombatResultText(
                content,
                "ATACANTE: " + attacker.getName(),
                true
        );

        addDiceDetails(content, result.getAttackerDice());

        addCombatResultText(
                content,
                "DEFENSOR: " + defender.getName(),
                true
        );

        addDiceDetails(content, result.getDefenderDice());

        addCombatResultText(
                content,
                "COMPARACIONES",
                true
        );

        for (int i = 0; i < result.getComparisons().size(); i++) {

            CombatResult.Comparison comparison =
                    result.getComparisons().get(i);

            String line =
                    "Dado " + (i + 1)
                            + ": Atacante "
                            + comparison.getAttackerDie().getFinalValue()
                            + " vs Defensor "
                            + comparison.getDefenderDie().getFinalValue();

            addCombatResultText(content, line, false);

            if (comparison.didAttackerLose()) {
                addCombatResultText(
                        content,
                        "Pierde una unidad el atacante.",
                        false
                );
            } else {
                addCombatResultText(
                        content,
                        "Pierde una unidad el defensor.",
                        false
                );
            }
        }

        addCombatResultText(content, "BAJAS CALCULADAS", true);

        addCombatResultText(
                content,
                "Atacante: " + result.getAttackerCasualties()
                        + "\nDefensor: " + result.getDefenderCasualties(),
                false
        );

        addCombatResultText(
                content,
                "Al aceptar, cada jugador seleccionará "
                        + "las unidades que perderá.",
                false
        );

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setBackgroundColor(0xFF172A3A);
        scrollView.addView(content);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("RESULTADO DEL COMBATE")
                .setView(scrollView)
                .setPositiveButton("ACEPTAR", null)
                .setNegativeButton("CANCELAR", null)
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.white
            );

            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels
                            * 0.90),
                    (int) (getResources().getDisplayMetrics().heightPixels
                            * 0.75)
            );

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(v -> {

                        dialog.dismiss();

                        CombatResolutionDialog resolutionDialog =
                                new CombatResolutionDialog(
                                        this,
                                        gameManager,
                                        attacker,
                                        defender,
                                        result,
                                        this::onCombatResolutionFinished
                                );

                        resolutionDialog.start();
                    });
        });

        dialog.show();
    }

    private void onCombatResolutionFinished() {

        mapView.invalidate();

        updatePlayerColors();

        if (selectedTerritory != null) {
            showTerritoryInfo(selectedTerritory);
        }

        Player winner = gameManager.getWinner();

        if (winner != null) {
            showVictoryDialog(winner);
            return;
        }

        updateScreen();
    }

    private void showVictoryDialog(Player winner) {

        if (gameFinished) return;

        gameFinished = true;
        reinforcementsRemaining = 0;

        turnText.setText("PARTIDA TERMINADA");
        reinforcementsText.setText("¡Tenemos un ganador!");

        updatePlayerColors();

        mapView.invalidate();

        new AlertDialog.Builder(this)
                .setTitle("¡VICTORIA!")
                .setMessage(
                        winner.getName()
                                + " ha conquistado todos los estados "
                                + "de México.\n\n"
                                + "¡Felicidades!"
                )
                .setCancelable(false)
                .setPositiveButton(
                        "NUEVA PARTIDA",
                        (dialog, which) -> {

                            Intent intent = new Intent(
                                    GameActivity.this,
                                    GameActivity.class
                            );

                            intent.putExtra(
                                    "PLAYER_COUNT",
                                    gameManager.getPlayers().size()
                            );

                            startActivity(intent);
                            finish();
                        }
                )
                .setNegativeButton(
                        "MENÚ PRINCIPAL",
                        (dialog, which) -> finish()
                )
                .show();
    }

    private void addDiceDetails(
            LinearLayout layout,
            List<CombatResult.DieResult> dice
    ) {

        for (int i = 0; i < dice.size(); i++) {

            CombatResult.DieResult die = dice.get(i);

            String modifierText = die.getModifier() >= 0
                    ? "+" + die.getModifier()
                    : String.valueOf(die.getModifier());

            String information =
                    "Dado " + (i + 1)
                            + " - " + die.getUnitType().getDisplayName()
                            + "\nResultado: " + die.getDiceValue()
                            + " (" + modifierText + ")"
                            + " = " + die.getFinalValue();

            addCombatResultText(layout, information, false);
        }
    }

    private void addCombatResultText(
            LinearLayout layout,
            String text,
            boolean bold
    ) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(15);
        view.setTextColor(0xFFFFFFFF);
        view.setPadding(0, dp(8), 0, dp(8));

        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
            view.setTextColor(0xFFFFC857);
        }

        layout.addView(view);
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    // --------------------------------------------------
    // RECLUTAMIENTO
    // --------------------------------------------------

    private void showRecruitmentOptions() {

        if (gameFinished) return;

        if (selectedTerritory == null) {
            Toast.makeText(
                    this,
                    "Primero selecciona un estado.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Player currentPlayer = gameManager.getCurrentPlayer();

        if (selectedTerritory.getOwner() != currentPlayer) {
            Toast.makeText(
                    this,
                    "Solo puedes reclutar en tus propios territorios.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (reinforcementsRemaining <= 0) {
            Toast.makeText(
                    this,
                    "Ya utilizaste todos tus refuerzos.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 15, 35, 10);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Reclutar unidades")
                .setMessage(
                        "Selecciona el tipo de unidad para "
                                + selectedTerritory.getName()
                )
                .setView(layout)
                .setNegativeButton("Cancelar", null)
                .create();

        addRecruitmentOption(
                layout,
                dialog,
                "INFANTERÍA",
                UnitType.INFANTRY
        );

        addRecruitmentOption(
                layout,
                dialog,
                "CABALLERÍA",
                UnitType.CAVALRY
        );

        addRecruitmentOption(
                layout,
                dialog,
                "ARTILLERÍA",
                UnitType.ARTILLERY
        );

        dialog.show();
    }

    private void addRecruitmentOption(
            LinearLayout layout,
            AlertDialog dialog,
            String text,
            UnitType type
    ) {

        TextView option = new TextView(this);

        option.setText(text);
        option.setTextColor(0xFFFFFFFF);
        option.setTextSize(16);
        option.setGravity(Gravity.CENTER);
        option.setTypeface(null, Typeface.BOLD);

        GradientDrawable background = new GradientDrawable();
        background.setColor(0xFF304454);
        background.setCornerRadius(12);

        option.setBackground(background);

        int height = dp(48);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        height
                );

        params.setMargins(0, 6, 0, 6);

        layout.addView(option, params);

        option.setOnClickListener(v -> {
            dialog.dismiss();
            recruitUnit(type);
        });
    }

    private void recruitUnit(UnitType type) {

        if (gameFinished || selectedTerritory == null) return;

        Player currentPlayer = gameManager.getCurrentPlayer();

        if (selectedTerritory.getOwner() != currentPlayer) return;

        if (reinforcementsRemaining <= 0) return;

        selectedTerritory.getArmy().addUnits(type, 1);

        reinforcementsRemaining--;

        showTerritoryInfo(selectedTerritory);

        updateScreen();

        Toast.makeText(
                this,
                "Unidad reclutada: " + type.getDisplayName(),
                Toast.LENGTH_SHORT
        ).show();
    }
}
