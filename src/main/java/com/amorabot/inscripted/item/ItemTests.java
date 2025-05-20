package com.amorabot.inscripted.item;

import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;

public class ItemTests {
    public static void main(String[] args) {
        for (InscriptionIDs inscription : InscriptionIDs.values()){
            System.out.println(inscription.getDefinitionData().getDisplayName());
        }
    }
}
