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
//    default <T extends Enum<T> & ItemModifier> Map<T, Map<Integer, int[]>> castTo(Class<T> castClass, Map<String, Map<Integer, int[]>> affixMap){
//        Map<T, Map<Integer, int[]>> castMap = new HashMap<>();
//        Set<String> mods = affixMap.keySet();
//        for (String mod : mods){
//            for (T m : castClass.getEnumConstants()){
//                if (m.toString().equals(mod)){
//                    castMap.put(m, affixMap.get(mod));
//                }
//            }
//        }
//        return castMap;
//    }

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
