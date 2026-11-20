package com.example.riskmex.model;

import java.util.ArrayList;
import java.util.List;

public class Territory {

    private final String name;
    private final Army army;
    private Player owner;
    private final List<String> neighbors;

    public Territory(String name) {
        this.name = name;
        this.army = new Army();
        this.neighbors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Army getArmy() {
        return army;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public List<String> getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(String neighbor) {
        if (!neighbors.contains(neighbor)) {
            neighbors.add(neighbor);
        }
    }

    public boolean isNeighbor(String territoryName) {
        return neighbors.contains(territoryName);
    }
}
