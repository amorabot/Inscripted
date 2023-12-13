package com.amorabot.inscripted.components.Items.DataStructures;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

public class Modifier implements Serializable {


    private ModifierIDs modifier;
    private byte tier; //0-maxTier
    private int[] value;
    private byte basePercentile; //0-100
    private boolean imbued = false; //mods are not fractured/imbued by default, but can be altered

    private Modifier(ModifierIDs mod, byte tier, int[] values, byte basePercentile){
        this.modifier = mod;
        this.tier = (byte) Math.min(tier, mod.getNumberOfTiers());
        this.value = values;
        this.basePercentile = basePercentile;
    }

    /*
    * @param modPool: The affix section referenced for the mod generation (Prefixes for axes, Suffixes for daggers...)
    * @param itemLevel: The base level of the item this mod is being generated for
    * @param currentItemMods: The modifier list that is already present in the item. (Preventing duplicates + X mod can block Y mod)
    * */
    public static Modifier getRandomModifier(Map<ModifierIDs, Map<Integer, int[]>> modPool, int itemLevel, List<Modifier> currentItemMods){
        //If modPool is, for instance, a prefix pool, lets check if the item already has any prefixes so it's removed (Prevents duplicates)

        Set<ModifierIDs> availableMods = filterAvailableMods(modPool, currentItemMods);

        Set<ModifierIDs> blackListedMods = new HashSet<>();
        //Filter mods with incompatible item level /or blocked mods
        for (ModifierIDs mod : availableMods){
            Set<Integer> tierLevels = modPool.get(mod).keySet();
            if (!checkModAvailability(tierLevels, itemLevel)){
                blackListedMods.add(mod);
                Utils.log("Insufficent item level for: " + mod.toString());
            }
        }
        availableMods.removeAll(blackListedMods);
        if (availableMods.isEmpty()){
            Utils.log("No mods available");
            return null;
        }
        List<ModifierIDs> availableModList = new ArrayList<>(availableMods);
        ModifierIDs selectedMod = availableModList.get(CraftingUtils.getRandomNumber(0, (availableModList.size()-1))); //Getting a random available mod
        byte tierCap = getMaximumAllowedTier(new ArrayList<>(modPool.get(selectedMod).keySet()), itemLevel);
        byte selectedTier = (byte) CraftingUtils.getRandomNumber(0, tierCap);
        int[] selectedValue = randomizeValue(selectedMod, selectedTier, modPool);

        byte basePercentile;
        if (tierCap == 0){
            basePercentile = 100;
        } else {
            basePercentile = (byte) (100*(selectedTier/(float) tierCap));
        }
        return new Modifier(selectedMod, selectedTier, selectedValue, basePercentile);
    }

    @NotNull
    private static Set<ModifierIDs> filterAvailableMods(Map<ModifierIDs, Map<Integer, int[]>> modPool, List<Modifier> currentItemMods) {
        Set<ModifierIDs> availableMods = modPool.keySet(); //All possible mods/affixes
        for (Modifier mod : currentItemMods){ //If the item alredy has a mod from this modPool, remove it from available mods
            availableMods.remove(mod.getModifierID());
        }
        return availableMods;
    }
    private static byte getMaximumAllowedTier(List<Integer> tierList, int itemLevel){
        if (tierList.contains(itemLevel)){
            return (byte) tierList.indexOf(itemLevel);
        } else {
            tierList.add(itemLevel);
        }
        tierList.sort(Comparator.naturalOrder());
        if (tierList.get(0) != itemLevel){ //If the item's level it greater than the minimum required, return its index in the list -1 (highest tier available)
            return (byte) (tierList.indexOf(itemLevel) - 1);
        }
        return 0;
    }
    private static boolean checkModAvailability(Set<Integer> tierValuesSet, int itemLevel){
        List<Integer> orderedTierList = new ArrayList<>(tierValuesSet);
        orderedTierList.sort(Comparator.naturalOrder());
        int initialTier = orderedTierList.get(0);
        return itemLevel >= initialTier;
    }
    public static int[] randomizeValue(ModifierIDs selectedMod, int selectedTier, Map<ModifierIDs, Map<Integer, int[]>> modPool){
        List<Integer> sortedTierList = new ArrayList<>(modPool.get(selectedMod).keySet());
        sortedTierList.sort(Comparator.naturalOrder());
        int tierIlvl = sortedTierList.get(selectedTier);
        int[] value = modPool.get(selectedMod).get(tierIlvl);
        RangeTypes rangeType = selectedMod.getRangeType();
        switch (rangeType){
            case SINGLE_VALUE -> {
                return value;
            }
            case SINGLE_RANGE -> {
                return new int[]{CraftingUtils.getRandomNumber(value[0], value[1])};
            }
            case DOUBLE_RANGE -> {
                int value1 = CraftingUtils.getRandomNumber(value[0], value[1]);
                int value2 = CraftingUtils.getRandomNumber(value[2], value[3]);
                return new int[]{value1, value2};
            }
            default -> {
                Utils.log("Error randomizing values for: " + selectedMod);
                return new int[]{-1};
            }
        }
    }
    public byte getTier() {
        return tier;
    }
    public void setTier(byte tier) {
        this.tier = tier;
    }
    public int[] getValue() {
        return value;
    }
    private void setValue(int[] value){
        this.value = value;
    }
    public ModifierIDs getModifierID() {
        return modifier;
    }
    public void setModifier(ModifierIDs modifier) {
        this.modifier = modifier;
    }
    public void setBasePercentile(byte basePercentile) {
        this.basePercentile = basePercentile;
    }
    public byte getBasePercentile() {
        return this.basePercentile;
    }
    public void imbue(){
        this.imbued = true;
    }

    public int getModifierOrdinal(){
        return getModifierID().ordinal();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modifier comparedMod = (Modifier) o;
        return modifier == comparedMod.modifier;
    }
    @Override
    public int hashCode() {
        return Objects.hash(modifier);
    }

    public String getModifierDisplayName(String valuesColor, int indent) {
        String modCategoryRune;
        switch (this.getModifierID().getAffixType()){
            case PREFIX -> modCategoryRune = "ᚴ";
            case SUFFIX -> modCategoryRune = "ᚭ";
            case UNIQUE -> modCategoryRune = "ᛟ";
            default -> modCategoryRune = "0";
        }
        String modifierDisplayName = "&7" + this.getModifierID().getDisplayName() + " &8" + modCategoryRune + " " + mapRomanicTierValue() + " ".repeat(indent);
        switch (this.getModifierID().getRangeType()){
            case SINGLE_VALUE -> {} //Utils.getPercentString(dodge)
            case SINGLE_RANGE -> modifierDisplayName = (modifierDisplayName
                    .replace("@value1@", valuesColor + this.getValue()[0]+"&7")).indent(indent);
            case DOUBLE_RANGE -> {
                int[] values = this.getValue();
                modifierDisplayName = (modifierDisplayName
                        .replace("@value1@", valuesColor+values[0]+"&7")
                        .replace("@value2@", valuesColor+values[1]+"&7")).indent(indent);
            }
        }
        return modifierDisplayName;
    }
    private String mapRomanicTierValue(){
        //Todo: more robust mapping
        return switch (this.tier) {
            case 0 -> "∅";
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            case 11 -> "XI";
            case 12 -> "XII";
            default -> "*";
        };
    }
}
