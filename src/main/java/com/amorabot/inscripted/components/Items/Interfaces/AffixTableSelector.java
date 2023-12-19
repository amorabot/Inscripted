package com.amorabot.inscripted.components.Items.Interfaces;

import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface AffixTableSelector {

    default Map<ModifierIDs, Map<Integer, Integer>> castModMap(Map<String, Map<Integer, Integer>> rawAffixMap){
        Map<ModifierIDs, Map<Integer, Integer>> castedMap = new HashMap<>();
        Set<String> mods = rawAffixMap.keySet();
        for (String mod : mods){
            try {
                ModifierIDs castedMod = ModifierIDs.valueOf(mod);
                castedMap.put(castedMod, rawAffixMap.get(mod));
            } catch (IllegalArgumentException exception){
                Utils.log("Couldn't cast mod: " + mod);
                continue;
            }
        }
        return castedMap;
    }

}
