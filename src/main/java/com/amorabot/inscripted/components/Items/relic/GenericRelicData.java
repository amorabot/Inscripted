package com.amorabot.inscripted.components.Items.relic;

import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;

import java.util.List;

public record GenericRelicData(String name, int itemLevel,
                               List<InscriptionID> specialInscriptions, List<InscriptionID> inscriptions,
                               List<String> flavorText) {
}
