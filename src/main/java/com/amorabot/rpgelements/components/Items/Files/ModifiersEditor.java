package com.amorabot.rpgelements.components.Items.Files;

import com.amorabot.rpgelements.components.Items.Armor.ArmorModifiers;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
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
    private static final RPGElements plugin = RPGElements.getPlugin();
    private final Class<ModifierType> castClass;

    public ModifiersEditor(Class<ModifierType> modifierTypeClass){
        this.modifiersMap = loadModifiersJSONIntoMap();
        this.castClass = modifierTypeClass;
    }
    public static void createEmptyJSON() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/modifiers.json");
        file.getParentFile().mkdir();
        //Check if the accessed file path exists:
        if (!file.exists()){
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> mapStructure = new HashMap<>();
            gson.toJson(mapStructure, writer);
            writer.flush();
            writer.close();
        }
    }
    public static boolean fileExists(){
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/modifiers.json");
        return file.exists();
    }

    public Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> loadModifiersJSONIntoMap() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/modifiers.json");

        try {
            Reader reader = new FileReader(file);
            TypeToken<Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>>> mapType
                    = new TypeToken<Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>>>(){};
            log("Loaded modifiers JSON (ModifiersEditor)");
            return gson.fromJson(reader, mapType);
        } catch (IOException exception){
            log("Unable to load modifiers JSON (ModifiersEditor)");
            return null;
        }
    }

    public Map<ModifierType, Map<Integer, int[]>> deserializeCastedModifiers(String itemSubtype, String affixType){
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
    public Map<String, Map<Integer, int[]>> loadModTierLevel(ItemTypes type, String subtype, Affix affix){
        return modifiersMap.get(type.toString()).get(subtype).get(affix.toString());
    }
    public void addModifier(ItemTypes type, String subtype, Affix affix, String modName){
        if (modifiersMap==null){
            Utils.log("Null modifierMap");
            return;
        }
        //Validation for subType should be done prior to this method call*
        Map<String, Map<Integer, int[]>> modValueMap = loadModTierLevel(type, subtype, affix);

        try {
            ArmorModifiers entry = ArmorModifiers.valueOf(modName.toUpperCase());
            Map<Integer, int[]> templateModStats = new HashMap<>();
            int[] templateArray;
            //Lets add a empty int[] based on the entry's range type
            switch (entry.getRangeType()) {
                case SINGLE_RANGE -> templateArray = new int[2];
                case DOUBLE_RANGE -> templateArray = new int[4];
                default -> templateArray = new int[1];
            }
            //Lets add entry.numberoftiers values to the map
            for (int i = 0; i < entry.getNumberOfTiers(); i++) {
                templateModStats.put(i, templateArray);
            }
            //modStats is updated, now lets put the entry inside modValueMap
            modValueMap.put(entry.toString(), templateModStats);
            Utils.log("Added: " + modName);
            persistJSONData();
        } catch (IllegalArgumentException exception){
            Utils.log("Unable to add: " + modName);
        }
    }
    public void clearAffix(ItemTypes type, String subtype, Affix affix){
        if (modifiersMap==null){
            Utils.log("Null modifierMap");
            return;
        }
        //Validation for subType should be done prior to this method call*
        Map<String, Map<Integer, int[]>> modValueMap = loadModTierLevel(type, subtype, affix);
        modValueMap.clear();
        Utils.log("Cleared: " + type + subtype + affix);
        persistJSONData();
    }
    public void removeModifier(ItemTypes type, String subtype, Affix affix, String modName){
        if (modifiersMap==null){
            Utils.log("Null modifierMap");
            return;
        }
        Map<String, Map<Integer, int[]>> modValueMap = loadModTierLevel(type, subtype, affix);
        modValueMap.remove(modName);
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
    public void persistJSONData(String rootName, Map<String, Map<String, Map<String, Map<Integer, int[]>>>> modData){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/modifiers.json");
//      The file can be accessed:
        try {
            Writer writer = new FileWriter(file, false); //Changes are done, write and override the file.
            modifiersMap.put(rootName, modData); //Overrides rootName data and persists it
            gson.toJson(modifiersMap, writer);
            writer.flush();
            writer.close();
            log("Saving operation complete. (key+value call)");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
