package com.amorabot.inscripted.components.Items.DataStructures;

import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModifierManager {
    public static Modifier selectAvailableMod(int ilvl, Map<ModifierIDs, Map<Integer, Integer>> availableAffixPool, Set<ModifierIDs> blockedMods){
        //Blocked mods -> generally mods that are already on the item, but can contain other purposefully blocked mods, if needed from the get-go
        //Blocked mods can also contain illegal mods for that item's item level
        List<ModifierIDs> legalMods = new ArrayList<>(filterAvailableMods(availableAffixPool, blockedMods));
        if (legalMods.isEmpty()){
            Utils.log("No mods available");
            return null;
        }

        int randomModIndex = CraftingUtils.getRandomNumber(0, legalMods.size()-1);
        ModifierIDs chosenMod = legalMods.get(randomModIndex);
        List<Integer> levelBrackets = new ArrayList<>(availableAffixPool.get(chosenMod).keySet());
        int highestTierAvailable = getHighestTier(levelBrackets, ilvl);
        //Once the max tier for that mod has been defined, lets set the definitive tier for that mod
        int chosenTierBracket = CraftingUtils.getRandomNumber(0, highestTierAvailable);

        //Now lets get the level bracket that corresponds to the chosen tier
        //And, after that, lets select the actual tier thats mapped to that bracket (may not be 1-1 mapping =>  tier 3 bracket might map to a tier 2 mod)
        levelBrackets.sort(Comparator.naturalOrder());
        int bracket = levelBrackets.get(chosenTierBracket);
        int finalTier = availableAffixPool.get(chosenMod).get(bracket);

        return new Modifier(chosenMod, finalTier, highestTierAvailable);
    }

    @NotNull
    private static Set<ModifierIDs> filterAvailableMods(Map<ModifierIDs, Map<Integer, Integer>> modPool, Set<ModifierIDs> blockedMods) {
        Set<ModifierIDs> availableMods = modPool.keySet(); //All possible mods/affixes
        availableMods.removeAll(blockedMods);
        return availableMods;
    }

    //Returns the highest legal tier of a modifier that item can generate, given its item level (0-maxTiers) or 0 in case its not valid
    private static int getHighestTier(List<Integer> tierLevelBrackets, int itemLevel){
        if (tierLevelBrackets.contains(itemLevel)){
            return tierLevelBrackets.indexOf(itemLevel);
        } else {
            tierLevelBrackets.add(itemLevel);
        }
        tierLevelBrackets.sort(Comparator.naturalOrder());
        if (tierLevelBrackets.get(0) != itemLevel){ //If the item's level it greater than the minimum required, return its index in the list -1 (highest tier available)
            return (tierLevelBrackets.indexOf(itemLevel) - 1);
        }
        return 0;
    }
    private static boolean isModLegalForItemLevel(List<Integer> tierLevelBrackets, int itemLevel){
        tierLevelBrackets.sort(Comparator.naturalOrder());
        int initialTierMinimumLevel = tierLevelBrackets.get(0);
        return itemLevel >= initialTierMinimumLevel;
    }
    public static Set<ModifierIDs> checkForIllegalMods(Map<ModifierIDs, Map<Integer, Integer>> availableAffixPool, int itemLevel){
        Set<ModifierIDs> illegalMods = new HashSet<>();
        for (Map.Entry<ModifierIDs, Map<Integer, Integer>> entry : availableAffixPool.entrySet()){
            if (isModLegalForItemLevel(new ArrayList<>(entry.getValue().keySet()), itemLevel)){
                continue;
            }
            illegalMods.add(entry.getKey());
        }
        return illegalMods;
    }

    public static int[] getMappedFinalValueFor(Modifier mod){
        int[] values = ModifierIDs.getModifierValuesFor(mod).clone();
        int[] mappedValues;
        switch (mod.getModifierID().getRangeType()){
            case SINGLE_VALUE -> {
                Utils.log("Single values not treated yet");
                mappedValues = values;
            }
            case SINGLE_RANGE -> mappedValues = new int[]{Utils.getRoundedParametricValue(values[0], values[1], mod.getBasePercentile())};
            case DOUBLE_RANGE -> mappedValues = new int[]{
                    Utils.getRoundedParametricValue(values[0], values[1], mod.getBasePercentile()),
                    Utils.getRoundedParametricValue(values[2], values[3], mod.getBasePercentile())
            };
            default -> {
                Utils.log("Unable to properly map: " + mod.getModifierID() + ", setting values to empty array");
                mappedValues = new int[2];
            }
        }
        return mappedValues;
    }
}
