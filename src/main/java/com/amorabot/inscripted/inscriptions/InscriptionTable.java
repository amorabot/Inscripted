package com.amorabot.inscripted.inscriptions;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.modifiers.Inscription;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.data.ModifierData;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InscriptionTable {
    private static Map<Affix, Map<InscriptionID, Map<Integer, int[]>>> PROCEDURAL_VALUES;
    private static final Map<InscriptionID, Map<Integer, int[]>> IMPLICIT_VALUES = new HashMap<>();


    private final Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> itemInscriptions;

    public InscriptionTable(@NotNull String itemName){
        InscriptionTableDTO tableData = InscriptionDataManager.loadInscriptionTableDataFor(itemName);
        assert tableData != null;
        IMPLICIT_VALUES.putAll(tableData.implicits());

        this.itemInscriptions = buildInscriptionTable(tableData);
    }
    public InscriptionTable(@NotNull Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> affixData, Map<InscriptionID, Map<Integer, int[]>> implicitData){
        IMPLICIT_VALUES.putAll(implicitData);
        this.itemInscriptions = affixData;
    }


    public Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> getInscriptionsTable(){
        return this.itemInscriptions;
    }
    public static int[] getImplicitValuesArray(InscriptionID implicit, int tier){
        ModifierData modData = implicit.getData();
        if (!modData.getAffixType().equals(Affix.IMPLICIT)){return new int[1];}
        if (!IMPLICIT_VALUES.containsKey(implicit)){return new int[1];}
        return IMPLICIT_VALUES.get(implicit).get(tier).clone();
    }


    public Map<Integer, Integer> getTierMappingsFor(InscriptionID inscription){
        if (!getInscriptionsTable().get(inscription.getData().getAffixType()).containsKey(inscription)){return new HashMap<>();}
        return getInscriptionsTable().get(inscription.getData().getAffixType()).get(inscription);
    }

    //Returns -1 if mod is not available
    public int getHighestTierFor(InscriptionID inscription, int itemLevel){
        assert itemLevel>=1;
        ModifierData modData = inscription.getData();
        if (modData.isUnique() || modData.isKeystone() || modData.isUniqueEffect()){return -1;}
        Map<Integer, Integer> tierMappings = getTierMappingsFor(inscription);
        if (tierMappings.isEmpty()){
            Utils.log("Empty mapping for: " + inscription);
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

    public int getRandomTierFor(InscriptionID inscription, int itemLevel){
        final int minTier = 0;
        final int highestTier = getHighestTierFor(inscription,itemLevel);
        if (highestTier==-1){return -1;}
        return Utils.getRandomIntBetween(highestTier, minTier);
    }

    public Inscription getRandomInscription(Affix affixToGenerate, int itemLevel, Set<InscriptionID> blockedInscriptions){
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
        Map<InscriptionID, Map<Integer, Integer>> itemAffixTable = itemInscriptions.get(affixToGenerate);
        //Defining available affixes
        Set<InscriptionID> availableAffixes = new HashSet<>(itemAffixTable.keySet());
        availableAffixes.removeAll(blockedInscriptions);
        if (availableAffixes.isEmpty()){
            Utils.error("No available affixes to generate. (" + affixToGenerate + ")------("+affixToGenerate+")-----");
            return null;
        }
        //Getting a random inscription from the available set
        List<InscriptionID> availableInscriptionList = new ArrayList<>(availableAffixes);
        final int selectedIndex = Utils.getRandomIntBetween(0, availableInscriptionList.size()-1);
        InscriptionID selectedInscription = availableInscriptionList.get(selectedIndex);
        //Getting a tier for that inscription
        int selectedTier = getRandomTierFor(selectedInscription,itemLevel);
        if (selectedTier<0){
//            Utils.error("REJECTED: " + selectedInscription);
            blockedInscriptions.add(selectedInscription);
            return getRandomInscription(affixToGenerate, itemLevel, blockedInscriptions);
        }
//        Utils.error("SELECTED: " + selectedInscription);
        //Mutate the given set, so its updated and prevents selectedInscription from being generated again
        blockedInscriptions.add(selectedInscription);
        return new Inscription(selectedInscription, selectedTier, getHighestTierFor(selectedInscription,itemLevel));
    }


    private Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> buildInscriptionTable(InscriptionTableDTO tableData){
        Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> specificMods = tableData.specificMods();
        if (specificMods.isEmpty()){
            specificMods.put(Affix.PREFIX, new HashMap<>());
            specificMods.put(Affix.SUFFIX, new HashMap<>());
        } else {
            if (!specificMods.containsKey(Affix.PREFIX)){specificMods.put(Affix.PREFIX,new HashMap<>());}
            if (!specificMods.containsKey(Affix.SUFFIX)){specificMods.put(Affix.SUFFIX,new HashMap<>());}
        }


        String[] subtables = tableData.subtables();
        if (subtables == null){
            return specificMods;
        }
        for (String subtable : subtables){
            try {
                InscriptionSubtable mappedSubtable = InscriptionSubtable.valueOf(subtable);
                Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> subtableData = mappedSubtable.getSubtableData();

                mergeTable(specificMods, subtableData, Affix.PREFIX);
                mergeTable(specificMods, subtableData, Affix.SUFFIX);
            } catch (IllegalArgumentException e) {
                Utils.error("Invalid subtable: " + subtable);
                throw new RuntimeException(e);
            }
        }
        return specificMods;
    }
    //Mutates original table. To avoid this clone originalTable -> new HashMap<>(map);
    public void mergeTable(
            Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> originalTable,
            Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> mergedTable,
            Affix affixToMerge){
        Map<InscriptionID, Map<Integer, Integer>> mergedAffixTable = mergedTable.get(affixToMerge);
        /*
        For every entry on mergedTable, let's try to merge it into original table
        If a key from mergedTable is not already on originalTable, OR the value associated is null, the mergedTable value will be merged

        In case where there's conflict, the intended behavior is to give priority to already defined values (That establishes a hierarchy between tables)
        so the original values are kept
            - Priority would go that way: specificMods > 1st subtable > 2nd subtable > ...
        */
        mergedAffixTable.forEach(
                (inscription, values) -> originalTable.get(affixToMerge).merge(inscription, values, (originalValues, valuesToMerge) -> originalValues)
        );
    }

    public void debug(){
        StringBuilder debugString = new StringBuilder("\n");
        getInscriptionsTable().forEach(
                (affix, inscriptionTierMap) -> {
                    debugString.append(affix).append("============\n");
                    StringBuilder tierMappingsString = new StringBuilder();
                    inscriptionTierMap.forEach(
                            (inscription, tierMapping) -> {
                                tierMappingsString.append(inscription.toString()).append("\n");
                                tierMapping.forEach(
                                        (level, tier) -> tierMappingsString.append("Lv.:").append(level).append(" | ").append("Tier:").append(tier).append("\n")
                                );
                                tierMappingsString.append("\n");
                            }
                    );
                    debugString.append(tierMappingsString);
                }
        );
        Utils.log(debugString.toString());
    }






    public static int[] queryValuesFor(Inscription inscription){
        return queryValuesFor(inscription.getInscription(), inscription.getTier());
    }
    public static int[] queryValuesFor(InscriptionID mod, int tier){
        if (mod.getData().getAffixType().equals(Affix.IMPLICIT)){return getImplicitValuesArray(mod,tier);}
        return PROCEDURAL_VALUES.get(mod.getData().getAffixType()).get(mod).get(tier).clone();
    }

    public static void loadValues(){
        Map<Affix, Map<InscriptionID, Map<Integer, int[]>>> valuesTable = new HashMap<>();
        loadProceduralValues(valuesTable, Affix.PREFIX);
        loadProceduralValues(valuesTable, Affix.SUFFIX);
        InscriptionTable.PROCEDURAL_VALUES = valuesTable;
    }
    //Mutates the base map with all tables mixed together
    private static void loadProceduralValues(Map<Affix, Map<InscriptionID, Map<Integer, int[]>>> valuesTable, Affix affixTableToLoad){

        Map<InscriptionID, Map<Integer, int[]>> affixValuesMap = new HashMap<>();
        YamlConfiguration valuesTableConfig = InscriptionDataManager.readValuesTableData(affixTableToLoad.toString());
        assert valuesTableConfig != null;

        for (InscriptionID mod : InscriptionID.values()){
            if (!mod.getData().getAffixType().equals(affixTableToLoad)){continue;}
            affixValuesMap.put(mod, loadValuesFor(valuesTableConfig, mod));
        }
        valuesTable.put(affixTableToLoad, affixValuesMap);
    }
    private static Map<Integer, int[]> loadValuesFor(@NotNull YamlConfiguration valuesConfig, InscriptionID mod){
        Map<Integer,int[]> tempTierValueMapping = new HashMap<>();
        for (int i = 0; i < mod.getTotalTiers(); i++){
            int[] currValue = getTableValuesFor(valuesConfig, mod, i);
            tempTierValueMapping.put(i, currValue);
//            Utils.log("Loaded: " + mod + " " + Arrays.toString(currValue));
        }
        return tempTierValueMapping;
    }

    private static int[] getTableValuesFor(YamlConfiguration valuesConfig, InscriptionID mod, int tier){
        String modString = mod.toString();
        String path = modString+"."+tier;
        List<Integer> values = valuesConfig.getIntegerList(path);
        int[] valuesArray = new int[values.size()];
        for (int i = 0; i< valuesArray.length; i++){
            valuesArray[i] = values.get(i);
        }
//        Utils.log("Successfully fetched values for T" + tier + " " + mod + ": " + Arrays.toString(valuesArray));
        return valuesArray;
    }
}
