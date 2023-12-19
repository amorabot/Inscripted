package com.amorabot.inscripted.components.Items.Files;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.DataStructures.ModifierIDs;
import com.amorabot.inscripted.utils.Utils;

import java.util.List;

public class ModifierEditor {


    public static void setupMods(){
        for (ModifierIDs mod : ModifierIDs.values()){
            setMod(mod);
        }
    }

    public static void setMod(ModifierIDs mod){
        if (ItemModifiersConfig.get() == null){
            Utils.log("Yaml config not loaded, aborting...");
            return;
        }
        if (mod.getAffixType().equals(Affix.UNIQUE)){
            Utils.log("Unique mod... ignoring");
            return;
        }
        final String modPath = mod.toString();
        for (int i = 0; i< mod.getNumberOfTiers(); i++){
            String affixType = mod.getAffixType().toString();
            String fullPath = affixType + "." + modPath + "." + i;
            int[] values;
            switch (mod.getRangeType()){
                case SINGLE_VALUE -> values = new int[1];
                case SINGLE_RANGE -> values = new int[2];
                case DOUBLE_RANGE -> values = new int[4];
                default -> {
                    Utils.log("Invalid mod range type for " + mod);
                    return;
                }
            }
            ItemModifiersConfig.set(fullPath, values);
            Utils.log(fullPath);
        }
        Utils.log("Saving...");
    }

    public static int[] getModValuesFor(ModifierIDs mod, int tier){
        String affixType = mod.getAffixType().toString();
        String modString = mod.toString();
        String path = affixType+"."+modString+"."+tier;
        List<Integer> values = ItemModifiersConfig.get().getIntegerList(path);
        int[] valuesArray = new int[values.size()];
        for (int i = 0; i< valuesArray.length; i++){
            valuesArray[i] = values.get(i);
        }
        return valuesArray;
    }
}
