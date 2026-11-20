package com.example.riskmex.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final int id;
    private final String name;
    private final int color;
    private final List<Territory> territories;

    private int cards;

    public Player(int id, String name, int color) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.territories = new ArrayList<>();
        this.cards = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public List<Territory> getTerritories() {
        return territories;
    }

    public int getTerritoryCount() {
        return territories.size();
    }

    public int getCards() {
        return cards;
    }

    public void addCard() {
        cards++;
    }

    public boolean useCards(int amount) {
        if (amount <= 0 || cards < amount) {
            return false;
        }

        cards -= amount;
        return true;
    }

    public void addTerritory(Territory territory) {
        if (!territories.contains(territory)) {
            territories.add(territory);
        }
    }

    public void removeTerritory(Territory territory) {
        territories.remove(territory);
    }
}
