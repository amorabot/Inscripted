package com.amorabot.inscripted.item.inscription;

import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;

public class InscriptionTests {
    public static void main(String[] args) {
        for (InscriptionIDs insc : InscriptionIDs.values()){
            System.out.println(insc.getDefinitionData().getDisplayName());
        }
    }
}
