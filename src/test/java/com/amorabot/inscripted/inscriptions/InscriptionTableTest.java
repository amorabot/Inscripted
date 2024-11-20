package com.amorabot.inscripted.inscriptions;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.utils.Utils;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
class InscriptionTableTest {

    private static final InscriptionTable sampleTable = getTableFor("AXE");


    @Test
    void AbyssalUnavailableDueToLevel() {
        final int selectedTier = sampleTable.getHighestTierFor(InscriptionID.ADDED_ABYSSAL, 73);
        assertEquals(-1, selectedTier);
    }
    @Test
    void invalidUniqueInscription() {
        final int selectedTier = sampleTable.getHighestTierFor(InscriptionID.FORBIDDEN_PACT, 33);
        assertEquals(-1, selectedTier);
    }
    @Test
    void unavailableInscription() {
        final int selectedTier = sampleTable.getHighestTierFor(InscriptionID.INTELLIGENCE, 33);
        assertEquals(-1, selectedTier);
    }
    @Test
    void maxTierSixAtSeventyThreeForAxeAddedFire() {
        final int selectedTier = sampleTable.getHighestTierFor(InscriptionID.ADDED_FIRE, 73);
        assertEquals(6, selectedTier);
    }
    @Test
    void maxTierTwoAtThirtyThreeForAxePercentPhysical() {
        final int selectedTier = sampleTable.getHighestTierFor(InscriptionID.PERCENT_PHYSICAL, 33);
        assertEquals(2, selectedTier);
    }
    @Test
    void levelMatchesTierTwoBracketForPercentElemental() {
        final int selectedTier = sampleTable.getHighestTierFor(InscriptionID.PERCENT_ELEMENTAL, 25);
        assertEquals(2, selectedTier);
    }
    @Test
    void shouldResultInMaxTier() {
        final int selectedTier = sampleTable.getHighestTierFor(InscriptionID.ADDED_FIRE, 999);
        //ADDED_FIRE tiers = 10 (starts at 0)
        assertEquals(9, selectedTier);
    }
    @Test
    void invalidLevel() {
        assertThrows(AssertionError.class, () -> sampleTable.getHighestTierFor(InscriptionID.PERCENT_PHYSICAL, -11));
    }


    @Test
    void randomTierNotHigherThanMaximum(){
        assertTrue(sampleTable.getRandomTierFor(InscriptionID.ADDED_FIRE,120) <= 9);
    }
    @Test
    void randomTierNotHigherThanSix(){
        assertTrue(sampleTable.getRandomTierFor(InscriptionID.ADDED_FIRE,73) <= 6);
    }



    private static InscriptionTable getTableFor(String itemName){
        InscriptionTableDTO tableData = InscriptionDataManager.getTableResourceDataFor(itemName);
        assert tableData != null;
        Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> affixData = buildInscriptionTable(tableData);
        //Only contains affixes for now
        return new InscriptionTable(affixData, new HashMap<>());
    }

    //These methods use the local copy for JSON resources (only used for tests and setting up plugin folder copies)
    private static Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> buildInscriptionTable(InscriptionTableDTO tableData){
        Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> specificMods = tableData.specificMods();
        if (specificMods.isEmpty()){
            specificMods.put(Affix.PREFIX, new HashMap<>());
            specificMods.put(Affix.SUFFIX, new HashMap<>());
        }


        String[] subtables = tableData.subtables();
        if (subtables == null){
            return specificMods;
        }
        for (String subtable : subtables){
            try {
                Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> subtableData =InscriptionDataManager.getSubtableResourceData(subtable);

                assert subtableData != null;
                mergeTable(specificMods, subtableData, Affix.PREFIX);
                mergeTable(specificMods, subtableData, Affix.SUFFIX);
            } catch (IllegalArgumentException e) {
                Utils.error("Invalid subtable: " + subtable);
                throw new RuntimeException(e);
            }
        }
        return specificMods;
    }
    private static void mergeTable(
            Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> originalTable,
            Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> mergedTable,
            Affix affixToMerge){
        Map<InscriptionID, Map<Integer, Integer>> mergedAffixTable = mergedTable.get(affixToMerge);
        mergedAffixTable.forEach(
                (inscription, values) -> originalTable.get(affixToMerge).merge(inscription, values, (originalValues, valuesToMerge) -> originalValues)
        );
    }
}