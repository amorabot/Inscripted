package com.amorabot.inscripted.item.generation;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.language.AffixType;
import com.amorabot.inscripted.item.inscription.table.InscriptionTable;
import com.amorabot.inscripted.utils.Utils;

import java.util.*;

public class InscriptionGenerator {

    //Returns -1 if mod is not available
    public static int getHighestTierFor(InscriptionIDs inscription, int itemLevel, Map<Integer, Integer> tierMappings){
        assert itemLevel>=1;
        if (inscription.getDefinitionData().getAffix().equals(AffixType.UNIQUE) || inscription.isKeystone() || inscription.isEffect()){return -1;}
        if (tierMappings.isEmpty()){
//            Utils.log("Empty mapping for: " + inscription);
            return -1;
        }

        List<Integer> levelBrackets = new ArrayList<>(tierMappings.keySet());
        levelBrackets.sort(Comparator.naturalOrder());

        if (levelBrackets.get(0)>itemLevel){return -1;}

        int highestTier = 0;
        for (int tierLevel : levelBrackets){
            if (tierLevel <= itemLevel){
                highestTier = tierMappings.get(tierLevel);
                continue;
            }
            //If a tierLevel higher than the itemLevel is found, the highest tier available has been found
            break;
        }
        return highestTier;
    }

    public static int getRandomTierFor(InscriptionIDs inscription, int itemLevel, Map<Integer, Integer> tierMappings){
        final int minTier = 0;
        final int highestTier = getHighestTierFor(inscription,itemLevel,tierMappings);
        if (highestTier==-1){return -1;}
        return Utils.getRandomIntBetween(highestTier, minTier);
    }

    public static Inscription getRandomInscription(InscriptionTable itemInscriptionTable,
                                            AffixType affixToGenerate, int itemLevel, Set<InscriptionIDs> blockedInscriptions){
        /* PSEUDOCODE
        Get affix table from instance data
        get the set containing all inscriptions of that affix type
        remove all blocked inscriptions from the original set
        if (set is empty, return null)
        get a random inscription from the remaining set
        get a random tier for that inscription at given item level
              if the tier is -1, that means that mod is unavailable, add that inscription to the blocked set and make a recursive call
                  return getRandomInscription(affix, ilvl, updatedBlockedInscriptions)
              else (its a valid attempt), return the resulting inscription object
        */
        //Getting affix table
        Map<InscriptionIDs, Map<Integer, Integer>> itemAffixTable = itemInscriptionTable.getAffixMapFor(affixToGenerate);
        //Defining available affixes
        Set<InscriptionIDs> availableAffixes = new HashSet<>(itemAffixTable.keySet());
        availableAffixes.removeAll(blockedInscriptions);
        if (availableAffixes.isEmpty()){
            Utils.error("No available affixes to generate. (" + affixToGenerate + ")");
            return null;
        }
        //Getting a random inscription from the available set
        List<InscriptionIDs> availableInscriptionList = new ArrayList<>(availableAffixes);
        final int selectedIndex = Utils.getRandomIntBetween(0, availableInscriptionList.size()-1);
        InscriptionIDs selectedInscription = availableInscriptionList.get(selectedIndex);
        // Getting all tier mappings
        Map<Integer, Integer> tierMappings = itemInscriptionTable.getTierMappingsFor(selectedInscription);
        //Getting a tier for that inscription
        int selectedTier = getRandomTierFor(selectedInscription,itemLevel, tierMappings);
        if (selectedTier<0){
            blockedInscriptions.add(selectedInscription);
            return getRandomInscription(itemInscriptionTable,affixToGenerate, itemLevel, blockedInscriptions);
        }
        //Mutate the given set, so its updated and prevents selectedInscription from being generated again
        blockedInscriptions.add(selectedInscription);
        return new Inscription(selectedInscription, selectedTier, getHighestTierFor(selectedInscription,itemLevel,tierMappings));
    }
}
