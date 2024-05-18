package com.amorabot.inscripted.components.Items.DataStructures;

import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.utils.CraftingUtils;
import com.amorabot.inscripted.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModifierManager {
    public static Inscription selectAvailableMod(int ilvl, Map<InscriptionID, Map<Integer, Integer>> availableAffixPool, Set<InscriptionID> blockedMods){
        //Blocked mods -> generally mods that are already on the item, but can contain other purposefully blocked mods, if needed from the get-go
        //Blocked mods can also contain illegal mods for that item's item level
        List<InscriptionID> legalMods = new ArrayList<>(filterAvailableMods(availableAffixPool, blockedMods));
        if (legalMods.isEmpty()){
            Utils.log("No mods available");
            return null;
        }

        int randomModIndex = CraftingUtils.getRandomNumber(0, legalMods.size()-1);
        InscriptionID chosenMod = legalMods.get(randomModIndex);
        List<Integer> levelBrackets = new ArrayList<>(availableAffixPool.get(chosenMod).keySet());
        int highestTierAvailable = getHighestTier(levelBrackets, ilvl);
        //Once the max tier for that mod has been defined, lets set the definitive tier for that mod
        int chosenTierBracket = CraftingUtils.getRandomNumber(0, highestTierAvailable);

        //Now lets get the level bracket that corresponds to the chosen tier
        //And, after that, lets select the actual tier thats mapped to that bracket (may not be 1-1 mapping =>  tier 3 bracket might map to a tier 2 mod)
        levelBrackets.sort(Comparator.naturalOrder());
        int bracket = levelBrackets.get(chosenTierBracket);
        int finalTier = availableAffixPool.get(chosenMod).get(bracket);

        return new Inscription(chosenMod, finalTier, highestTierAvailable);
    }

    @NotNull
    private static Set<InscriptionID> filterAvailableMods(Map<InscriptionID, Map<Integer, Integer>> modPool, Set<InscriptionID> blockedMods) {
        Set<InscriptionID> availableMods = modPool.keySet(); //All possible mods/affixes
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
    public static Set<InscriptionID> checkForIllegalMods(Map<InscriptionID, Map<Integer, Integer>> availableAffixPool, int itemLevel){
        Set<InscriptionID> illegalMods = new HashSet<>();
        for (Map.Entry<InscriptionID, Map<Integer, Integer>> entry : availableAffixPool.entrySet()){
            if (isModLegalForItemLevel(new ArrayList<>(entry.getValue().keySet()), itemLevel)){
                continue;
            }
            illegalMods.add(entry.getKey());
        }
        return illegalMods;
    }
}
