package com.amorabot.inscripted.item.inscription.table;

import com.amorabot.inscripted.file.item.InscriptionDataManager;
import com.amorabot.inscripted.file.item.RelicEditor;
import com.amorabot.inscripted.item.inscription.ProceduralInscription;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.language.AffixType;
import com.amorabot.inscripted.item.relic.RelicArmorData;
import com.amorabot.inscripted.item.relic.RelicWeaponData;
import com.amorabot.inscripted.item.relic.Relics;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InscriptionTable {
    // Raw Values for each individual PREFIX and SUFFIX tier
    private static Map<AffixType, Map<InscriptionIDs, Map<Integer, int[]>>> MAPPED_AFFIX_TIER_VALUES;
    private static final Map<InscriptionIDs, Map<Integer, int[]>> IMPLICIT_VALUES = new HashMap<>();
    //Tier-level mappings for an individual item type (AXE, SWORD, DEX_CHESTPLATE,...)
    private final Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> itemInscriptionMappings;

    // Via filename
    public InscriptionTable(@NotNull String itemName){
        InscriptionTableDTO tableData = InscriptionDataManager.loadInscriptionTableDataFor(itemName);
        assert tableData != null;
        IMPLICIT_VALUES.putAll(tableData.implicits());

        this.itemInscriptionMappings = buildInscriptionTable(tableData);
    }
    // Via premade affix mappings
    public InscriptionTable(@NotNull Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> affixData, Map<InscriptionIDs, Map<Integer, int[]>> implicitData){
        IMPLICIT_VALUES.putAll(implicitData);
        this.itemInscriptionMappings = affixData;
    }


    public Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> getItemInscriptionsClone(){
        return new HashMap<>(this.itemInscriptionMappings);
    }
    // Get the original reference to the item's inscriptions
    private Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> getInscriptionsTableReference(){
        return this.itemInscriptionMappings;
    }
    private static int[] getImplicitValuesArray(InscriptionIDs implicit, int tier){
        if (!implicit.getDefinitionData().getAffix().equals(AffixType.IMPLICIT)){return new int[1];}
        if (!IMPLICIT_VALUES.containsKey(implicit)){return new int[1];}
        return IMPLICIT_VALUES.get(implicit).get(tier).clone();
    }

    public Map<InscriptionIDs, Map<Integer, Integer>> getAffixMapFor(AffixType affix){
        if (!getInscriptionsTableReference().containsKey(affix)){return new HashMap<>();}
        return new HashMap<>(getInscriptionsTableReference().get(affix));
    }
    public Map<Integer, Integer> getTierMappingsFor(InscriptionIDs inscription){
        AffixType inscriptionAffix = inscription.getDefinitionData().getAffix();
        if (!getAffixMapFor(inscriptionAffix).containsKey(inscription)){return new HashMap<>();}
        return new HashMap<>(getInscriptionsTableReference().get(inscriptionAffix).get(inscription));
    }

    private Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> buildInscriptionTable(InscriptionTableDTO tableData){
        Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> specificMods = tableData.specificMods();
        if (specificMods.isEmpty()){
            specificMods.put(AffixType.PREFIX, new HashMap<>());
            specificMods.put(AffixType.SUFFIX, new HashMap<>());
        } else {
            if (!specificMods.containsKey(AffixType.PREFIX)){specificMods.put(AffixType.PREFIX,new HashMap<>());}
            if (!specificMods.containsKey(AffixType.SUFFIX)){specificMods.put(AffixType.SUFFIX,new HashMap<>());}
        }


        String[] subtables = tableData.subtables();
        if (subtables == null){
            return specificMods;
        }
        for (String subtable : subtables){
            try {
                InscriptionSubtable mappedSubtable = InscriptionSubtable.valueOf(subtable);
                Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> subtableData = new HashMap<>(mappedSubtable.getSubtableData());

                mergeTable(specificMods, subtableData, AffixType.PREFIX);
                mergeTable(specificMods, subtableData, AffixType.SUFFIX);
            } catch (IllegalArgumentException e) {
                Utils.error("Invalid subtable: " + subtable);
                throw new RuntimeException(e);
            }
        }
        return specificMods;
    }
    //Mutates original table. To avoid this clone originalTable when passing it as a parameter new HashMap<>(map);
    public void mergeTable(
            Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> originalTable,
            Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> mergedTable,
            AffixType affixToMerge){
        Map<InscriptionIDs, Map<Integer, Integer>> mergedAffixTable = mergedTable.get(affixToMerge);
        /*
        For every entry on mergedTable, let's try to merge it into original table
        If a key from mergedTable is not already on originalTable, OR the value associated is null, the mergedTable value will be merged

        In case where there's conflict, the intended behavior is to give priority to already defined values (That establishes a hierarchy between tables)
        so the original values are kept
            - Priority would go that way: specificMods > 1st subtable > 2nd subtable > ...
        */
        mergedAffixTable.forEach(
                // KV pair in that map, get a reference to the Key and the Value and to Lambda1
                (inscription, values) ->
                        // (Lambda1) -> get the original table (recieving the merged values) and override any inscriptions there with the merged ones,
                        // prioritizing merged values over the currently stored. The Lambda2 in .merge() reflects that.
                        originalTable.get(affixToMerge).merge(inscription, values, (originalValues, valuesToMerge)
                                -> originalValues)
        );
    }

    public void debug(){
        StringBuilder debugString = new StringBuilder("\n");
        getInscriptionsTableReference().forEach(
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






    public static int[] queryValuesFor(ProceduralInscription proceduralInscription){
        return queryValuesFor(proceduralInscription.getInscription(), proceduralInscription.getTier());
    }
    public static int[] queryValuesFor(InscriptionIDs mod, int tier){
        AffixType inscriptionAffix = mod.getDefinitionData().getAffix();
        if (inscriptionAffix.equals(AffixType.UNIQUE)){// Invalid
            return new int[1];
        }
        if (inscriptionAffix.equals(AffixType.IMPLICIT)){
            return getImplicitValuesArray(mod,tier);
        }
        int [] fetchedValue = MAPPED_AFFIX_TIER_VALUES.get(inscriptionAffix).get(mod).get(tier).clone();
        if (fetchedValue == null){
            Utils.error("Invalid inscription mapping. Possible causes:"+
                    "\nInscription not available on item table"+
                    "\nInvalid tier");
        }
        return fetchedValue;
    }

    public static void loadRawValues(){
        Map<AffixType, Map<InscriptionIDs, Map<Integer, int[]>>> valuesTable = new HashMap<>();
        loadProceduralValues(valuesTable, AffixType.PREFIX);
        loadProceduralValues(valuesTable, AffixType.SUFFIX);
        InscriptionTable.MAPPED_AFFIX_TIER_VALUES = valuesTable;
    }
    //Mutates the base map with all tables mixed together
    private static void loadProceduralValues(Map<AffixType, Map<InscriptionIDs, Map<Integer, int[]>>> valuesTable, AffixType affixTableToLoad){

        Map<InscriptionIDs, Map<Integer, int[]>> affixValuesMap = new HashMap<>();
        YamlConfiguration valuesTableConfig = InscriptionDataManager.readValuesTableData(affixTableToLoad.toString());
        assert valuesTableConfig != null;

        for (InscriptionIDs mod : InscriptionIDs.values()){
            if (!mod.getDefinitionData().getAffix().equals(affixTableToLoad)){continue;}
            affixValuesMap.put(mod, loadValuesFor(valuesTableConfig, mod));
        }
        valuesTable.put(affixTableToLoad, affixValuesMap);
    }
    private static Map<Integer, int[]> loadValuesFor(@NotNull YamlConfiguration valuesConfig, InscriptionIDs mod){
        Map<Integer,int[]> tempTierValueMapping = new HashMap<>();
        for (int i = 0; i < mod.getTiers(); i++){
            int[] currValue = getTableValuesFor(valuesConfig, mod, i);
            tempTierValueMapping.put(i, currValue);
        }
        return tempTierValueMapping;
    }

    private static int[] getTableValuesFor(YamlConfiguration valuesConfig, InscriptionIDs mod, int tier){
        String modString = mod.toString();
        String path = modString+"."+tier;
        List<Integer> values = valuesConfig.getIntegerList(path);
        int[] valuesArray = new int[values.size()];
        for (int i = 0; i< valuesArray.length; i++){
            valuesArray[i] = values.get(i);
        }
        return valuesArray;
    }
}
