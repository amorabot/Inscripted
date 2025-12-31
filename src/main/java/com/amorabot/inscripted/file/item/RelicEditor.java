package com.amorabot.inscripted.file.item;

import com.amorabot.inscripted.item.relic.*;
import com.amorabot.inscripted.file.ResourcesJSONReader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.*;

public class RelicEditor {
    public static final String armorsFilename = "/relics/armor.json";
    public static final String weaponsFilename = "/relics/weapon.json";

//    public static void setup() throws IOException {
//        createRelicArmorsFile();
////        createRelicWeaponsFile();
//    }

    public static Map<Relics, RelicArmorData> loadAllArmors(){
        return getRelicsArmorsData();
    }
    public static Map<Relics, RelicWeaponData> loadAllWeapons(){
        return getRelicsWeaponsData();
    }


    private static Map<Relics, RelicArmorData> getRelicsArmorsData(){
        InputStream jsonData = ResourcesJSONReader.getResourceJSONAt(armorsFilename);
        if (jsonData == null){return null;}

        Reader reader = new InputStreamReader(jsonData);
        Gson gson = new GsonBuilder().create();
        TypeToken<Map<Relics, RelicArmorData>> mapType = new TypeToken<Map<Relics, RelicArmorData>>(){};
        return gson.fromJson(reader, mapType);
    }
    private static Map<Relics, RelicWeaponData> getRelicsWeaponsData(){
        InputStream jsonData = ResourcesJSONReader.getResourceJSONAt(weaponsFilename);
        if (jsonData == null){return null;}

        Reader reader = new InputStreamReader(jsonData);
        Gson gson = new GsonBuilder().create();
        TypeToken<Map<Relics, RelicWeaponData>> mapType = new TypeToken<Map<Relics, RelicWeaponData>>(){};
        return gson.fromJson(reader, mapType);
    }



//  File template generators  ======================================================
//    private static void createRelicArmorsFile() throws IOException {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + armorsFilename);
//        file.getParentFile().mkdirs();
//        //Check if the accessed file path exists:
//        if (!file.exists()){
//            file.createNewFile();
//            Writer writer = new FileWriter(file, false);
//            List<UniqueInscriptionDTO> templateInscriptions = new ArrayList<>();
//            templateInscriptions.add(new UniqueInscriptionDTO("keystone: THUNDERSTRUCK",new int[1]));
//            List<String> flavor = List.of("This severed, yet","pulsating heart","gives you a unending","desire for carnage.");
//
//            GenericRelicData relicData = new GenericRelicData("Dummy Armor", 1, templateInscriptions, flavor);
//            RelicArmorData armorDAO = new RelicArmorData(EquipmentSlots.CHESTPLATE, ArmorTypes.ARMORED, 10, relicData);
//            Map<Relics,RelicArmorData> list = new HashMap<>();
//            list.put(Relics.TRAINING_DUMMY_ARMOR, armorDAO);
//            gson.toJson(list, writer);
//            writer.flush();
//            writer.close();
//        }
//    }
//    private static void createRelicWeaponsFile() throws IOException {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + weaponsFilename);
//        file.getParentFile().mkdir();
//        //Check if the accessed file path exists:
//        if (!file.exists()){
//            file.createNewFile();
//            Writer writer = new FileWriter(file, false);
//            List<InscriptionID> templateInscriptions = new ArrayList<>();
//            templateInscriptions.add(InscriptionID.BERSERK);
//
//            List<InscriptionID> relicStats = List.of(InscriptionID.HELLFORGE_STRENGTH_TO_FIRE_DMG,InscriptionID.OMTW_ABYSSAL);
//
//            List<String> flavor = List.of("Can protect you","against conspiracies,","but certainly not", "against electricity!");
//
//            GenericRelicData relicData = new GenericRelicData("Dummy wEAPONNN", 1, templateInscriptions, relicStats,flavor);
//            RelicWeaponDAO weaponDAO = new RelicWeaponDAO(WeaponTypes.WAND, WeaponAttackSpeeds.SLOW, new int[]{4,20}, relicData);
//            Map<Relics,RelicWeaponDAO> list = new HashMap<>();
//            list.put(Relics.OMINOUS_TWIG, weaponDAO);
//            gson.toJson(list, writer);
//            writer.flush();
//            writer.close();
//        }
//    }
}
