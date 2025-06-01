package com.amorabot.inscripted.profile.parsing;

import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.util.*;

@Getter
public class StatPool {
    private static final boolean DEBUG_MODE = false;

    private final Map<Stats, Map<ValueType, int[]>> baseStats;
    private final Map<Stats, Double> multipliers;

    public StatPool (Map<Stats, Map<ValueType, int[]>> baseStats, Map<Stats, Double> multipliers){
        this.baseStats = baseStats;
        this.multipliers = multipliers;
    }
    public StatPool(){
        this. baseStats = new HashMap<>();
        this.multipliers = new HashMap<>();
    }
    public StatPool snapshot(){
        return new StatPool(new HashMap<>(baseStats),new HashMap<>(multipliers));
    }
    public static StatPool getItemStats(Inscription implicit, List<Inscription> itemInscriptions, Map<Stats, int[]> localStats, Set<Integer> alreadyCompiledIDs){
        StatPool globalItemStatPool = new StatPool();
        globalItemStatPool.addInscriptionStats(implicit,alreadyCompiledIDs);
        for (Inscription inscription : itemInscriptions){
            globalItemStatPool.addInscriptionStats(inscription,alreadyCompiledIDs);
        }
        globalItemStatPool.applyLocalStats(localStats);
        return globalItemStatPool;
    }



    public void addInscriptionStats(Inscription inscription, Set<Integer> blockedIDs){
        if (inscription.isSpecial()){return;} // Special inscriptions aren't compiled here
        InscriptionIDs inscriptionID = inscription.getInscription();
        InscriptionDefinition definitionData = inscriptionID.getDefinitionData();
        if (definitionData instanceof InscriptionDefinition.Regular regularDef){
            InscriptionDefinition.BaseInscription regularBaseInsc = regularDef.getBaseData();
            int definitionID = regularBaseInsc.id(regularDef.isGlobal(), regularDef.isPositive());
            if (!blockedIDs.contains(definitionID)){
                if (DEBUG_MODE){Utils.log("Parsing ID " + definitionID);}
                insertValue(regularBaseInsc.stat(),regularBaseInsc.type(),inscription.getMappedFinalValues());
            }
            return;
        }
        if (definitionData instanceof InscriptionDefinition.Hybrid hybridDef){
            InscriptionDefinition.BaseInscription primaryData = hybridDef.getPrimaryData();
            int primaryID = primaryData.id(hybridDef.isGlobal(), hybridDef.isPositive());
            int[] mappedHybridValues = inscription.getMappedFinalValues();

            if (!blockedIDs.contains(primaryID)){ // Parse the primary value
                if (DEBUG_MODE) {Utils.log("Parsing Primary ID for "+inscriptionID.name()+ ": " + primaryID);}
                insertValue(primaryData.stat(),primaryData.type(),hybridDef.getPrimaryValues(mappedHybridValues));
            }

            InscriptionDefinition.BaseInscription secondaryData = hybridDef.getSecondaryData();
            int secondaryID = secondaryData.id(hybridDef.isGlobal(), hybridDef.isPositive());
            if (!blockedIDs.contains(primaryID)){
                if (DEBUG_MODE) {Utils.log("Parsing Secondary ID for "+inscriptionID.name()+ ": " + secondaryID);}
                insertValue(secondaryData.stat(),secondaryData.type(),hybridDef.getSecondaryValues(mappedHybridValues));
            }
            return;
        }
    }

    private void insertValue(Stats stat,ValueType type,int[] values){
        if (type.equals(ValueType.MULTIPLIER)){
            double storedMulti = multipliers.getOrDefault(stat,1D);
            double newMulti = ( 100 + values[0] ) / 100D;
            double finalMulti = storedMulti * newMulti;
            multipliers.put(stat, finalMulti);
            if (DEBUG_MODE){
                Utils.log("Multiplied " + stat + " by " + values[0] + "%" +
                        "\nFinal Multi.: " + finalMulti);
            }
            return;
        }
        if (!baseStats.containsKey(stat)){
            Map<ValueType, int[]> newValueMapping = new HashMap<>();
            newValueMapping.put(type,values);
            baseStats.put(stat,newValueMapping);
            if (DEBUG_MODE){
                Utils.log("Added " + stat.name() + ": " + type.name() + " " + Arrays.toString(values));
            }
            return;
        }
        // It contains that stat
        Map<ValueType, int[]> valueMapping = baseStats.get(stat);
        if (!valueMapping.containsKey(type)){
            valueMapping.put(type,values);
            if (DEBUG_MODE){
                Utils.log("Added "+type.name()+" value for " + stat.name() + " " + Arrays.toString(values));
            }
            return;
        }
        // Add the new value to the existing one
        int[] existingValues = valueMapping.getOrDefault(type,new int[values.length]);
        valueMapping.put(type, Utils.vectorSum(existingValues,values));
        if (DEBUG_MODE){
            Utils.log("Added to existing stat: " + stat.name() + " -> " + Arrays.toString(values));
        }
    }


    public void applyLocalStats(Map<Stats,int[]> localStats){
        localStats.forEach(
                (stat, values) -> {
                    if (DEBUG_MODE){Utils.log("Adding Flat " + stat + ": " + Arrays.toString(values) + " to StatPool");}
                    insertValue(stat,ValueType.FLAT,values);
                }
        );
    }
    public void mergeBaseStats(Map<Stats, Map<ValueType, int[]>> externalStats){
        externalStats.forEach(
                (stat, valueTypeMap) -> {
                    valueTypeMap.forEach(
                            (valueType, values) -> {
                                if (DEBUG_MODE){
                                    Utils.log("Merging exteral stat pool stat:" +
                                            "\n"+stat.name()+"-> "+valueType.name()+" "+Arrays.toString(values));
                                }
                                insertValue(stat,valueType,values);
                            }
                    );
                }
        );
    }
    public void mergeMultipliers(Map<Stats, Double> externalMultipliers){
        externalMultipliers.forEach(
                (stat, extMultiplier) -> {
                    double newMulti = getMultipliers().getOrDefault(stat,1D) * extMultiplier;
                    if (DEBUG_MODE){Utils.log("Merged ext. multiplier. New Multi.: " + newMulti);}
                    multipliers.put(stat,newMulti);
                }
        );
    }

    public void debug(String statPoolName){
        Utils.log(statPoolName+"'s Stats ---------------------\n");
        Utils.log("--- Base Stats -----------");
        getBaseStats().forEach(
                (stat, valueTypeMap) -> {
                    Utils.log(stat.getAlias()+": ");
                    valueTypeMap.forEach(
                            (valueType, values) -> {
                                Utils.log(valueType.name()+" -> " + Arrays.toString(values));
                            }
                    );
                    Utils.log("");
                }
        );
        if (getBaseStats().isEmpty()){
            Utils.log("-------------------------------------------");
            return;
        }
        Utils.log("--- Multipliers ----------");
        getMultipliers().forEach(
                (stat, multi) -> Utils.log(stat.getAlias() + ": " + multi)
        );
        Utils.log("\n");

        Utils.log("-------------------------------------------");
    }
}
