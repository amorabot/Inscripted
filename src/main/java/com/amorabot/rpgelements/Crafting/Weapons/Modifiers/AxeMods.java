package com.amorabot.rpgelements.Crafting.Weapons.Modifiers;

import com.amorabot.rpgelements.Crafting.Interfaces.TableDataAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum AxeMods implements TableDataAccess {

    PREFIXES(new ModTableEntry(WeaponModifiers.STAMINA,
                    new TierData(  4,  1, 3),
                    new TierData(  15, 4, 7),
                    new TierData(  30, 7, 10),
                    new TierData(  60, 11, 14),
                    new TierData(  81, 15, 20),
                    new TierData(  86, 21, 30) ),
            new ModTableEntry(WeaponModifiers.ADDED_PHYSICAL,
                    new TierData(  2,  2, 2,     4,5),
                    new TierData(  13, 6, 8,     12,15),
                    new TierData(  21, 10, 13,   21,25),
                    new TierData(  29, 13, 17,   28,32),
                    new TierData(  36, 16, 22,   35, 40),
                    new TierData(  46, 20, 28,   43, 51) ),
            new ModTableEntry(WeaponModifiers.ADDED_FIRE,
                    new TierData(  1,  2, 2,     4,5),
                    new TierData(  11, 6, 8,     12,15),
                    new TierData(  18, 10, 13,   21,25),
                    new TierData(  26, 13, 17,   28,32),
                    new TierData(  33, 16, 22,   35, 40),
                    new TierData(  42, 20, 28,   43, 51) ),
            new ModTableEntry(WeaponModifiers.ADDED_ABYSSAL,
                    new TierData(  83, 98, 149,  183,280) ),
            new ModTableEntry(WeaponModifiers.PERCENT_ELEMENTAL,
                    new TierData(  4,  19, 34),
                    new TierData(  15, 36, 51),
                    new TierData(  30, 53, 61),
                    new TierData(  60, 63, 71),
                    new TierData(  81, 73, 85),
                    new TierData(  86, 87, 100) ),
            new ModTableEntry(WeaponModifiers.PERCENT_PHYSICAL,
                    new TierData(  4,  19, 34),
                    new TierData(  15, 36, 51),
                    new TierData(  30, 53, 61),
                    new TierData(  60, 63, 71),
                    new TierData(  81, 73, 85),
                    new TierData(  86, 87, 100) ) ),

    SUFFIXES(new ModTableEntry(WeaponModifiers.STRENGTH,
                    new TierData(  1, 8,12),
                    new TierData(  11, 13,17),
                    new TierData(  22, 18,22),
                    new TierData(  33, 23, 27),
                    new TierData(  44, 28, 32),
                    new TierData(  55, 33, 37) ),
            new ModTableEntry(WeaponModifiers.DEXTERITY,
                    new TierData(  1, 8,12),
                    new TierData(  11, 13,17),
                    new TierData(  22, 18,22),
                    new TierData(  33, 23, 27),
                    new TierData(  44, 28, 32),
                    new TierData(  55, 33, 37) ),
            new ModTableEntry(WeaponModifiers.ACCURACY,
                    new TierData(  1, 80,130),
                    new TierData(  20, 131,215),
                    new TierData(  40, 216,325),
                    new TierData(  60, 326, 455),
                    new TierData(  75, 456, 624) ),
            new ModTableEntry(WeaponModifiers.ATTACK_SPEED,
                    new TierData(  1, 5, 7),
                    new TierData(  11, 8, 10),
                    new TierData(  22, 11, 13),
                    new TierData(  30, 14, 16),
                    new TierData(  37, 17, 19),
                    new TierData(  45, 20, 22),
                    new TierData(  60, 23, 25) ) );

    private final Map<WeaponModifiers, TierData[]> modifierTierMapping = new HashMap<>();
    private final List<WeaponModifiers> modifierList = new ArrayList<>();

    AxeMods(ModTableEntry... affixes){
        for (ModTableEntry affix : affixes){
            this.modifierList.add(affix.getMod());

            this.modifierTierMapping.put(affix.getMod(), affix.getTiers());
        }
    }

    @Override
    public TierData[] getModTiers(WeaponModifiers mod){
        return modifierTierMapping.get(mod);
    }
    @Override
    public List<Integer> getSortedTierIlvls(WeaponModifiers mod) {
        TierData[] modTiers = getModTiers(mod);
        List<Integer> aux = new ArrayList<>();
        for (TierData modTier : modTiers) {
            aux.add(modTier.ilvl());
        }
        return aux;
    }
    @Override
    public List<WeaponModifiers> getAllModNames(){
        return modifierList;
    }
}
