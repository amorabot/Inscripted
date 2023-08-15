package com.amorabot.rpgelements.components.Items.Files;

import com.amorabot.rpgelements.components.Items.DataStructures.Enums.Affix;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.RangeTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.WeaponTypes;
import com.amorabot.rpgelements.components.Items.DataStructures.Enums.WeaponModifiers;
import com.amorabot.rpgelements.RPGElements;
import com.amorabot.rpgelements.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ModifiersJSON {

    //        Map<Integer, int[]> tierValues = new HashMap<>();
    //        Map<String, Map<Integer, int[]>> modifiers = new HashMap<>();
    //        Map<String, Map<String, Map<Integer, int[]>>> affixSection = new HashMap<>();
    //        Map<String, Map<String, Map<String, Map<Integer, int[]>>>> itemCategory = new HashMap<>();
    private static Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> itemType = new HashMap<>();
    private static RPGElements plugin;

    public static void setup(RPGElements mainPlugin){
        plugin = mainPlugin;

        initialize();
        try{
            createJSON();
        }catch (IOException exception){
            Utils.log("Unable to save modifiers JSON properly");
        }
    }

    private static void initialize(){

        for (ItemTypes type : ItemTypes.values()){

            Map<String, Map<String, Map<String, Map<Integer, int[]>>>> itemCategory = new HashMap<>();//----------------

            switch (type){
                case ARMOR -> {
                    continue;
                }
                case WEAPON -> {

                    Map<String, Map<String, Map<Integer, int[]>>> affixSection = new HashMap<>();//-------------

                    for (WeaponTypes category : WeaponTypes.values()){

                        Map<String, Map<Integer, int[]>> modifiers = new HashMap<>();//-------------

                        for (Affix affixCategory : Affix.values()){

                            Map<Integer, int[]> tierValues = new HashMap<>();//-------------

                            WeaponModifiers sampleMod = WeaponModifiers.STAMINA;
                            int tiers = sampleMod.getNumberOfTiers();
                            RangeTypes modRangeType = sampleMod.getRangeType();

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
                            modifiers.put("SAMPLE-MOD", tierValues);//--------------------

                            affixSection.put(affixCategory.toString(), modifiers);//------------------------
                        }
                        itemCategory.put(category.toString(), affixSection);//---------------------------------------
                    }
                    itemType.put(type.toString(), itemCategory);
                }
            }
        }
    }

    public static Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> getBasicModifiers(){ //JSON de-serialization
        InputStream jsonData = getJsonAsStream();
        if (jsonData == null){
            return null;
        }
        Reader reader = new InputStreamReader(jsonData);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TypeToken<Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>>> mapType
                = new TypeToken<Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>>>(){};
        return gson.fromJson(reader, mapType);
    }
    private static InputStream getJsonAsStream(){ //getting the data from the JSON file as a inputStream
        String fileName = "/json/modifiers.json";

        InputStream inputStream = ModifiersJSON.class.getResourceAsStream(fileName);

        if (inputStream!=null){
            return  inputStream;
        }
        Utils.log("File not found: " + fileName);
        return null;
    }
    private static void createJSON() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/modifiers.json");
        file.getParentFile().mkdir();
        //Check if the accessed file path exists:
        if (!file.exists()){
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            gson.toJson(itemType, writer);
            writer.flush();
            writer.close();
        }
    }
    public static Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> getAllModifiers(){
        return itemType;
    }
    //Todo: organizar WeaponModifiers para filtrar prefixes e suffixes (metodos novos em ItemModifier)

}
