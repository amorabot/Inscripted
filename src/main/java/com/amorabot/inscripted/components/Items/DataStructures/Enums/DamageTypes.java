package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import com.amorabot.inscripted.components.renderers.InscriptedPalette;
import lombok.Getter;

@Getter
public enum DamageTypes {
    PHYSICAL(InscriptedPalette.PHYSICAL, "🗡"),
    FIRE(InscriptedPalette.FIRE, "\uD83D\uDD25"),
    LIGHTNING(InscriptedPalette.LIGHTNING, "⚡"),
    COLD(InscriptedPalette.COLD, "✽"),
    ABYSSAL(InscriptedPalette.ABYSSAL, "☽");

    final String character;
    private final InscriptedPalette dmgColor;
    DamageTypes(InscriptedPalette dmgColor, String specialChar){
        this.dmgColor = dmgColor;
        this.character = specialChar;
    }
}
