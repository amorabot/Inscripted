package com.amorabot.inscripted.item.inscription.table;

import com.amorabot.inscripted.file.InscriptionDataManager;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.language.AffixType;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class MockInscriptionTable {

    private final InscriptionTable inscriptionTable;

    public MockInscriptionTable(String itemName){
        this.inscriptionTable = getTableFor(itemName);
    }

    private static InscriptionTable getTableFor(String itemName){
        InscriptionTableDTO tableData = InscriptionDataManager.getTableResourceDataFor(itemName);
        assert tableData != null;
        Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> affixData = buildInscriptionTable(tableData);
        //Only contains affixes for now
        return new InscriptionTable(affixData, new HashMap<>());
    }

    //These methods use the local copy for JSON resources (only used for tests and setting up plugin folder copies)
    private static Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> buildInscriptionTable(InscriptionTableDTO tableData){
        Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> specificMods = tableData.specificMods();
        if (specificMods.isEmpty()){
            specificMods.put(AffixType.PREFIX, new HashMap<>());
            specificMods.put(AffixType.SUFFIX, new HashMap<>());
        }


        String[] subtables = tableData.subtables();
        if (subtables == null){
            return specificMods;
        }
        for (String subtable : subtables){
            try {
                Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> subtableData =InscriptionDataManager.getSubtableResourceData(subtable);

                assert subtableData != null;
                mergeTable(specificMods, subtableData, AffixType.PREFIX);
                mergeTable(specificMods, subtableData, AffixType.SUFFIX);
            } catch (IllegalArgumentException e) {
                Utils.error("Invalid subtable: " + subtable);
                throw new RuntimeException(e);
            }
        }
        return specificMods;
    }
    private static void mergeTable(
            Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> originalTable,
            Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> mergedTable,
            AffixType affixToMerge){
        Map<InscriptionIDs, Map<Integer, Integer>> mergedAffixTable = mergedTable.get(affixToMerge);
        mergedAffixTable.forEach(
                (inscription, values) -> originalTable.get(affixToMerge).merge(inscription, values, (originalValues, valuesToMerge) -> originalValues)
        );
    }
}
