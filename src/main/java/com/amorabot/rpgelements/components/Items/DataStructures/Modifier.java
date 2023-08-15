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
    private int tier;
    private int[] value;

    public Modifier(Map<Mod, Map<Integer, int[]>> modPool, int itemLevel, List<Modifier<Mod>> itemModifierList){
        randomizeMod(modPool, itemLevel, itemModifierList);
    }

    public void randomizeMod(Map<Mod, Map<Integer, int[]>> modPool, int itemLevel, List<Modifier<Mod>> itemModifierList){
        List<Mod> availableMods = new ArrayList<>(modPool.keySet()); //All possible mods
        List<Mod> currentMods = new ArrayList<>(); //Already selected mods
        Affix modPoolType = availableMods.get(0).getAffixType();
        for (Modifier<Mod> mod : itemModifierList){
            Affix modType = mod.getModifier().getAffixType();
           if (modType == modPoolType){ //Only check for same-type mods
               currentMods.add(mod.getModifier());
           }
        }
        availableMods.removeAll(currentMods); //Currently available mods (All except selected for that category)
        List<Mod> blackListedMods = new ArrayList<>();
        for (Mod mod : availableMods){ //Checking for attempts to generate a unavailable modifier to given itemlevel
            Set<Integer> tierLevels = modPool.get(mod).keySet();
            if (!checkModAvailability(tierLevels, itemLevel)){
                blackListedMods.add(mod);
                Utils.log("Blacklisting mod: " + mod.toString() + ", Insufficient item level");
            }
        }
        availableMods.removeAll(blackListedMods);

        Mod selectedMod = availableMods.get(CraftingUtils.getRandomNumber(0, (availableMods.size()-1))); //Getting a random available mod
        int selectedTier = CraftingUtils.getRandomNumber(0, mapMaximumTier(new ArrayList<>(modPool.get(selectedMod).keySet()), itemLevel));
        int[] selectedValue = randomizeValue(selectedMod, selectedTier, modPool);

        setModifier(selectedMod);
        setTier(selectedTier);
        setValue(selectedValue);
    }
    private int mapMaximumTier(List<Integer> tierList, int itemLevel){
        if (tierList.contains(itemLevel)){
            return tierList.indexOf(itemLevel);
        } else {
            tierList.add(itemLevel);
        }
        tierList.sort(Comparator.naturalOrder());
        if (tierList.get(0) != itemLevel){ //If the item's level it greater than the minimum required, return its index in the list -1 (highest tier available)
            return tierList.indexOf(itemLevel) - 1;
        }
        return 0;
    }
    private boolean checkModAvailability(Set<Integer> tierValuesSet, int itemLevel){
        List<Integer> orderedTierList = new ArrayList<>(tierValuesSet);
        orderedTierList.sort(Comparator.naturalOrder());
        int initialTier = orderedTierList.get(0);
        return itemLevel >= initialTier;
    }
    public int getTier() {
        return tier;
    }
    public void setTier(int tier) {
        this.tier = tier;
    }
    public int[] getValue() {
        return value;
    }
    private void setValue(int[] value){
        this.value = value;
    }
    public int[] randomizeValue(Mod selectedMod, int selectedTier, Map<Mod, Map<Integer, int[]>> modPool){
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

    public Mod getModifier() {
        return modifier;
    }

    public void setModifier(Mod modifier) {
        this.modifier = modifier;
    }
}
