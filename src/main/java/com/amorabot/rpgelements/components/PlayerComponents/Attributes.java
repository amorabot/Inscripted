package com.amorabot.rpgelements.components.PlayerComponents;

public class Attributes {
    private int points;
    private int intelligence;
    private int agility;
    private int strength;

    public Attributes(int points, int intelligence, int agility, int strength) {
        this.points = points;
        this.intelligence = intelligence;
        this.agility = agility;
        this.strength = strength;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
    public int getIntelligence() {
        return intelligence;
    }

    public int getAgility() {
        return agility;
    }

    public int getStrength() {
        return strength;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}
