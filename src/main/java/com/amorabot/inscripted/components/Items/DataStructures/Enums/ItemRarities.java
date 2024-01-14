package com.amorabot.inscripted.components.Items.DataStructures.Enums;

public enum ItemRarities {
    //TODO: Store rarity colors here
    COMMON(0, "&f"),
    MAGIC(2, "&9"),
    RARE(6, "&e"),
    UNIQUE(10, "&c");

    final int maxMods;
    final String color;
    ItemRarities(int maxMods, String rarityColor){
        this.maxMods = maxMods;
        this.color = rarityColor;
    }

    public int getMaxMods() {
        return maxMods;
    }
    public String getColor() {
        return color;
    }
}
