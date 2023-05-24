package pluginstudies.pluginstudies.managers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import pluginstudies.pluginstudies.RPGElements;
import pluginstudies.pluginstudies.components.PlayerComponents.Attributes;
import pluginstudies.pluginstudies.components.PlayerComponents.Profile;
import pluginstudies.pluginstudies.components.PlayerComponents.Stats;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static pluginstudies.pluginstudies.utils.Utils.log;

public class JSONProfileManager {

    private static Map<UUID, Profile> profiles = new HashMap<>();
    private static RPGElements plugin;

    public JSONProfileManager (RPGElements pluginInstance){
        plugin = pluginInstance;
    }

    // CRUD Operations ->  Create - Read - Update - Delete

    public static Profile createProfile(String uuid){
        Attributes attributes = new Attributes(10, 0,0,0);
        Stats stats = new Stats(40, 0, 1);
        UUID id = UUID.fromString(uuid);
        profiles.put(id, new Profile(attributes, stats));

        return new Profile(attributes, stats);
    }

    public static Profile getProfile(String uuid){
        UUID id = UUID.fromString(uuid);
        return profiles.get(id);
    }

    public static void setProfile(String uuid, Profile profile){
        UUID id = UUID.fromString(uuid);
        profiles.put(id, profile);
    }

    public static void deleteProfile(String uuid){
        UUID id = UUID.fromString(uuid);
        profiles.remove(id);
    }

    public static void saveToJSON() throws IOException { //Saves ALL cached profile changes to the main JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/profiles.json");
        file.getParentFile().mkdir();
        //Check if the accessed file path exists:
        if (!file.exists()){
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            gson.toJson(profiles, writer);
            writer.flush();
            writer.close();
            return;
        }
        //The file can be accessed:
        try {
            Reader reader = new FileReader(file);
            JsonObject JSONProfileMap = gson.fromJson(reader, JsonObject.class); //Getting the JSON from file
            if (!JSONProfileMap.isJsonObject()){
                log("invalid profileMap");
                return;
            }
            for (UUID uuid : profiles.keySet()){ //For each profile in cache, update its entry in the profile map JSON
                JSONProfileMap.asMap().put(uuid.toString(), gson.toJsonTree(profiles.get(uuid)));
            }
            Writer writer = new FileWriter(file, false); //Changes are done, write and override the file.
            gson.toJson(JSONProfileMap, writer);
            writer.flush();
            writer.close();
            log("Saving operation complete.");
        } catch (FileNotFoundException exception){
            exception.printStackTrace();
        }
    }
    public static void loadFromJSON() throws IOException{ //Loads EVERY profile registered, refrain from using this operation casually
        Gson gson = new Gson();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/profiles.json");
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
                TypeToken<Map<UUID, Profile>> mapType = new TypeToken<Map<UUID, Profile>>(){};
                Map<UUID, Profile> mappedProfiles = gson.fromJson(reader, mapType);
                profiles = mappedProfiles;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void loadProfileFromJSON(String uuid){ // Loads a specific profile into memory
        Gson gson = new Gson();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/profiles.json");
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
                JsonObject JSONProfileMap = gson.fromJson(reader, JsonObject.class);
                JsonElement JSONProfile = JSONProfileMap.get(uuid);
                String stringifiedProfile = JSONProfile.toString();
                log(stringifiedProfile);
                Profile playerProfile = gson.fromJson(stringifiedProfile, Profile.class);
                profiles.put(UUID.fromString(uuid), playerProfile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            log("Profile loaded: " + uuid);
        }
    }

    public static boolean isNewPlayer(UUID uuid){ // Checks the profiles JSON for this player's entry
        Gson gson = new Gson();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/profiles.json");
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
                JsonObject JSONProfileMap = gson.fromJson(reader, JsonObject.class);
                if (!JSONProfileMap.isJsonObject()){
                    log("trouble reading from JSON Map - isNewPlayer() call");
                }
                return !JSONProfileMap.has(uuid.toString());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        log("The profiles JSON file is currently unavailable.");
        return false;
    }
}
