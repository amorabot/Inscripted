package com.amorabot.rpgelements.Crafting.Weapons.Enums;

import com.amorabot.rpgelements.Crafting.Interfaces.SelectableAffixTable;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.AxeMods;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.SwordMods;

public enum WeaponTypes implements SelectableAffixTable {

    AXE("#b01330"){
        @Override
        public Enum<?> getPrefixTable() {
            return AxeMods.PREFIXES;
        }

        @Override
        public Enum<?> getSuffixTable() {
            return AxeMods.SUFFIXES;
        }
        //"#e8885f"
    },
    SHORTSWORD("#e2e831") {
        @Override
        public Enum<?> getPrefixTable() {
            return SwordMods.PREFIXES;
        }

        @Override
        public Enum<?> getSuffixTable() {
            return SwordMods.SUFFIXES;
        }
        //"#99cce0"
    };
//    BOW,
//    SCYTHE,
//    WAND,
//    STAFF

    private final String color;
    WeaponTypes(String defaultColor){
        this.color = defaultColor;
    }
    public String getDefaulNameColor(){
        return color;
    }
}
