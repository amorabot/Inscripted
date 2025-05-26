package com.amorabot.inscripted.file;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.item.structure.ItemSubtype;
import com.amorabot.inscripted.utils.Utils;

import java.io.InputStream;
import java.util.Map;

public class ResourcesJSONReader {
    public static <SubType extends Enum<SubType> & ItemSubtype> Map<String, Map<String, Map<Integer, Integer>>> getModifierTableFor(ItemTypes itemType, SubType category){
//        String path;
//        if (itemType.equals(ItemTypes.WEAPON)){
//            if (!(category instanceof WeaponTypes)){return null;}
//            WeaponTypes subType = (WeaponTypes) category;
//
//            path = itemType + "/" + subType;
//            return getModTableFromResources(path);
//        }
//        //Weapons are handled above, its now safe to assume the category argument is a ArmorTypes instance and itemType is a armor slot
//        if (!(category instanceof ArmorTypes)){return null;}
//        ArmorTypes subType = (ArmorTypes) category;
//
//        path = itemType + "/" + subType;
//        return getModTableFromResources(path);
        return null;
    }

    private static Map<String, Map<String, Map<Integer, Integer>>> getModTableFromResources(String path){
//        InputStream jsonData = getResourceJSONAsStream(path);
//        if (jsonData == null){return null;}
//
//        Reader reader = new InputStreamReader(jsonData);
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        TypeToken<Map<String, Map<String, Map<Integer, Integer>>>> mapType
//                = new TypeToken<Map<String, Map<String, Map<Integer, Integer>>>>(){};
//        return gson.fromJson(reader, mapType);
        return null;
    }

//    public static InputStream getResourceJSONAsStream(String path){ //getting the data from the JSON file as a inputStream
//        final String fullPath = "/json/" + path + ".json";
//
//        InputStream inputStream = ResourcesJSONReader.class.getResourceAsStream(fullPath);
//
//        if (inputStream!=null){return  inputStream;}
//        Utils.log("File not found. File's path: "+ fullPath);
//        return null;
//    }

    public static InputStream getResourceJSONAt(String fullPath){
        InputStream inputStream = Inscripted.class.getResourceAsStream(fullPath);

        if (inputStream!=null){return  inputStream;}
        Utils.log("File not found. File's path: "+ fullPath);
        return null;
    }
}
