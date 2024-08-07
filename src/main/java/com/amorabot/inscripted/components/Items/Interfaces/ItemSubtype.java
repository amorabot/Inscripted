package com.amorabot.inscripted.components.Items.Interfaces;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Tiers;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface ItemSubtype {

    default Map<InscriptionID, Map<Integer, Integer>> castModMap(Map<String, Map<Integer, Integer>> rawAffixMap){
        Map<InscriptionID, Map<Integer, Integer>> castedMap = new HashMap<>();
        Set<String> mods = rawAffixMap.keySet();
        for (String mod : mods){
            try {
                InscriptionID castedMod = InscriptionID.valueOf(mod);
                castedMap.put(castedMod, rawAffixMap.get(mod));
            } catch (IllegalArgumentException exception){
                Utils.log("Couldn't cast mod: " + mod);
                continue;
            }
        }
        return castedMap;
    }

    String loadTierName(Tiers tier);
    String getTierName(Tiers tier);
}
