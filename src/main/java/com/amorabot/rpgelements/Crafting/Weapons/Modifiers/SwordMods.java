package com.amorabot.rpgelements.Crafting.Weapons.Modifiers;

import com.amorabot.rpgelements.Crafting.CraftingUtils;
import com.amorabot.rpgelements.Crafting.Interfaces.AffixTableAcessInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amorabot.rpgelements.Crafting.CraftingUtils.rangeOf;

public enum SwordMods implements AffixTableAcessInterface {

    PREFIXES(new ModTableEntry(WeaponModifiers.STAMINA,
                    new ItemLevelRange(  4, CraftingUtils.rangeOf(1,3)),
                    new ItemLevelRange(  15, CraftingUtils.rangeOf(4,7)),
                    new ItemLevelRange(  30, CraftingUtils.rangeOf(7,10)),
                    new ItemLevelRange(  60, CraftingUtils.rangeOf(11, 14)),
                    new ItemLevelRange(  81, CraftingUtils.rangeOf(15, 20)),
                    new ItemLevelRange(  86, CraftingUtils.rangeOf(21, 30)) ),
            new ModTableEntry(WeaponModifiers.ADDED_PHYSICAL,
                    new ItemLevelRange(  2, CraftingUtils.rangeOf(2,2, 4,5)),
                    new ItemLevelRange(  13, CraftingUtils.rangeOf(6,8,12,15)),
                    new ItemLevelRange(  21, CraftingUtils.rangeOf(10,13, 21,25)),
                    new ItemLevelRange(  29, CraftingUtils.rangeOf(13, 17, 28,32)),
                    new ItemLevelRange(  36, CraftingUtils.rangeOf(16, 22, 35, 40)),
                    new ItemLevelRange(  46, CraftingUtils.rangeOf(20, 28, 43, 51)) ),
            new ModTableEntry(WeaponModifiers.ADDED_FIRE,
                    new ItemLevelRange(  1, CraftingUtils.rangeOf(2,2, 4,5)),
                    new ItemLevelRange(  11, CraftingUtils.rangeOf(6,8,12,15)),
                    new ItemLevelRange(  18, CraftingUtils.rangeOf(10,13, 21,25)),
                    new ItemLevelRange(  26, CraftingUtils.rangeOf(13, 17, 28,32)),
                    new ItemLevelRange(  33, CraftingUtils.rangeOf(16, 22, 35, 40)),
                    new ItemLevelRange(  42, CraftingUtils.rangeOf(20, 28, 43, 51)) ),
            new ModTableEntry(WeaponModifiers.ADDED_ABYSSAL,
                    new ItemLevelRange(  83, CraftingUtils.rangeOf(98,149, 183,280)) ),
            new ModTableEntry(WeaponModifiers.PERCENT_ELEMENTAL,
                    new ItemLevelRange(  4, CraftingUtils.rangeOf(19,34)),
                    new ItemLevelRange(  15, CraftingUtils.rangeOf(36,51)),
                    new ItemLevelRange(  30, CraftingUtils.rangeOf(53,61)),
                    new ItemLevelRange(  60, CraftingUtils.rangeOf(63, 71)),
                    new ItemLevelRange(  81, CraftingUtils.rangeOf(73, 85)),
                    new ItemLevelRange(  86, CraftingUtils.rangeOf(87, 100)) ),
            new ModTableEntry(WeaponModifiers.PERCENT_PHYSICAL,
                    new ItemLevelRange(  4, CraftingUtils.rangeOf(19,34)),
                    new ItemLevelRange(  15, CraftingUtils.rangeOf(36,51)),
                    new ItemLevelRange(  30, CraftingUtils.rangeOf(53,61)),
                    new ItemLevelRange(  60, CraftingUtils.rangeOf(63, 71)),
                    new ItemLevelRange(  81, CraftingUtils.rangeOf(73, 85)),
                    new ItemLevelRange(  86, CraftingUtils.rangeOf(87, 100)) ) ),

    SUFFIXES(new ModTableEntry(WeaponModifiers.STRENGTH,
                    new ItemLevelRange(  1, CraftingUtils.rangeOf(8,12)),
                    new ItemLevelRange(  11, CraftingUtils.rangeOf(13,17)),
                    new ItemLevelRange(  22, CraftingUtils.rangeOf(18,22)),
                    new ItemLevelRange(  33, CraftingUtils.rangeOf(23, 27)),
                    new ItemLevelRange(  44, CraftingUtils.rangeOf(28, 32)),
                    new ItemLevelRange(  55, CraftingUtils.rangeOf(33, 37)) ),
            new ModTableEntry(WeaponModifiers.DEXTERITY,
                    new ItemLevelRange(  1, CraftingUtils.rangeOf(8,12)),
                    new ItemLevelRange(  11, CraftingUtils.rangeOf(13,17)),
                    new ItemLevelRange(  22, CraftingUtils.rangeOf(18,22)),
                    new ItemLevelRange(  33, CraftingUtils.rangeOf(23, 27)),
                    new ItemLevelRange(  44, CraftingUtils.rangeOf(28, 32)),
                    new ItemLevelRange(  55, CraftingUtils.rangeOf(33, 37)) ),
            new ModTableEntry(WeaponModifiers.ACCURACY,
                    new ItemLevelRange(  1, CraftingUtils.rangeOf(80,130)),
                    new ItemLevelRange(  20, CraftingUtils.rangeOf(131,215)),
                    new ItemLevelRange(  40, CraftingUtils.rangeOf(216,325)),
                    new ItemLevelRange(  60, CraftingUtils.rangeOf(326, 455)),
                    new ItemLevelRange(  75, CraftingUtils.rangeOf(456, 624)) ),
            new ModTableEntry(WeaponModifiers.ATTACK_SPEED,
                    new ItemLevelRange(  1, CraftingUtils.rangeOf(5, 7)),
                    new ItemLevelRange(  11, CraftingUtils.rangeOf(8, 10)),
                    new ItemLevelRange(  22, CraftingUtils.rangeOf(11, 13)),
                    new ItemLevelRange(  30, CraftingUtils.rangeOf(14, 16)),
                    new ItemLevelRange(  37, CraftingUtils.rangeOf(17, 19)),
                    new ItemLevelRange(  45, CraftingUtils.rangeOf(20, 22)),
                    new ItemLevelRange(  60, CraftingUtils.rangeOf(23, 25)) ) );

    SwordMods(ModTableEntry... affixes){
        for (ModTableEntry affix : affixes){
            this.affixes.put(affix.getMod(), affix.getMappedRanges());

            this.modifierList.add(affix.getMod());
        }
    }
    private final Map<WeaponModifiers, Map<Integer, int[]>> affixes = new HashMap<>();
    private final List<WeaponModifiers> modifierList = new ArrayList<>();

    @Override
    public Map<Integer, int[]> getModTable(WeaponModifiers mod){
        return affixes.get(mod);
    }
    @Override
    public List<WeaponModifiers> getAllModNames(){
        return modifierList;
    }
}
