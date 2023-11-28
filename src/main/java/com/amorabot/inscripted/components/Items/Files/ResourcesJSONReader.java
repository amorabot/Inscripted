package com.amorabot.inscripted.components.Items.Files;

import com.amorabot.inscripted.components.Items.DataStructures.Enums.ItemTypes;
import com.amorabot.inscripted.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

public class ResourcesJSONReader {
    public static Map<String, Map<String, Map<String, Map<Integer, int[]>>>> getModifierTableFor(ItemTypes itemType){ //JSON de-serialization for mod tables resource
        InputStream jsonData = getJsonAsStream(itemType);
        if (jsonData == null){
            return null;
        }
        Reader reader = new InputStreamReader(jsonData);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        TypeToken<Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> mapType
                = new TypeToken<Map<String, Map<String, Map<String, Map<Integer, int[]>>>>>(){};
        return gson.fromJson(reader, mapType);
    }
    private static InputStream getJsonAsStream(ItemTypes targetJson){ //getting the data from the JSON file as a inputStream
        String filePrefix = "/json/";
        String fileName = targetJson.toString().toLowerCase();
        String fileSuffix = ".json";

        InputStream inputStream = ResourcesJSONReader.class.getResourceAsStream(filePrefix+fileName+fileSuffix);

        if (inputStream!=null){
            return  inputStream;
        }
        Utils.log("File not found: " + fileName + ".json in resources"+filePrefix);
        return null;
    }
}
