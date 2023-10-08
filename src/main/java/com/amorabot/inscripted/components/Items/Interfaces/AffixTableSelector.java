package com.amorabot.inscripted.components.Items.Interfaces;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierList;
import com.amorabot.inscripted.utils.Utils;

import java.util.*;

public interface AffixTableSelector {
    default Map<String, Map<Integer, int[]>> getAffixTable(Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> modifiersJSON, ItemTypes type, Affix affixType) {
        if (modifiersJSON == null){
            Utils.log("De-Serialization error:" + type);
            return null;
        }
        return modifiersJSON.get(type.toString()).get(this.toString()).get(affixType.toString());
    }

    default Map<ModifierList, Map<Integer, int[]>> castToModEnum(Map<String, Map<Integer, int[]>> rawAffixMap){
        Map<ModifierList, Map<Integer, int[]>> castedMap = new HashMap<>();
        Set<String> mods = rawAffixMap.keySet();
        for (String mod : mods){
            try {
                ModifierList castedMod = ModifierList.valueOf(mod);
                castedMap.put(castedMod, rawAffixMap.get(mod));
            } catch (IllegalArgumentException exception){
                continue;
            }
        }
        return castedMap;
    }

}
