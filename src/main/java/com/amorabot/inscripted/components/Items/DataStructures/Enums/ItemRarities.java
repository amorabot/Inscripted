package com.amorabot.inscripted.components.Items.DataStructures.Enums;

public enum ItemRarities {
    COMMON(0),
    MAGIC(2),
    RARE(6),
    UNIQUE(10);

    final int maxMods;
    ItemRarities(int maxMods){
        this.maxMods = maxMods;
    }

    public int getMaxMods() {
        return maxMods;
    }
}
