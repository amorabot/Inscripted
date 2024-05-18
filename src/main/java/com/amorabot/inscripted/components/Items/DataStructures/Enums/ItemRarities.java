package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import lombok.Getter;

@Getter
public enum ItemRarities {
    COMMON(0, "&f"),
    MAGIC(2, "&9"),
    RARE(6, "&e"),
    RELIC(10, "&c");

    final int maxMods;
    final String color;
    ItemRarities(int maxMods, String rarityColor){
        this.maxMods = maxMods;
        this.color = rarityColor;
    }

}
