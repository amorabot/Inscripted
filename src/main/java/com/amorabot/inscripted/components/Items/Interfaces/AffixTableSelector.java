package com.amorabot.inscripted.components.Items.Interfaces;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.utils.Utils;

import java.util.*;

public interface AffixTableSelector {
    default Map<String, Map<Integer, int[]>> getAffixes(Map<String, Map<String, Map<String, Map<Integer, int[]>>>> modifiersJSON, Affix affixType) {
        if (modifiersJSON == null){
            Utils.log("De-Serialization error: " + this);
            return null;
        }
        return modifiersJSON.get(this.toString()).get(affixType.toString());
    }

    default Map<ModifierIDs, Map<Integer, int[]>> castToModEnum(Map<String, Map<Integer, int[]>> rawAffixMap){
        Map<ModifierIDs, Map<Integer, int[]>> castedMap = new HashMap<>();
        Set<String> mods = rawAffixMap.keySet();
        for (String mod : mods){
            try {
                ModifierIDs castedMod = ModifierIDs.valueOf(mod);
                castedMap.put(castedMod, rawAffixMap.get(mod));
            } catch (IllegalArgumentException exception){
                continue;
            }
        }
        return castedMap;
    }

}
