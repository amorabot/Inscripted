package com.amorabot.inscripted.inscriptions;

import com.amorabot.inscripted.item.generation.InscriptionGenerator;
import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.table.InscriptionTable;
import com.amorabot.inscripted.item.inscription.table.MockInscriptionTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class InscriptionTableTest {

    private static final InscriptionTable sampleTable = new MockInscriptionTable("AXE").getInscriptionTable();


    @Test
    void AbyssalUnavailableDueToLevel() {
        final InscriptionIDs inscription = InscriptionIDs.ADDED_ABYSSAL;
        final int selectedTier = InscriptionGenerator.getHighestTierFor(inscription, 73, sampleTable.getTierMappingsFor(inscription));
        assertEquals(-1, selectedTier);
    }
//    @Test
//    void invalidUniqueInscription() {
//        final int selectedTier = sampleTable.getHighestTierFor(InscriptionIDs.FORBIDDEN_PACT, 33);
//        assertEquals(-1, selectedTier);
//    }
    @Test
    void unavailableInscription() {
        final InscriptionIDs inscription = InscriptionIDs.FLAT_INTELLIGENCE;
        final int selectedTier = InscriptionGenerator.getHighestTierFor(inscription, 33, sampleTable.getTierMappingsFor(inscription));
        assertEquals(-1, selectedTier);
    }
    @Test
    void maxTierSixAtSeventyThreeForAxeAddedFire() {
        final InscriptionIDs inscription = InscriptionIDs.ADDED_FIRE;
        final int selectedTier = InscriptionGenerator.getHighestTierFor(inscription, 73, sampleTable.getTierMappingsFor(inscription));
        assertEquals(6, selectedTier);
    }
    @Test
    void maxTierTwoAtThirtyThreeForAxePercentPhysical() {
        final InscriptionIDs inscription = InscriptionIDs.PERCENT_PHYSICAL;
        final int selectedTier = InscriptionGenerator.getHighestTierFor(inscription, 33, sampleTable.getTierMappingsFor(inscription));
        assertEquals(2, selectedTier);
    }
    @Test
    void levelMatchesTierTwoBracketForPercentElemental() {
        final InscriptionIDs inscription = InscriptionIDs.PERCENT_ELEMENTAL;
        final int selectedTier = InscriptionGenerator.getHighestTierFor(inscription, 25, sampleTable.getTierMappingsFor(inscription));
        assertEquals(2, selectedTier);
    }
    @Test
    void shouldResultInMaxTier() {
        final InscriptionIDs inscription = InscriptionIDs.ADDED_FIRE;
        final int selectedTier = InscriptionGenerator.getHighestTierFor(inscription, 999, sampleTable.getTierMappingsFor(inscription));
        //ADDED_FIRE tiers = 10 (starts at 0)
        assertEquals(9, selectedTier);
    }
    @Test
    void invalidLevel() {
        assertThrows(AssertionError.class, () -> InscriptionGenerator.getHighestTierFor(InscriptionIDs.PERCENT_PHYSICAL, -11,
                sampleTable.getTierMappingsFor(InscriptionIDs.PERCENT_PHYSICAL)));
    }


    @Test
    void randomTierNotHigherThanMaximum(){
        final InscriptionIDs inscription = InscriptionIDs.ADDED_FIRE;
        final int tier = InscriptionGenerator.getRandomTierFor(inscription,120, sampleTable.getTierMappingsFor(inscription));
        assertTrue(tier <= 9);
    }
    @Test
    void randomTierNotHigherThanSix(){
        final InscriptionIDs inscription = InscriptionIDs.ADDED_FIRE;
        final int tier = InscriptionGenerator.getRandomTierFor(inscription,73, sampleTable.getTierMappingsFor(inscription));
        assertTrue(tier <= 6);
    }
}