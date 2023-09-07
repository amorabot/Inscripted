package com.amorabot.rpgelements.components.Items.DataStructures.Enums;

public enum Tiers {
    T1(10),
    T2(25),
    T3(45),
    T4(75),
    T5(120);

    private final int maxLevel;
    Tiers(int maxLevel){
        this.maxLevel = maxLevel;
    }
    public int getMaxLevel() {
        return maxLevel;
    }
}
