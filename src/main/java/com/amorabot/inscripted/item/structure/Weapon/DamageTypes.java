package com.amorabot.inscripted.item.structure.Weapon;

import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.render.InscriptedPalette;
import lombok.Getter;

@Getter
public enum DamageTypes {
    PHYSICAL(Stats.PHYSICAL_DAMAGE,InscriptedPalette.PHYSICAL, "🗡"),
    FIRE(Stats.FIRE_DAMAGE,InscriptedPalette.FIRE, "\uD83D\uDD25"),
    LIGHTNING(Stats.LIGHTNING_DAMAGE,InscriptedPalette.LIGHTNING, "⚡"),
    COLD(Stats.COLD_DAMAGE,InscriptedPalette.COLD, "✽"),
    ABYSSAL(Stats.ABYSSAL_DAMAGE,InscriptedPalette.ABYSSAL, "☽");

    final String character;
    private final InscriptedPalette dmgColor;
    private final Stats dmgStat;
    DamageTypes(Stats dmgStat, InscriptedPalette dmgColor, String specialChar){
        this.dmgStat = dmgStat;
        this.dmgColor = dmgColor;
        this.character = specialChar;
    }
}
