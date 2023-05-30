package com.amorabot.rpgelements.Crafting.Weapons.Enums;

import com.amorabot.rpgelements.Crafting.Interfaces.ItemModifierAccess;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.AxeMods;
import com.amorabot.rpgelements.Crafting.Weapons.Modifiers.SwordMods;

public enum WeaponTypes implements ItemModifierAccess {

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
