package com.amorabot.rpgelements.components.Items.DataStructures;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.rpgelements.components.Items.Interfaces.ItemModifier;
import com.amorabot.rpgelements.utils.CraftingUtils;
import com.amorabot.rpgelements.utils.Utils;

import java.io.Serializable;
import java.util.*;

public class Modifier <Mod extends Enum<Mod> & ItemModifier> implements Serializable {

    private Mod modifier;
    private byte tier; //0-maxTier
    private int[] value;
    private byte basePercentile; //0-100
    private boolean imbued = false; //mods are not fractured by default, but can be altered


    public Modifier(){

    }

    public static <M extends Enum<M> & ItemModifier> Modifier<M> getRandomModifier(Map<M, Map<Integer, int[]>> modPool, int itemLevel, List<Modifier<M>> itemModifierList){
        List<M> availableMods = new ArrayList<>(modPool.keySet()); //All possible mods
        List<M> currentMods = new ArrayList<>(); //Already selected mods
        Affix modPoolType = availableMods.get(0).getAffixType();
        for (Modifier<M> mod : itemModifierList){
            Affix modType = mod.getModifier().getAffixType();
            if (modType == modPoolType){ //Only check for same-type mods
                currentMods.add(mod.getModifier());
            }
        }
        availableMods.removeAll(currentMods); //Currently available mods (All except selected for that category)
        List<M> blackListedMods = new ArrayList<>();
        for (M mod : availableMods){ //Checking for attempts to generate a unavailable modifier to given itemlevel
            Set<Integer> tierLevels = modPool.get(mod).keySet();
            if (!checkModAvailability(tierLevels, itemLevel)){
                blackListedMods.add(mod);
                Utils.log("Blacklisting mod: " + mod.toString() + ", Insufficient item level");
            }
        }
        availableMods.removeAll(blackListedMods);

        M selectedMod = availableMods.get(CraftingUtils.getRandomNumber(0, (availableMods.size()-1))); //Getting a random available mod
        byte tierCap = getMaximumAllowedTier(new ArrayList<>(modPool.get(selectedMod).keySet()), itemLevel);
        byte selectedTier = (byte) CraftingUtils.getRandomNumber(0, tierCap);
        int[] selectedValue = randomizeValue(selectedMod, selectedTier, modPool);

        Modifier<M> mod = new Modifier<>();
        mod.setModifier(selectedMod);
        mod.setTier(selectedTier);
        mod.setValue(selectedValue);
        byte basePercentile;
        if (tierCap == 0){
            basePercentile = 100;
        } else {
            basePercentile = (byte) (100*(selectedTier/(float) tierCap));
        }
        mod.setBasePercentile(basePercentile);
        return mod;
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
    public static <M extends Enum<M> & ItemModifier> int[] randomizeValue(M selectedMod, int selectedTier, Map<M, Map<Integer, int[]>> modPool){
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
    public Mod getModifier() {
        return modifier;
    }
    public void setModifier(Mod modifier) {
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
    public <ModClass extends Enum<ModClass> & ItemModifier> Modifier<ModClass> castTo(Class<ModClass> modifierClass){
        //Modifiers may come from a variety of sources, and must be casted to reflect this.
        Modifier<ModClass> castedMod = new Modifier<>();
        castedMod.setModifier(modifierClass.cast(this.modifier));
        castedMod.setTier(this.tier);
        castedMod.setValue(this.value);
        castedMod.setBasePercentile(this.basePercentile);
        return castedMod;
    }
}
