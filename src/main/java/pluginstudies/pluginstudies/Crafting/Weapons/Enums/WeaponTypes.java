package pluginstudies.pluginstudies.Crafting.Weapons.Enums;

import pluginstudies.pluginstudies.Crafting.Interfaces.ItemModifierAccess;
import pluginstudies.pluginstudies.Crafting.ItemTypes;
import pluginstudies.pluginstudies.Crafting.Weapons.Modifiers.AxeMods;
import pluginstudies.pluginstudies.Crafting.Weapons.Modifiers.WeaponModifiers;
import pluginstudies.pluginstudies.Crafting.Weapons.ShortswordAffixes;

public enum WeaponTypes implements ItemModifierAccess {

    AXE{ //AXE SERÁ O UNICO TESTADO POR ENQUANTO
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
            return ShortswordAffixes.PREFIXES;
        }

        @Override
        public Enum<?> getSuffixTable() {
            return ShortswordAffixes.SUFFIXES;
        }
    }
//    BOW,
//    SCYTHE,
//    WAND,
//    STAFF

}
