package com.amorabot.inscripted.managers;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.DamageComponent;
import com.amorabot.inscripted.components.DefenceComponent;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.Abstract.Item;
import com.amorabot.inscripted.components.Items.Files.ItemGSONAdapter;
import com.amorabot.inscripted.components.Player.Attributes;
import com.amorabot.inscripted.components.Player.Profile;
import com.amorabot.inscripted.components.Player.PlayerEquipment;
import com.amorabot.inscripted.tasks.PlayerInterfaceRenderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.amorabot.inscripted.utils.Utils.log;

public class JSONProfileManager {

    private static Map<UUID, Profile> profiles = new HashMap<>();
    private static final Inscripted plugin = Inscripted.getPlugin();
    // CRUD Operations ->  Create - Read - Update - Delete

    public static Profile createProfile(String uuid){
        Attributes attributes = new Attributes(0, 0,0);
        PlayerEquipment equipment = new PlayerEquipment();
        UUID id = UUID.fromString(uuid);
        Profile createdProfile = new Profile(new HealthComponent(), new DefenceComponent(), new DamageComponent(), attributes, equipment);
        profiles.put(id, createdProfile);

        return createdProfile;
    }
    public static boolean containsProfile(UUID uuid){
        return profiles.containsKey(uuid);
    }

    public static Profile getProfile(UUID uuid){
        return profiles.get(uuid);
    }

    public static void setProfile(String uuid, Profile profile){
        UUID id = UUID.fromString(uuid);
        profiles.put(id, profile);
    }

    public static void deleteProfile(String uuid){
        UUID id = UUID.fromString(uuid);
        profiles.remove(id);
    }

    public static void saveAllToJSON() throws IOException { //Saves ALL cached profile changes to the main JSON
        Gson gson = new GsonBuilder().registerTypeAdapter(Item.class, new ItemGSONAdapter()).setPrettyPrinting().create();
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
            /*
            Synchronizing profile data between cache and file
            This shard's data is saved and later centralized in a main database
            */
            Writer writer = new FileWriter(file,false);
            gson.toJson(profiles, writer);
            writer.flush();
            writer.close();
            log("Saving operation complete.");
        } catch (FileNotFoundException exception){
            exception.printStackTrace();
        }
    }
    public static void saveProfileOnQuitToJSON(UUID uuid, Profile playerProfile){
        //Since this method overrides the file, it may cause de-syncronization in some saving circunstances
        Gson gson = new GsonBuilder().registerTypeAdapter(Item.class, new ItemGSONAdapter()).setPrettyPrinting().create();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/profiles.json");
        try {
            Reader reader = new FileReader(file);//Getting the profile map
            JsonObject JSONProfileMap = gson.fromJson(reader, JsonObject.class); //Getting the JSON from file
            if (!JSONProfileMap.isJsonObject()){
                log("invalid profileMap");
                return;
            }
            JSONProfileMap.asMap().put(uuid.toString(), gson.toJsonTree(playerProfile)); //Updating the profile map

            Writer writer = new FileWriter(file, false); //Changes are done, write and override the file.
            gson.toJson(JSONProfileMap, writer);
            writer.flush();
            writer.close();
            log("Saving " + uuid + "'s profile");
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }
    public static void loadAllFromJSON() throws IOException{ //Loads EVERY profile registered, refrain from using this operation casually
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Item.class, new ItemGSONAdapter());
        Gson gson = builder.create();
//        Gson gson = new Gson();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/profiles.json");
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
                TypeToken<Map<UUID, Profile>> mapType = new TypeToken<Map<UUID, Profile>>(){};
                profiles = gson.fromJson(reader, mapType);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void loadProfileFromJSON(UUID uuid){ // Loads a specific profile into memory
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Item.class, new ItemGSONAdapter());
        Gson gson = builder.create();
//        Gson gson = new Gson();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/profiles.json");
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
                TypeToken<Map<UUID, Profile>> mapType = new TypeToken<Map<UUID, Profile>>(){};
                Map<UUID, Profile> profileMap = gson.fromJson(reader, mapType); //Loads the entire profile JSON, farm from optimal
                profiles.put(uuid, profileMap.get(uuid));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            log("Profile loaded: " + uuid);
        }
    }
    public static void reloadOnlinePlayers(Collection<? extends Player> onlinePlayers){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Item.class, new ItemGSONAdapter());
        Gson gson = builder.create();
//        Gson gson = new Gson();
        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/profiles.json");
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
                TypeToken<Map<UUID, Profile>> mapType = new TypeToken<Map<UUID, Profile>>(){};
                Map<UUID, Profile> profileMap = gson.fromJson(reader, mapType); //All profiles from a given shard
                for (Player player : onlinePlayers){
                    UUID playerId = player.getUniqueId();
                    if (!containsProfile(playerId)){ //If profile is not loaded, load
                        profiles.put(playerId, profileMap.get(playerId));
                        log("Profile reloaded: " + player.getDisplayName());
//                        PlayerInterfaceRenderer.startupBossBars(player);
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

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
        log("The profiles.JSON file is currently unavailable.");
        return false;
    }
}
