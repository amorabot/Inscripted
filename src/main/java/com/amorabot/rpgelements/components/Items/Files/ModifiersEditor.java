package com.amorabot.rpgelements.components.Items.Files;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.components.Items.Interfaces.ItemModifier;
import com.amorabot.rpgelements.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.amorabot.rpgelements.utils.Utils.log;

public class ModifiersEditor <ModifierType extends Enum<ModifierType> & ItemModifier> {

    private final Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> modifiersMap;
    private final RPGElements plugin;
    private final Class<ModifierType> castClass;

    public ModifiersEditor(RPGElements plugin, Class<ModifierType> modifierTypeClass){
        this.plugin = plugin;
        this.modifiersMap = loadModifiers();
        this.castClass = modifierTypeClass;
    }

    public Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> loadModifiers() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/modifiers.json");

        try {
            Reader reader = new FileReader(file);
            TypeToken<Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>>> mapType
                    = new TypeToken<Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>>>(){};
            return gson.fromJson(reader, mapType);
        } catch (IOException exception){
            log("Unable to load modifiers JSON (ModifiersEditor)");
            return null;
        }
    }

    public Map<String, Map<Integer, int[]>> getModsByAffixType(String itemSubtype, String affixType){
        String uppercaseSubtype = itemSubtype.toUpperCase();
        Set<String> itemTypes = modifiersMap.keySet();
        for (String type : itemTypes){
            if (modifiersMap.get(type).containsKey(uppercaseSubtype)){
                if (affixType.equals("")){
                    log("No affixtype given");
                    return null;
                }
                return modifiersMap.get(type).get(uppercaseSubtype).get(affixType.toUpperCase());
            }
        }
        log("Unable to get mods for: " + uppercaseSubtype + " " + affixType);
        return null;
    }

    public Map<ModifierType, Map<Integer, int[]>> deserializeModsByAffixType(String itemSubtype, String affixType){
        //Car c = (Car) v NAO FUNCIONA PORRA
        //Vehicle v = new Vehicle();
        String uppercaseSubtype = itemSubtype.toUpperCase();
        String uppercaseAffixType = affixType.toUpperCase();
        Set<String> allItemTypes = modifiersMap.keySet();
        for (String type : allItemTypes){
            if (modifiersMap.get(type).containsKey(uppercaseSubtype)){
                Map<String, Map<Integer, int[]>> JSONMods = modifiersMap.get(type).get(uppercaseSubtype).get(uppercaseAffixType);
                if (JSONMods.isEmpty()){
                    log("No affixtype given");
                    return null;
                }
                return castModifiers(JSONMods);
            }
        }
        log("Unable to get mods for: " + uppercaseSubtype + " " + affixType);
        return null;
    }

    public Map<ModifierType, Map<Integer, int[]>> castModifiers(Map<String, Map<Integer, int[]>> jsonMods){
        Map<ModifierType, Map<Integer, int[]>> castMap = new HashMap<>();
        Set<String> mods = jsonMods.keySet();
        for (String mod : mods){
            for (ModifierType m : castClass.getEnumConstants()){
                if (m.toString().equals(mod)){
                    castMap.put(m, jsonMods.get(mod));
                }
            }
        }
        return castMap;
    }
    public void addMod(Affix affixType, Map<String, Map<Integer, int[]>> affixMap, String newMod){ //newMod is case-sensitive
        Map<Integer, int[]> tierValues = new HashMap<>();

        ModifierType[] modifiers = castClass.getEnumConstants();
        ModifierType targetMod = null;
        for (ModifierType mod : modifiers){
            if ((mod.toString().equals(newMod)) && (mod.getAffixType()==affixType)){ //Asserts that the consant exists and it matches the affixType
                targetMod = mod;
                break;
            }
        }
        if (targetMod == null){ //If no suitable mod is found, do a early return
            return;
        }
        int tiers = targetMod.getNumberOfTiers();
        RangeTypes modRangeType = targetMod.getRangeType();

        int[] values;
        switch (modRangeType){
            case SINGLE_VALUE -> values = new int[]{0};
            case SINGLE_RANGE -> values = new int[]{0,0};
            case DOUBLE_RANGE -> values = new int[]{0,0,0,0};
            default -> values = new int[]{-1};
        }
        for (int i = 0; i < tiers; i++){
            tierValues.put(i,values);//--------------------------------
        }
        affixMap.put(newMod, tierValues);
        persistJSONData();
    }
    public void removeMod(Map<String, Map<Integer, int[]>> affixMap, String targetMod){
        if (affixMap.remove(targetMod) == null){
            Utils.log("No match for given modifier: " + targetMod);
        }
        persistJSONData();
    }

    private void persistJSONData(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/modifiers.json");
//      The file can be accessed:
        try {
            Writer writer = new FileWriter(file, false); //Changes are done, write and override the file.
            gson.toJson(modifiersMap, writer);
            writer.flush();
            writer.close();
            log("Saving operation complete.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
