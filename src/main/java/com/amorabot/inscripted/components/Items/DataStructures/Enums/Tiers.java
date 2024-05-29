package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import lombok.Getter;

import java.util.Optional;

@Getter
public enum Tiers {
    T1(10, "LEATHER"),
    T2(25, "CHAINMAIL"),
    T3(45, "IRON"),
    T4(75, "DIAMOND"),
    T5(120, "GOLDEN");

    private final int maxLevel;
    private final String material;
    Tiers(int maxLevel, String material){
        this.maxLevel = maxLevel;
        this.material = material;
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
