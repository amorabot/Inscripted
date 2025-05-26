package com.amorabot.inscripted.item.inscription;

import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.table.InscriptionTable;
import com.amorabot.inscripted.item.inscription.table.MockInscriptionTable;

public class InscriptionTests {
    public static void main(String[] args) {
//        InscriptionTable mockTable = new MockInscriptionTable("AXE").getInscriptionTable();
//        InscriptionTable.loadRawValues();
//        Inscription newInscription = new Inscription(InscriptionIDs.ADDED_FIRE, 2, 1D);
//        System.out.println(newInscription.getDisplayName());
        for (InscriptionIDs insc : InscriptionIDs.values()){
            System.out.println(insc.getDefinitionData().getDisplayName());
        }
    }
}
