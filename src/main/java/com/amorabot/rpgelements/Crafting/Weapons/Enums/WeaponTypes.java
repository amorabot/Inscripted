package com.amorabot.rpgelements.Crafting.Weapons.Enums;

import com.amorabot.rpgelements.Crafting.Interfaces.SelectableAffixTable;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.AxeMods;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.SwordMods;

public enum WeaponTypes implements SelectableAffixTable {

    AXE{
        @Override
        public Enum<?> getPrefixTable() {
            return AxeMods.PREFIXES;
        }

        @Override
        public Enum<?> getSuffixTable() {
            return AxeMods.SUFFIXES;
        }
    },
    SHORTSWORD {
        @Override
        public Enum<?> getPrefixTable() {
            return SwordMods.PREFIXES;
        }

        @Override
        public Enum<?> getSuffixTable() {
            return SwordMods.SUFFIXES;
        }
    }
//    BOW,
//    SCYTHE,
//    WAND,
//    STAFF

}
