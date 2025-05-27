package com.amorabot.inscripted.item.structure.Armor;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.DefenceTypes;
import com.amorabot.inscripted.item.inscription.Inscription;
import com.amorabot.inscripted.item.inscription.definition.InscriptionDefinition;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.definition.Stats;
import com.amorabot.inscripted.item.inscription.language.ValueType;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.util.*;

@Getter
public class LocalDefence {

    private static final boolean DEBUG_MODE = false;
    private static final Set<Integer> locallyCompiledStatIDs = new HashSet<>();
    private final Map<DefenceTypes, Integer> armorDefences = new HashMap<>();

    public LocalDefence(Armor armor){
        if (armor==null){return;}
        Map<DefenceTypes, Integer> baseDefences = armor.getArmorType().buildArmorDefences(
                armor.getIlvl(),armor.getTier(),armor.getSlot(),armor.getHealth()
        );
        Map<DefenceTypes, Integer> localAddedFlats = buildLocalIncreases(armor.getInscriptions(),true);
        Map<DefenceTypes, Integer> localIncreases = buildLocalIncreases(armor.getInscriptions(),false);

        // Adding flat to base
        for (DefenceTypes flatDefence : localAddedFlats.keySet()){
            addLocalIncrease(baseDefences,flatDefence,localAddedFlats.get(flatDefence));
        }

        final int qualityIncrease = 5 * armor.getQuality();

        // Getting final values(flat+inc) and storing them
        for (DefenceTypes defence : baseDefences.keySet()){
            final int baseDefValue = baseDefences.get(defence);
            final int totalIncrease = localIncreases.getOrDefault(defence,0) + qualityIncrease;
            if (DEBUG_MODE){
                Utils.log(defence.name()+": "+baseDefValue);
                Utils.log("Increase: " + totalIncrease);
            }
            final int finalValue = (int) Utils.applyPercentageTo(baseDefValue, totalIncrease);
            armorDefences.put(defence,finalValue);
        }
    }


    public int getDefence(DefenceTypes defence){
        return armorDefences.getOrDefault(defence,0);
    }
    public void merge(LocalDefence externalLocalDefence){
        Map<DefenceTypes, Integer> externalDefences = externalLocalDefence.getArmorDefences();
        for (DefenceTypes defence : externalDefences.keySet()){
            addLocalIncrease(armorDefences,defence, externalDefences.get(defence));
        }
    }

    private Map<DefenceTypes, Integer> buildLocalIncreases(List<Inscription> inscriptions, boolean targetFlatValues){
        Map<DefenceTypes, Integer> localIncreases = new HashMap<>();

        for (Inscription insc : inscriptions){
            InscriptionIDs inscriptionID = insc.getInscription();
            InscriptionDefinition definition = inscriptionID.getDefinitionData();
            if (insc.isSpecial()){continue;}
            if (definition.isGlobal()){continue;}
            // Its a local armor mod, if it targets a defence type, and its a increase/flat, lets map it
            if (definition instanceof InscriptionDefinition.Regular regularDef){
                if (!isRegularDefence(regularDef)){
                    continue;
                }
                boolean isFlat = regularDef.getBaseData().type().equals(ValueType.FLAT);
                if (targetFlatValues ^ isFlat){ // target & not flat || not target & flat -> Ignore (xor)
                    continue;
                }

                registerLocallyCompiledStat(regularDef.getBaseData(), regularDef.isGlobal(), regularDef.isPositive(), inscriptionID);
                addLocalIncrease(
                        localIncreases,
                        mapStatDefence(regularDef.getBaseData().stat()),
                        insc.getMappedFinalValues()[0]
                );
            }
            if (definition instanceof InscriptionDefinition.Hybrid hybridDef){
                int[] hybridValues = insc.getMappedFinalValues();
                /*
                Both can be increases:
                If the desired mapped increased value corresponds to the primary stat,
                    It should be the first (SINGLE_VALUE final mapping)
                If the desired value is for the secondary,
                    Then it should be the last, regardless of the 1st mapping
                */
                if (isHybridDefence(hybridDef, true)){
                    boolean isPrimaryFlat = hybridDef.getPrimaryData().type().equals(ValueType.FLAT);
                    if (targetFlatValues ^ isPrimaryFlat){
                        continue;
                    }
                    registerLocallyCompiledStat(hybridDef.getPrimaryData(), hybridDef.isGlobal(), hybridDef.isPositive(), inscriptionID);
                    addLocalIncrease(
                            localIncreases,
                            mapStatDefence(hybridDef.getPrimaryData().stat()),
                            hybridValues[0]
                    );
                }
                if (isHybridDefence(hybridDef, false)){
                    boolean isSecondaryFlat = hybridDef.getSecondaryData().type().equals(ValueType.FLAT);
                    if (targetFlatValues ^ isSecondaryFlat){
                        continue;
                    }
                    registerLocallyCompiledStat(hybridDef.getSecondaryData(), hybridDef.isGlobal(), hybridDef.isPositive(), inscriptionID);
                    addLocalIncrease(
                            localIncreases,
                            mapStatDefence(hybridDef.getSecondaryData().stat()),
                            hybridValues[hybridValues.length-1]
                    );
                }
            }
        }

        return localIncreases;
    }
    private void addLocalIncrease(Map<DefenceTypes, Integer> localIncreases, DefenceTypes defenceToAdd, int value){
        localIncreases.put(defenceToAdd,
                localIncreases.getOrDefault(defenceToAdd,0) + value);
    }
    private boolean isRegularDefence(InscriptionDefinition.Regular regularInscription){
        return isDefence(regularInscription.getBaseData());
    }
    private boolean isHybridDefence(InscriptionDefinition.Hybrid hybridInscription, boolean checkPrimary){
        InscriptionDefinition.BaseInscription checkedInscriptionData;
        if (checkPrimary){
            checkedInscriptionData = hybridInscription.getPrimaryData();
        } else {
            checkedInscriptionData = hybridInscription.getSecondaryData();
        }
        return isDefence(checkedInscriptionData);
    }
    public boolean isDefence(InscriptionDefinition.BaseInscription inscriptionData){
        return mapStatDefence(inscriptionData.stat()) != null;
    }
    public DefenceTypes mapStatDefence(Stats statToCheck){
        for (DefenceTypes def : DefenceTypes.values()){
            if (statToCheck.equals(def.getStat())){return def;}
        }
        return null;
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
