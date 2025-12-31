package com.amorabot.inscripted.item.inscription.table;

import com.amorabot.inscripted.item.inscription.definition.InscriptionIDs;
import com.amorabot.inscripted.item.inscription.language.AffixType;

import java.util.Map;

public record InscriptionTableDTO(
        Map<AffixType, Map<InscriptionIDs, Map<Integer, Integer>>> specificMods,
        Map<InscriptionIDs, Map<Integer, int[]>> implicits,
        String[] subtables) {
}
