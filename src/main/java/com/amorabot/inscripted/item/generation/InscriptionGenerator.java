package com.amorabot.inscripted.item.generation;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.language.AffixType;
import com.amorabot.inscripted.item.inscription.table.InscriptionTable;
import com.amorabot.inscripted.item.render.InscriptionRenderer;
import com.amorabot.inscripted.item.render.ItemRenderer;
import com.amorabot.inscripted.item.structure.Item;
import com.amorabot.inscripted.item.structure.ItemRarities;
import com.amorabot.inscripted.math.MathUtils;
import com.amorabot.inscripted.utils.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InscriptionGenerator {


    public static List<Inscription> generateInscriptionSetFor(Item item){
        Set<InscriptionIDs> blockedInscriptions = new HashSet<>();
        InscriptionTable itemInscriptionsTable = item.getGenericSubtype().getTableData();
        assert itemInscriptionsTable != null;
        if (item.isCorrupted()){
            Utils.error("Unable to generate new inscriptions for Corrupted Item");
            return new ArrayList<>();
        }
        switch (item.getRarity()){
            case AUGMENTED -> {return generateMagicSet(item, itemInscriptionsTable, blockedInscriptions);}
            case RUNIC -> {return generateRunicSet(item, itemInscriptionsTable, blockedInscriptions);}
            // Relic sets are generated in a separate method
            default -> {return new ArrayList<>();}
        }
    }
    public static List<Inscription> generateMagicSet(Item itemData, InscriptionTable table, Set<InscriptionIDs> blockedInscriptions){
        List<Inscription> inscriptions = itemData.getInscriptions();
        final int maxAffixes = ItemRarities.AUGMENTED.getMaxAffixes();
        List<Inscription> fixedInscr = inscriptions.stream().filter(inscription -> !inscription.isModifiable()).toList();
        final int inscriptionsToGenerate = MathUtils.getRandomNumber(1, maxAffixes - fixedInscr.size());
        // Clear original set
        inscriptions.clear();
        // Re-Add unmodifiable inscriptions & block them
        fixedInscr.forEach(
                fixedInscription -> {
                    inscriptions.add(fixedInscription);
                    blockedInscriptions.add(fixedInscription.getInscription());
                }
        );
        for (int i = 0; i < inscriptionsToGenerate; i++) {
            if (MathUtils.fiftyFifty()){ // Prefix
                inscriptions.add(getRandomInscription(table,AffixType.PREFIX,itemData.getIlvl(),blockedInscriptions));
                continue;
            }
            // Suffix
            inscriptions.add(getRandomInscription(table,AffixType.SUFFIX,itemData.getIlvl(),blockedInscriptions));
        }
        inscriptions.sort(InscriptionRenderer.SORTER);
        return inscriptions;
    }
    public static List<Inscription> generateRunicSet(Item itemData, InscriptionTable table, Set<InscriptionIDs> blockedInscriptions){
        List<Inscription> inscriptions = itemData.getInscriptions();
        final int maxAffixes = ItemRarities.RUNIC.getMaxAffixes();
        AtomicInteger prefixes = new AtomicInteger();
        AtomicInteger suffixes = new AtomicInteger();

        List<Inscription> fixedInscr = inscriptions.stream().filter(inscription -> !inscription.isModifiable()).toList();
        final int inscriptionsToGenerate = MathUtils.getRandomNumber(3, maxAffixes - fixedInscr.size());
        // Clear original set
        inscriptions.clear();
        // Re-Add unmodifiable inscriptions, block them and update affix count
        fixedInscr.forEach(
                fixedInscription -> {
                    inscriptions.add(fixedInscription);
                    blockedInscriptions.add(fixedInscription.getInscription());
                    AffixType fixedAffixType = fixedInscription.getInscription().getDefinitionData().getAffix();
                    if (fixedAffixType.equals(AffixType.PREFIX)){
                        prefixes.getAndIncrement();
                    }
                    if (fixedAffixType.equals(AffixType.SUFFIX)){
                        suffixes.getAndIncrement();
                    }
                }
        );

        for (int i = 0; i < inscriptionsToGenerate; i++) {
            boolean generatePrefix = MathUtils.fiftyFifty();
            boolean openPrefix = prefixes.intValue()<3;
            boolean openSuffix = suffixes.intValue()<3;
            
            if (generatePrefix){
                if (openPrefix){
                    prefixes.getAndIncrement();
                    inscriptions.add(getRandomInscription(table,AffixType.PREFIX,itemData.getIlvl(),blockedInscriptions));
                } else {
                    suffixes.getAndIncrement();
                    inscriptions.add(getRandomInscription(table,AffixType.SUFFIX,itemData.getIlvl(),blockedInscriptions));
                }
                continue;
            }
            // Generate a suffix
            if (openSuffix){
                suffixes.getAndIncrement();
                inscriptions.add(getRandomInscription(table,AffixType.SUFFIX,itemData.getIlvl(),blockedInscriptions));
                continue;
            }
            prefixes.getAndIncrement();
            inscriptions.add(getRandomInscription(table,AffixType.PREFIX,itemData.getIlvl(),blockedInscriptions));
        }
        inscriptions.sort(InscriptionRenderer.SORTER);
        return inscriptions;
    }


    //Returns -1 if mod is not available
    public static int getHighestTierFor(InscriptionIDs inscription, int itemLevel, Map<Integer, Integer> tierMappings){
        assert itemLevel>=1;
        if (inscription.getDefinitionData().getAffix().equals(AffixType.UNIQUE) || inscription.isKeystone() || inscription.isEffect()){return -1;}
        if (tierMappings.isEmpty()){
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
        return new Inscription(selectedInscription, selectedTier, Utils.getNormalizedValue());
    }
}
