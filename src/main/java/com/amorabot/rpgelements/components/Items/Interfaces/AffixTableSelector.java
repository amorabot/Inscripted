package com.amorabot.rpgelements.components.Items.Interfaces;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface AffixTableSelector {
    default Map<String, Map<Integer, int[]>> getAffixTable(Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> modifiersJSON, ItemTypes type, Affix affixType) {
        if (modifiersJSON == null){
            Utils.log("De-Serialization error:" + type);
        }
        return modifiersJSON.get(type.toString()).get(this.toString()).get(affixType.toString());
    }
    default <T extends Enum<T> & ItemModifier> Map<T, Map<Integer, int[]>> castTo(Class<T> castClass, Map<String, Map<Integer, int[]>> affixMap){
        Map<T, Map<Integer, int[]>> castMap = new HashMap<>();
        Set<String> mods = affixMap.keySet();
        for (String mod : mods){
            for (T m : castClass.getEnumConstants()){
                if (m.toString().equals(mod)){
                    castMap.put(m, affixMap.get(mod));
                }
            }
        }
        return castMap;
    }

}
