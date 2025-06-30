package com.amorabot.inscripted.item.structure.Weapon;

import com.amorabot.inscripted.item.inscription.ProceduralInscription;
import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.util.*;

@Getter
public class LocalDamage {

    private static final boolean DEBUG_MODE = false;
    @Getter
    private static final Set<Integer> locallyCompiledStatIDs = new HashSet<>();
    private final Map<DamageTypes, int[]> weaponDamage = new HashMap<>();

    public LocalDamage(Weapon weapon){
        if(weapon==null){return;}
        List<ProceduralInscription> inscriptions = weapon.getProceduralInscriptions();

        Map<DamageTypes, int[]> baseDamage = new HashMap<>();
        baseDamage.put(DamageTypes.PHYSICAL,weapon.getBaseDamage());

        Map<DamageTypes, int[]> addedDamages = buildAddedDamages(inscriptions);
        Map<DamageTypes, Integer> localIncreases = buildLocalIncreases(inscriptions);

        final int qualityIncrease = 5 * weapon.getQuality();

        //Adding local flats to baseDamage
        for (DamageTypes dmg : addedDamages.keySet()){
            addFlatDamage(baseDamage,dmg, addedDamages.get(dmg));
        }

        //Getting final values
        for (DamageTypes finalDmg : baseDamage.keySet()){
            final int[] baseDmg = baseDamage.get(finalDmg);
            final int totalIncrease = localIncreases.getOrDefault(finalDmg,0) + qualityIncrease;
            if (DEBUG_MODE){
                Utils.log(finalDmg.name()+": "+Arrays.toString(baseDmg));
                Utils.log("Increase: " + totalIncrease);
            }
            final int[] finalValues = Arrays.stream(baseDmg).map(currValue -> (int) ((1+((float)totalIncrease/100))*currValue)).toArray();
            weaponDamage.put(finalDmg,finalValues);
        }
    }

    public int[] getDamage(DamageTypes dmg){
        return weaponDamage.getOrDefault(dmg,new int[2]).clone();
    }
    public void merge(LocalDamage externalLocalDamage){
        Map<DamageTypes, int[]> externalDamages = externalLocalDamage.getWeaponDamage();
        for (DamageTypes dmg : externalDamages.keySet()){
            addFlatDamage(weaponDamage,dmg,externalDamages.get(dmg));
        }
    }

    private Map<DamageTypes, int[]> buildAddedDamages(List<ProceduralInscription> inscriptions){
        Map<DamageTypes, int[]> addedDamages = new HashMap<>();

        for (ProceduralInscription insc : inscriptions){
            InscriptionIDs inscriptionID = insc.getInscription();
            InscriptionDefinition definition = inscriptionID.getDefinitionData();
            if (insc.isSpecial()){continue;}
            if (definition.isGlobal()){continue;}
            if (definition instanceof InscriptionDefinition.Regular regularDef){
                if (!regularDef.getBaseData().type().equals(ValueType.FLAT)){
                    continue;
                }
                // It's a locally compiled stat, lets process it:
                registerLocallyCompiledStat(regularDef.getBaseData(), regularDef.isGlobal(), regularDef.isPositive(), inscriptionID);

                DamageTypes damageToAdd = mapFlatDamageTypes(regularDef.getBaseData().stat());
                if(damageToAdd==null){continue;}
                addFlatDamage(addedDamages,damageToAdd, insc.getMappedFinalValues());
            }
            if (definition instanceof InscriptionDefinition.Hybrid hybridDef){
                int[] hybridValues = insc.getMappedFinalValues();
                if (hybridDef.getPrimaryData().type().equals(ValueType.FLAT)){
                    /*
                    We only register this one, the other might be "Accuracy", which might be local and does not matter locally.
                    Same logic applies to the 2nd half. It's a side effect of having arbitrary local mods and only compiling damage-related ones.
                    */
                    registerLocallyCompiledStat(hybridDef.getPrimaryData(), hybridDef.isGlobal(), hybridDef.isPositive(), inscriptionID);

                    DamageTypes damageToAdd = mapFlatDamageTypes(hybridDef.getPrimaryData().stat());
                    if(damageToAdd==null){continue;}
                    // Get the first 2 values [X, Y, ...]
                    addFlatDamage(addedDamages,damageToAdd, new int[]{hybridValues[0],hybridValues[1]});
                }
                if (hybridDef.getSecondaryData().type().equals(ValueType.FLAT)){
                    registerLocallyCompiledStat(hybridDef.getSecondaryData(), hybridDef.isGlobal(), hybridDef.isPositive(), inscriptionID);

                    DamageTypes damageToAdd = mapFlatDamageTypes(hybridDef.getSecondaryData().stat());
                    if(damageToAdd==null){continue;}
                    final int lastIndex = hybridValues.length-1;
                    // Get the last 2 values [..., X, Y]
                    addFlatDamage(addedDamages,damageToAdd, new int[]{hybridValues[lastIndex-1],hybridValues[lastIndex]});
                }
            }
        }

        return addedDamages;
    }
    private Map<DamageTypes, Integer> buildLocalIncreases(List<ProceduralInscription> inscriptions){
        Map<DamageTypes, Integer> localIncreases = new HashMap<>();

        for (ProceduralInscription insc : inscriptions){
            InscriptionIDs inscriptionID = insc.getInscription();
            InscriptionDefinition definition = inscriptionID.getDefinitionData();
            if (insc.isSpecial()){continue;}
            if (definition.isGlobal()){continue;}
            if (definition instanceof InscriptionDefinition.Regular regularDef){
                if (!regularDef.getBaseData().type().equals(ValueType.INCREASED)){
                    continue;
                }
                registerLocallyCompiledStat(regularDef.getBaseData(), regularDef.isGlobal(), regularDef.isPositive(), inscriptionID);
                addStatIncreases(localIncreases,regularDef.getBaseData().stat(),insc.getMappedFinalValues()[0]);
            }
            if (definition instanceof InscriptionDefinition.Hybrid hybridDef){
                int[] hybridValues = insc.getMappedFinalValues();
                boolean is1stIncr = hybridDef.getPrimaryData().type().equals(ValueType.INCREASED);
                if (is1stIncr){
                    registerLocallyCompiledStat(hybridDef.getPrimaryData(), hybridDef.isGlobal(), hybridDef.isPositive(), inscriptionID);
                    addStatIncreases(localIncreases,hybridDef.getPrimaryData().stat(),hybridValues[0]);
                }
                boolean is2ndIncr = hybridDef.getSecondaryData().type().equals(ValueType.INCREASED);
                if (is2ndIncr){
                    registerLocallyCompiledStat(hybridDef.getSecondaryData(), hybridDef.isGlobal(), hybridDef.isPositive(), inscriptionID);
                    addStatIncreases(localIncreases,hybridDef.getSecondaryData().stat(),hybridValues[hybridValues.length-1]);
                }
            }
        }

        return localIncreases;
    }
    private void addFlatDamage(Map<DamageTypes, int[]> baseDamage, DamageTypes dmgType, int[] values){
        int[] existingDmg = baseDamage.getOrDefault(dmgType,new int[2]);
        baseDamage.put(dmgType, Utils.vectorSum(existingDmg,values));
    }
    private void addStatIncreases(Map<DamageTypes, Integer> localIncreases, Stats dmgStat, int value){
        switch (dmgStat){
            case PHYSICAL_DAMAGE -> addLocalncrease(localIncreases,DamageTypes.PHYSICAL,value);
            case FIRE_DAMAGE -> addLocalncrease(localIncreases,DamageTypes.FIRE,value);
            case LIGHTNING_DAMAGE -> addLocalncrease(localIncreases,DamageTypes.LIGHTNING,value);
            case COLD_DAMAGE -> addLocalncrease(localIncreases,DamageTypes.COLD,value);
            case ELEMENTAL_DAMAGE -> {
                addLocalncrease(localIncreases,DamageTypes.FIRE,value);
                addLocalncrease(localIncreases,DamageTypes.COLD,value);
                addLocalncrease(localIncreases,DamageTypes.LIGHTNING,value);
            }
            case ABYSSAL_DAMAGE -> addLocalncrease(localIncreases,DamageTypes.ABYSSAL,value);
        }
    }
    private DamageTypes mapFlatDamageTypes(Stats dmgStat){
        for (DamageTypes dmg : DamageTypes.values()){
            if (dmg.getDmgStat().equals(dmgStat)){return dmg;}
        }
        return null;
    }
    private void addLocalncrease(Map<DamageTypes, Integer> localIncreases, DamageTypes damageToAdd, int value){
        localIncreases.put(damageToAdd,
                localIncreases.getOrDefault(damageToAdd,0) + value);
    }


    private void registerLocallyCompiledStat(InscriptionDefinition.BaseInscription baseData, boolean isGlobal, boolean isPositive, InscriptionIDs sourceInscription){
        int definitionID = baseData.id(isGlobal,isPositive);
        if (DEBUG_MODE){Utils.log("Inscription code("+sourceInscription+"): " + definitionID);}
        locallyCompiledStatIDs.add(definitionID);
    }
    public static boolean hasStatID(int statID){
        return locallyCompiledStatIDs.contains(statID);
    }
}
