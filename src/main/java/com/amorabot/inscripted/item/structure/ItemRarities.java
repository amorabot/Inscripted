package com.amorabot.inscripted.item.structure;

import com.amorabot.inscripted.item.render.InscriptedPalette;
import lombok.Getter;

@Getter
public enum ItemRarities {
    COMMON(0, "&f",InscriptedPalette.WHITE),
    AUGMENTED(2, "&9",InscriptedPalette.AUGMENTED),
    RUNIC(6, "&e",InscriptedPalette.RUNIC),
    RELIC(10, "&c",InscriptedPalette.RELIC);

    final int maxAffixes;
    final String color;
    final InscriptedPalette colorComponent;
    ItemRarities(int maxAffixes, String rarityColor, InscriptedPalette color){
        this.maxAffixes = maxAffixes;
        this.color = rarityColor;
        this.colorComponent = color;
    }

}
