package pluginstudies.pluginstudies.Crafting.Weapons.Modifiers;

import pluginstudies.pluginstudies.Crafting.Interfaces.AffixTableAcessInterface;
import pluginstudies.pluginstudies.utils.ItemLevelRange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pluginstudies.pluginstudies.Crafting.CraftingUtils.rangeOf;

public enum AxeMods implements AffixTableAcessInterface {

    PREFIXES(new ModTableEntry(WeaponModifiers.STAMINA,
                    new ItemLevelRange(  4, rangeOf(1,3)),
                    new ItemLevelRange(  15, rangeOf(4,7)),
                    new ItemLevelRange(  30, rangeOf(7,10)),
                    new ItemLevelRange(  60, rangeOf(11, 14)),
                    new ItemLevelRange(  81, rangeOf(15, 20)),
                    new ItemLevelRange(  86, rangeOf(21, 30)) ),
            new ModTableEntry(WeaponModifiers.ADDED_PHYSICAL,
                    new ItemLevelRange(  2, rangeOf(2,2, 4,5)),
                    new ItemLevelRange(  13, rangeOf(6,8,12,15)),
                    new ItemLevelRange(  21, rangeOf(10,13, 21,25)),
                    new ItemLevelRange(  29, rangeOf(13, 17, 28,32)),
                    new ItemLevelRange(  36, rangeOf(16, 22, 35, 40)),
                    new ItemLevelRange(  46, rangeOf(20, 28, 43, 51)) ),
            new ModTableEntry(WeaponModifiers.ADDED_FIRE,
                    new ItemLevelRange(  1, rangeOf(2,2, 4,5)),
                    new ItemLevelRange(  11, rangeOf(6,8,12,15)),
                    new ItemLevelRange(  18, rangeOf(10,13, 21,25)),
                    new ItemLevelRange(  26, rangeOf(13, 17, 28,32)),
                    new ItemLevelRange(  33, rangeOf(16, 22, 35, 40)),
                    new ItemLevelRange(  42, rangeOf(20, 28, 43, 51)) ),
            new ModTableEntry(WeaponModifiers.ADDED_ABYSSAL,
                    new ItemLevelRange(  83, rangeOf(98,149, 183,280)) ),
            new ModTableEntry(WeaponModifiers.PERCENT_ELEMENTAL,
                    new ItemLevelRange(  4, rangeOf(19,34)),
                    new ItemLevelRange(  15, rangeOf(36,51)),
                    new ItemLevelRange(  30, rangeOf(53,61)),
                    new ItemLevelRange(  60, rangeOf(63, 71)),
                    new ItemLevelRange(  81, rangeOf(73, 85)),
                    new ItemLevelRange(  86, rangeOf(87, 100)) ),
            new ModTableEntry(WeaponModifiers.PERCENT_PHYSICAL,
                    new ItemLevelRange(  4, rangeOf(19,34)),
                    new ItemLevelRange(  15, rangeOf(36,51)),
                    new ItemLevelRange(  30, rangeOf(53,61)),
                    new ItemLevelRange(  60, rangeOf(63, 71)),
                    new ItemLevelRange(  81, rangeOf(73, 85)),
                    new ItemLevelRange(  86, rangeOf(87, 100)) ) ),

    SUFFIXES(new ModTableEntry(WeaponModifiers.STRENGTH,
                    new ItemLevelRange(  1, rangeOf(8,12)),
                    new ItemLevelRange(  11, rangeOf(13,17)),
                    new ItemLevelRange(  22, rangeOf(18,22)),
                    new ItemLevelRange(  33, rangeOf(23, 27)),
                    new ItemLevelRange(  44, rangeOf(28, 32)),
                    new ItemLevelRange(  55, rangeOf(33, 37)) ),
            new ModTableEntry(WeaponModifiers.DEXTERITY,
                    new ItemLevelRange(  1, rangeOf(8,12)),
                    new ItemLevelRange(  11, rangeOf(13,17)),
                    new ItemLevelRange(  22, rangeOf(18,22)),
                    new ItemLevelRange(  33, rangeOf(23, 27)),
                    new ItemLevelRange(  44, rangeOf(28, 32)),
                    new ItemLevelRange(  55, rangeOf(33, 37)) ),
            new ModTableEntry(WeaponModifiers.ACCURACY,
                    new ItemLevelRange(  1, rangeOf(80,130)),
                    new ItemLevelRange(  20, rangeOf(131,215)),
                    new ItemLevelRange(  40, rangeOf(216,325)),
                    new ItemLevelRange(  60, rangeOf(326, 455)),
                    new ItemLevelRange(  75, rangeOf(456, 624)) ),
            new ModTableEntry(WeaponModifiers.ATTACK_SPEED,
                    new ItemLevelRange(  1, rangeOf(5, 7)),
                    new ItemLevelRange(  11, rangeOf(8, 10)),
                    new ItemLevelRange(  22, rangeOf(11, 13)),
                    new ItemLevelRange(  30, rangeOf(14, 16)),
                    new ItemLevelRange(  37, rangeOf(17, 19)),
                    new ItemLevelRange(  45, rangeOf(20, 22)),
                    new ItemLevelRange(  60, rangeOf(23, 25)) ) );

    private final Map<WeaponModifiers, Map<Integer, int[]>> affixes = new HashMap<>();
    private final List<WeaponModifiers> modifierList = new ArrayList<>();

    AxeMods(ModTableEntry... affixes){
        for (ModTableEntry affix : affixes){
            this.affixes.put(affix.getMod(), affix.getMappedRanges());

            this.modifierList.add(affix.getMod());
        }
    }

    @Override
    public Map<Integer, int[]> getModTable(WeaponModifiers mod){
        return affixes.get(mod);
    }
    @Override
    public List<WeaponModifiers> getAllModNames(){
        return modifierList;
    }
}
