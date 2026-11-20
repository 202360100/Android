package com.example.riskmex.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameManager {

    private final List<Player> players;
    private final List<Territory> territories;

    private int currentPlayerIndex;
    private final Random random;

    private static final String[] STATE_NAMES = {
            "Aguascalientes",
            "Baja California",
            "Baja California Sur",
            "Campeche",
            "Chiapas",
            "Chihuahua",
            "Ciudad de México",
            "Coahuila",
            "Colima",
            "Durango",
            "Estado de México",
            "Guanajuato",
            "Guerrero",
            "Hidalgo",
            "Jalisco",
            "Michoacán",
            "Morelos",
            "Nayarit",
            "Nuevo León",
            "Oaxaca",
            "Puebla",
            "Querétaro",
            "Quintana Roo",
            "San Luis Potosí",
            "Sinaloa",
            "Sonora",
            "Tabasco",
            "Tamaulipas",
            "Tlaxcala",
            "Veracruz",
            "Yucatán",
            "Zacatecas"
    };

    private static final int[] PLAYER_COLORS = {
            0xFF2878D0,
            0xFFE53935,
            0xFF43A047,
            0xFFFFB300
    };

    public GameManager(int playerCount) {

        if (playerCount < 2 || playerCount > 4) {
            throw new IllegalArgumentException(
                    "La partida necesita entre 2 y 4 jugadores."
            );
        }

        players = new ArrayList<>();
        territories = new ArrayList<>();
        random = new Random();

        currentPlayerIndex = 0;

        createPlayers(playerCount);
        createTerritories();
        createNeighbors();
        distributeTerritories();
    }

    private void createPlayers(int count) {

        for (int i = 0; i < count; i++) {

            Player player = new Player(
                    i,
                    "Jugador " + (i + 1),
                    PLAYER_COLORS[i]
            );

            players.add(player);
        }
    }

    private void createTerritories() {

        for (String stateName : STATE_NAMES) {
            territories.add(new Territory(stateName));
        }
    }

    private void createNeighbors() {

        connect("Aguascalientes", "Zacatecas");
        connect("Aguascalientes", "Jalisco");

        connect("Baja California", "Sonora");
        connect("Baja California", "Baja California Sur");

        connect("Campeche", "Yucatán");
        connect("Campeche", "Quintana Roo");
        connect("Campeche", "Tabasco");

        connect("Chiapas", "Oaxaca");
        connect("Chiapas", "Veracruz");
        connect("Chiapas", "Tabasco");

        connect("Chihuahua", "Sonora");
        connect("Chihuahua", "Sinaloa");
        connect("Chihuahua", "Durango");
        connect("Chihuahua", "Coahuila");

        connect("Ciudad de México", "Estado de México");
        connect("Ciudad de México", "Morelos");

        connect("Coahuila", "Durango");
        connect("Coahuila", "Zacatecas");
        connect("Coahuila", "Nuevo León");
        connect("Coahuila", "San Luis Potosí");

        connect("Colima", "Jalisco");
        connect("Colima", "Michoacán");

        connect("Durango", "Zacatecas");
        connect("Durango", "Nayarit");
        connect("Durango", "Sinaloa");

        connect("Estado de México", "Hidalgo");
        connect("Estado de México", "Querétaro");
        connect("Estado de México", "Michoacán");
        connect("Estado de México", "Guerrero");
        connect("Estado de México", "Morelos");
        connect("Estado de México", "Puebla");
        connect("Estado de México", "Tlaxcala");

        connect("Guanajuato", "Jalisco");
        connect("Guanajuato", "Zacatecas");
        connect("Guanajuato", "San Luis Potosí");
        connect("Guanajuato", "Querétaro");
        connect("Guanajuato", "Michoacán");

        connect("Guerrero", "Michoacán");
        connect("Guerrero", "Morelos");
        connect("Guerrero", "Puebla");
        connect("Guerrero", "Oaxaca");

        connect("Hidalgo", "San Luis Potosí");
        connect("Hidalgo", "Veracruz");
        connect("Hidalgo", "Puebla");
        connect("Hidalgo", "Tlaxcala");
        connect("Hidalgo", "Querétaro");

        connect("Jalisco", "Nayarit");
        connect("Jalisco", "Zacatecas");
        connect("Jalisco", "Michoacán");

        connect("Michoacán", "Querétaro");

        connect("Morelos", "Puebla");

        connect("Nayarit", "Sinaloa");
        connect("Nayarit", "Zacatecas");

        connect("Nuevo León", "Zacatecas");
        connect("Nuevo León", "San Luis Potosí");
        connect("Nuevo León", "Tamaulipas");

        connect("Oaxaca", "Puebla");
        connect("Oaxaca", "Veracruz");

        connect("Puebla", "Tlaxcala");
        connect("Puebla", "Veracruz");

        connect("Querétaro", "San Luis Potosí");

        connect("Quintana Roo", "Yucatán");

        connect("San Luis Potosí", "Tamaulipas");
        connect("San Luis Potosí", "Veracruz");
        connect("San Luis Potosí", "Zacatecas");

        connect("Sinaloa", "Sonora");

        connect("Tamaulipas", "Veracruz");

        connect("Zacatecas", "San Luis Potosí");
    }

    private void connect(String stateA, String stateB) {

        Territory a = findTerritory(stateA);
        Territory b = findTerritory(stateB);

        if (a != null && b != null) {
            a.addNeighbor(stateB);
            b.addNeighbor(stateA);
        }
    }

    private void distributeTerritories() {

        List<Territory> shuffledTerritories =
                new ArrayList<>(territories);

        Collections.shuffle(shuffledTerritories, random);

        for (int i = 0; i < shuffledTerritories.size(); i++) {

            Player player = players.get(i % players.size());
            Territory territory = shuffledTerritories.get(i);

            territory.setOwner(player);
            territory.getArmy().addUnits(UnitType.INFANTRY, 1);

            player.addTerritory(territory);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Territory> getTerritories() {
        return territories;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public Territory findTerritory(String name) {

        for (Territory territory : territories) {
            if (territory.getName().equals(name)) {
                return territory;
            }
        }

        return null;
    }

    // --------------------------------------------------
    // ELIMINACIÓN Y VICTORIA
    // --------------------------------------------------

    public boolean isEliminated(Player player) {
        return player == null || player.getTerritoryCount() <= 0;
    }

    public boolean isGameOver() {
        return getWinner() != null;
    }

    public Player getWinner() {

        Player remainingPlayer = null;
        int activePlayers = 0;

        for (Player player : players) {

            if (!isEliminated(player)) {
                activePlayers++;
                remainingPlayer = player;
            }
        }

        if (activePlayers == 1) {
            return remainingPlayer;
        }

        return null;
    }

    public void nextTurn() {

        if (isGameOver()) {
            return;
        }

        int checkedPlayers = 0;

        do {

            currentPlayerIndex++;

            if (currentPlayerIndex >= players.size()) {
                currentPlayerIndex = 0;
            }

            checkedPlayers++;

        } while (
                isEliminated(getCurrentPlayer())
                        && checkedPlayers < players.size()
        );
    }

    public int calculateReinforcements(Player player) {

        if (isEliminated(player) || isGameOver()) {
            return 0;
        }

        return Math.max(3, player.getTerritoryCount() / 3);
    }

    /**
     * Cambia el propietario de un territorio y actualiza
     * las listas de territorios de ambos jugadores.
     */
    public boolean conquerTerritory(
            Territory territory,
            Player newOwner
    ) {

        if (territory == null || newOwner == null) {
            return false;
        }

        if (!players.contains(newOwner)) {
            return false;
        }

        if (territory.getOwner() == newOwner) {
            return false;
        }

        Player previousOwner = territory.getOwner();

        if (previousOwner != null) {
            previousOwner.removeTerritory(territory);
        }

        territory.setOwner(newOwner);
        newOwner.addTerritory(territory);

        return true;
    }

    public boolean transferUnits(
            Territory origin,
            Territory destination,
            UnitType type,
            int amount
    ) {

        if (origin == null || destination == null || type == null) {
            return false;
        }

        if (origin == destination) {
            return false;
        }

        if (origin.getOwner() == null
                || origin.getOwner() != destination.getOwner()) {
            return false;
        }

        if (!origin.isNeighbor(destination.getName())) {
            return false;
        }

        if (amount <= 0) {
            return false;
        }

        if (origin.getArmy().getUnits(type) < amount) {
            return false;
        }

        if (origin.getArmy().getTotalUnits() - amount < 1) {
            return false;
        }

        if (!origin.getArmy().removeUnits(type, amount)) {
            return false;
        }

        destination.getArmy().addUnits(type, amount);

        return true;
    }
}
