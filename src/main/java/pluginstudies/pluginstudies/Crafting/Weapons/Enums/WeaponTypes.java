package pluginstudies.pluginstudies.Crafting.Weapons.Enums;

import pluginstudies.pluginstudies.Crafting.Interfaces.ItemModifierAccess;
import pluginstudies.pluginstudies.Crafting.Weapons.Modifiers.AxeMods;
import pluginstudies.pluginstudies.Crafting.Weapons.Modifiers.SwordMods;

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
