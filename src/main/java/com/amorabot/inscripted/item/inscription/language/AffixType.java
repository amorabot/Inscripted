package com.amorabot.inscripted.item.inscription.language;

import lombok.Getter;

@Getter
public enum AffixType {
    PREFIX("ᚴ"), SUFFIX("ᚭ"), UNIQUE("ᛟ"), IMPLICIT("⸸");

    private final String runeIcon;

    AffixType(String icon){
        this.runeIcon = icon;
    }
}
