package com.amorabot.inscripted.components.Items.modifiers.unique;

import lombok.Getter;

@Getter
public enum TriggerTypes {
    ON_HIT("\uD83D\uDDE1"),
    WHEN_HIT("\uD83D\uDEE1"),
    ON_CAST("\uD83D\uDD25"),
    ON_CRIT("\uD83D\uDCA5"),
    ON_DODGE("\uD83C\uDF0A"),
    ON_DEATH("☠");

    private final String icon;

    TriggerTypes(String icon){
        this.icon = "&c("+icon+")&c&l";
    }
}
