package com.amorabot.inscripted.inscriptions;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;

import java.util.Map;

public record InscriptionTableDTO(
        Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> specificMods,
        Map<InscriptionID, Map<Integer, int[]>> implicits,
        String[] subtables) {
}
