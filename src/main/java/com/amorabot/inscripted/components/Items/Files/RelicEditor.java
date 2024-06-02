package com.amorabot.inscripted.components.Items.Files;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.modifiers.unique.Relics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.*;

import static com.amorabot.inscripted.components.Items.Files.ResourcesJSONReader.getResourceJSONAsStream;
import static com.amorabot.inscripted.utils.Utils.log;

public class RelicEditor {
    public static final String armorsFilename = "RELICS/armors";
    public static final String weaponsFilename = "RELICS/weapons";

    public static void setup() throws IOException {
        createRelicArmorsFile();
        createRelicWeaponsFile();
    }

    public static Map<Relics, RelicArmorDAO> loadAllArmors(){
        return getRelicsArmorsFile();
    }
    public static Map<Relics, RelicWeaponDAO> loadAllWeapons(){
        return getRelicsWeaponsFile();
    }
    //TODO: Make de-serialization generic
    private static Map<Relics, RelicArmorDAO> getRelicsArmorsFile(){
        InputStream jsonData = getResourceJSONAsStream(armorsFilename);
        if (jsonData == null){return null;}

        Reader reader = new InputStreamReader(jsonData);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TypeToken<Map<Relics, RelicArmorDAO>> mapType = new TypeToken<Map<Relics, RelicArmorDAO>>(){};
        return gson.fromJson(reader, mapType);
    }
    private static Map<Relics, RelicWeaponDAO> getRelicsWeaponsFile(){
        InputStream jsonData = getResourceJSONAsStream(weaponsFilename);
        if (jsonData == null){return null;}

        Reader reader = new InputStreamReader(jsonData);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TypeToken<Map<Relics, RelicWeaponDAO>> mapType = new TypeToken<Map<Relics, RelicWeaponDAO>>(){};
        return gson.fromJson(reader, mapType);
    }



//  File template generators  ======================================================
    private static void createRelicArmorsFile() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + armorsFilename);
        file.getParentFile().mkdir();
        //Check if the accessed file path exists:
        if (!file.exists()){
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            List<InscriptionID> templateInscriptions = new ArrayList<>();
            templateInscriptions.add(InscriptionID.BLEEDING_HEART_BLD_CHANCE);
            templateInscriptions.add(InscriptionID.FORBIDDEN_PACT);
            RelicArmorDAO armorDAO = new RelicArmorDAO("Dummy armor",1, ItemTypes.CHESTPLATE, ArmorTypes.HEAVY_PLATING, 10, templateInscriptions);
            Map<Relics,RelicArmorDAO> list = new HashMap<>();
            list.put(Relics.TRAINING_DUMMY_ARMOR, armorDAO);
            gson.toJson(list, writer);
            writer.flush();
            writer.close();
        }
    }
    private static void createRelicWeaponsFile() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + weaponsFilename);
        file.getParentFile().mkdir();
        //Check if the accessed file path exists:
        if (!file.exists()){
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            List<InscriptionID> templateInscriptions = new ArrayList<>();
            templateInscriptions.add(InscriptionID.OMINOUS_TWIG_ABYSSAL);
            RelicWeaponDAO weaponDAO = new RelicWeaponDAO("Dummy armor",1, WeaponTypes.WAND, WeaponAttackSpeeds.SLOW, new int[]{4,20}, templateInscriptions);
            Map<Relics,RelicWeaponDAO> list = new HashMap<>();
            list.put(Relics.OMINOUS_TWIG, weaponDAO);
            gson.toJson(list, writer);
            writer.flush();
            writer.close();
        }
    }
    public void addRelicArmor(String name, int ilvl, ItemTypes slot, ArmorTypes type, int baseHealth, List<InscriptionID> inscriptions){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + armorsFilename);
        RelicArmorDAO template = new RelicArmorDAO(name, ilvl, slot, type, baseHealth, inscriptions);
        try {
            Reader reader = new FileReader(file);//Getting the profile map
            JsonObject JSONRelicsObject = gson.fromJson(reader, JsonObject.class); //Getting the JSON from file
            if (!JSONRelicsObject.isJsonObject()){
                log("invalid Relics file");
                return;
            }
            JSONRelicsObject.asMap().put(name.toUpperCase(), gson.toJsonTree(template)); //Updating the profile map

            Writer writer = new FileWriter(file, true); //Changes are done, write and override the file.
            gson.toJson(JSONRelicsObject, writer);
            writer.flush();
            writer.close();
            log("Saving " + name);
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }
}
