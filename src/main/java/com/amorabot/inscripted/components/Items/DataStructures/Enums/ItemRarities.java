package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import com.amorabot.inscripted.components.renderers.InscriptedPalette;
import lombok.Getter;

@Getter
public enum ItemRarities {
    COMMON(0, "&f",InscriptedPalette.WHITE),
    AUGMENTED(2, "&9",InscriptedPalette.AUGMENTED),
    RUNIC(6, "&e",InscriptedPalette.RUNIC),
    RELIC(10, "&c",InscriptedPalette.RELIC);

    final int maxMods;
    final String color;
    final InscriptedPalette colorComponent;
    ItemRarities(int maxMods, String rarityColor, InscriptedPalette color){
        this.maxMods = maxMods;
        this.color = rarityColor;
        this.colorComponent = color;
    }

}
