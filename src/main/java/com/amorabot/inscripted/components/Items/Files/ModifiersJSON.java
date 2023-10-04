package com.amorabot.inscripted.components.Items.Files;

import com.amorabot.inscripted.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

public class ModifiersJSON {
//    private static Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> itemType = new HashMap<>();
//    private static final Inscripted plugin = Inscripted.getPlugin();

//    public static void setup(){
//        //do whatever
//    }

    public static Map<String, Map<String, Map<String, Map<String, Map<Integer, int[]>>>>> getBasicModifiers(){ //JSON de-serialization for modifiers.json resource (Entire file)
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
}
