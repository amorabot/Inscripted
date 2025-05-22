package com.amorabot.inscripted.components.Items.DataStructures.Enums;

import lombok.Getter;

@Getter
public enum Affix {
    PREFIX("ᚴ"),
    SUFFIX("ᚭ"),
    RELIC("ᛟ"),
    IMPLICIT("⸸");

    private final String runeIcon;

    Affix(String icon){
        this.runeIcon = icon;
    }
}
