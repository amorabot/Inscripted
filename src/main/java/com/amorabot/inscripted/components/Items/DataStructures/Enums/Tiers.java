package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import java.util.Optional;

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

    public Optional<Tiers> getPreviousTier(){
        if (this.ordinal() == 0){
            return Optional.empty();
        }
        //Its now safe to assume the accessed tier has a previous ordinal value, since the minimum has already been mapped
        return Optional.of(Tiers.values()[this.ordinal()-1]);
    }
    public static Tiers mapItemLevel(int itemLevel){
        for (Tiers tier : Tiers.values()){
            if (itemLevel<= tier.getMaxLevel() && itemLevel > 0){
                return tier;
            }
        }
        return Tiers.T1;
    }
}
