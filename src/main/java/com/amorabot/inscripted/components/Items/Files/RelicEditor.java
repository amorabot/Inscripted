package com.amorabot.inscripted.components.Items.Files;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.Armor.ArmorTypes;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.components.Items.Weapon.WeaponAttackSpeeds;
import com.amorabot.inscripted.components.Items.Weapon.WeaponTypes;
import com.amorabot.inscripted.components.Items.modifiers.InscriptionID;
import com.amorabot.inscripted.components.Items.relic.GenericRelicData;
import com.amorabot.inscripted.components.Items.relic.RelicArmorDAO;
import com.amorabot.inscripted.components.Items.relic.RelicWeaponDAO;
import com.amorabot.inscripted.components.Items.relic.enums.Relics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.*;

public class RelicEditor {
    public static final String armorsFilename = "/relics/armors.json";
    public static final String weaponsFilename = "/relics/weapons.json";

    public static void setup() throws IOException {
        createRelicArmorsFile();
        createRelicWeaponsFile();
    }

    public static Map<Relics, RelicArmorDAO> loadAllArmors(){
        return getRelicsArmorsData();
    }
    public static Map<Relics, RelicWeaponDAO> loadAllWeapons(){
        return getRelicsWeaponsData();
    }


    //TODO: Make de-serialization generic
    private static Map<Relics, RelicArmorDAO> getRelicsArmorsData(){
        InputStream jsonData = ResourcesJSONReader.getResourceJSONAt(armorsFilename);
        if (jsonData == null){return null;}

        Reader reader = new InputStreamReader(jsonData);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TypeToken<Map<Relics, RelicArmorDAO>> mapType = new TypeToken<Map<Relics, RelicArmorDAO>>(){};
        return gson.fromJson(reader, mapType);
    }
    private static Map<Relics, RelicWeaponDAO> getRelicsWeaponsData(){
        InputStream jsonData = ResourcesJSONReader.getResourceJSONAt(weaponsFilename);
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
            templateInscriptions.add(InscriptionID.FORBIDDEN_PACT);

            List<InscriptionID> relicStats = List.of(InscriptionID.BLHA_BLEED_CHANCE);


            List<String> flavor = List.of("This severed, yet","pulsating heart","gives you a unending","desire for carnage.");

            GenericRelicData relicData = new GenericRelicData("Dummy Armor", 1, templateInscriptions, relicStats, flavor);
            RelicArmorDAO armorDAO = new RelicArmorDAO(ItemTypes.CHESTPLATE, ArmorTypes.HEAVY_PLATING, 10, relicData);
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
            templateInscriptions.add(InscriptionID.BERSERK);

            List<InscriptionID> relicStats = List.of(InscriptionID.HELLFORGE_STRENGTH_TO_FIRE_DMG,InscriptionID.OMTW_ABYSSAL);

            List<String> flavor = List.of("Can protect you","against conspiracies,","but certainly not", "against electricity!");

            GenericRelicData relicData = new GenericRelicData("Dummy wEAPONNN", 1, templateInscriptions, relicStats,flavor);
            RelicWeaponDAO weaponDAO = new RelicWeaponDAO(WeaponTypes.WAND, WeaponAttackSpeeds.SLOW, new int[]{4,20}, relicData);
            Map<Relics,RelicWeaponDAO> list = new HashMap<>();
            list.put(Relics.OMINOUS_TWIG, weaponDAO);
            gson.toJson(list, writer);
            writer.flush();
            writer.close();
        }
    }
}
