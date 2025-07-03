package com.amorabot.inscripted.item.inscription.definition;

import lombok.Getter;

@Getter
public enum TriggerTypes {
    ON_HIT("\uD83D\uDDE1"),
    WHEN_HIT("\uD83D\uDEE1"),
    ON_CAST("\uD83D\uDD25"),
    ON_CRIT("\uD83D\uDCA5"),
    ON_DODGE("\uD83C\uDF0A"),
    ON_BLEED("❣"),
    ON_DEATH("☠"),
    ON_MOVEMENT("Mov."),
    ON_UTILITY("Uti."),
    ON_SIGNATURE("Sig.");

    private final String icon;

    TriggerTypes(String icon){
        this.icon = "("+icon+")";
    }
    public String getDisplayName(){
        char initial = this.name().charAt(0);
        char[] lc = this.name().replace("_", " ").toLowerCase().toCharArray();
        lc[0] = initial;
        return (new String(lc));
    }
}
