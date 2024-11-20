package com.amorabot.inscripted.inscriptions;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.Affix;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.*;

import static com.amorabot.inscripted.components.Items.Files.ResourcesJSONReader.getResourceJSONAt;
import static com.amorabot.inscripted.utils.Utils.log;

public class InscriptionDataManager {


    //TODO: make a "resetFiles()" that overrides any present files (or add a flag)
    public static void setupFiles(){
        Utils.log("Setting up Inscription local files");
        setupJSONTables();
        setupValuesTable(Affix.PREFIX.name());
        setupValuesTable(Affix.SUFFIX.name());

    }

    private static void setupValuesTable(String tableName){
        File file = new File(Inscripted.getPlugin().getDataFolder(), "modifiers/"+ tableName +".yml");

        if (!file.exists()){
            Inscripted.getPlugin().saveResource("modifiers/"+ tableName +".yml", false);
        }
    }
    public static YamlConfiguration readValuesTableData(String tableName){
        File file = new File(Inscripted.getPlugin().getDataFolder(), "modifiers/"+ tableName +".yml");

        if (!file.exists()){return null;}

        YamlConfiguration config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        Utils.log(tableName+" table successfully loaded.");
        return config;
    }





    public static InscriptionTableDTO loadInscriptionTableDataFor(String itemName){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + "/modifiers/tables/"+itemName+".json");
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
                TypeToken<InscriptionTableDTO> dtoTypeToken = new TypeToken<InscriptionTableDTO>(){};
                log("Table loaded: " + itemName);
                return gson.fromJson(reader, dtoTypeToken);
            } catch (FileNotFoundException e) {
                Utils.error("Table '"+itemName+"' not found.");
                throw new RuntimeException(e);
            }
        }
        Utils.error("Table does not exist. ("+itemName+")");
        return null;
    }
    public static Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> loadSubtable(String subtableName){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + "/modifiers/subtables/"+subtableName+".json");
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
                TypeToken<Map<Affix, Map<InscriptionID, Map<Integer, Integer>>>> subtableTypeToken
                        = new TypeToken<Map<Affix, Map<InscriptionID, Map<Integer, Integer>>>>(){};

                log("Subtable loaded: " + subtableName);
                return gson.fromJson(reader, subtableTypeToken);
            } catch (FileNotFoundException e) {
                Utils.error("Table '"+subtableName+"' not found.");
                throw new RuntimeException(e);
            }
        }
        Utils.error("Table does not exist. ("+subtableName+")");
        return null;
    }


    public static void createTemplateTable(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + "/templateTable.json");
        file.getParentFile().mkdir();
        //Check if the accessed file path exists:
        try {
            if (!file.exists()){
                file.createNewFile();
                Writer writer = new FileWriter(file, false);

                Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> specificMods = new HashMap<>();

                Map<InscriptionID, Map<Integer, int[]>> implicitData = new HashMap<>();
                Map<Integer, int[]> tierData = new HashMap<>();
                tierData.put(0, new int[1]);
                tierData.put(1, new int[1]);
                tierData.put(2, new int[1]);
                tierData.put(3, new int[1]);
                tierData.put(4, new int[1]);
                implicitData.put(InscriptionID.MARAUDER_AXE, tierData);

                String[] subtables = new String[]{"GENERIC_WEAPON","STRENGTH_WEAPON"};

                InscriptionTableDTO dto = new InscriptionTableDTO(specificMods, implicitData, subtables);
                gson.toJson(dto, writer);
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            Utils.error("Unable to create Inscription table template JSON.");
            throw new RuntimeException(e);
        }
    }


    private static void setupJSONTables(){
        /*
        If done using the Enum classes (WeaponTypes, ArmorTypes,...), the initialization for the Enum will start.
            In case its the first startup (responsible for setting up the files in the plugins folder)
            those enum values will try to load a invalid file into memory
                Alternative: Make the enum startup fetch responsible for cloning the needed files locally if
                 they're unavailable(?)
        */
        setupTable("tables","AXE");
        setupTable("tables","SWORD");
        setupTable("tables","BOW");
        setupTable("tables","DAGGER");
        setupTable("tables","WAND");
        setupTable("tables","MACE");

        setupTable("tables", "HEAVY_PLATING");
        setupTable("tables", "CARVED_PLATING");
        setupTable("tables", "LIGHT_CLOTH");
        setupTable("tables", "RUNIC_LEATHER");
        setupTable("tables", "ENCHANTED_SILK");
        setupTable("tables", "RUNIC_STEEL");

        setupTable("subtables","GENERIC_WEAPON");
        setupTable("subtables","STRENGTH_WEAPON");
        setupTable("subtables","DEXTERITY_WEAPON");
        setupTable("subtables","INTELLIGENCE_WEAPON");

        setupTable("subtables","GENERIC_ARMOR");
        setupTable("subtables","STRENGTH_ARMOR");
        setupTable("subtables","DEXTERITY_ARMOR");
        setupTable("subtables","INTELLIGENCE_ARMOR");

    }
    private static void setupTable(String tableType, String tableName){
        Object jsonData;
        switch (tableType){
            case "tables":
                jsonData = getTableResourceDataFor(tableName);
                break;
            case "subtables":
                jsonData = getSubtableResourceData(tableName);
                break;
            default:
                Utils.error("Invalid path: " + tableType+"/"+tableName);
                return;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + "/modifiers/"+tableType+"/"+tableName+".json");
        file.getParentFile().mkdir();
        //Check if the accessed file path exists:
        try {
            if (!file.exists()){
                file.createNewFile();
                Writer writer = new FileWriter(file, false);

                gson.toJson(jsonData, writer);
                writer.flush();
                writer.close();
            }
        } catch (IOException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    //Used for tests
    public static Map<Affix, Map<InscriptionID, Map<Integer, Integer>>> getSubtableResourceData(String subtableName){
        InputStream jsonData = getResourceJSONAt("/modifiers/subtables/"+subtableName+".json");
        if (jsonData == null){return null;}

        Reader reader = new InputStreamReader(jsonData);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TypeToken<Map<Affix, Map<InscriptionID, Map<Integer, Integer>>>> mapType = new TypeToken<Map<Affix, Map<InscriptionID, Map<Integer, Integer>>>>(){};
        return gson.fromJson(reader, mapType);
    }
    //Used for tests
    public static InscriptionTableDTO getTableResourceDataFor(String itemName){
        InputStream jsonData = getResourceJSONAt("/modifiers/tables/"+itemName+".json");
        if (jsonData == null){return null;}

        Reader reader = new InputStreamReader(jsonData);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TypeToken<InscriptionTableDTO> dtoTypeToken = new TypeToken<InscriptionTableDTO>(){};
        return gson.fromJson(reader, dtoTypeToken);
    }
}
