package com.amorabot.inscripted.file.profile;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.Profile;
import com.amorabot.inscripted.player.profile.ProfileEvents;
import com.amorabot.inscripted.utils.Utils;
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


public class ProfileDatabase {

    private static final String PROFILES_JSON_PATH = "/profilesDB.json";

    public static void createProfilesJSON(UUID uuid){
        //Pretty printing for the original data
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File jsonDB = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + PROFILES_JSON_PATH);
        if (!jsonDB.exists()){
            try {
                Map<UUID,Profile> baseProfileMap = new HashMap<>();
                Profile profile = PlayerDataContainer.getProfile(uuid);
                baseProfileMap.put(uuid,profile);

                // Persist the original profile
                jsonDB.createNewFile();
                Writer writer = new FileWriter(jsonDB, false);
                gson.toJson(baseProfileMap, writer);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void saveProfile(UUID uuid){
        Gson gson = new GsonBuilder().create();
        File dbFile = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + PROFILES_JSON_PATH);
        if (!dbFile.exists()){
            createProfilesJSON(uuid);
            Utils.log("Database & Profile created successfully! (Saving "+uuid+")");
            return;
        }
        try {
            Reader reader = new FileReader(dbFile);
            TypeToken<Map<UUID, Profile>> mapType = new TypeToken<Map<UUID, Profile>>(){};
            Map<UUID, Profile> storedProfiles = gson.fromJson(reader, mapType);

            Profile profileToPersist = PlayerDataContainer.getProfile(uuid);
            storedProfiles.put(uuid,profileToPersist);

            Writer writer = new FileWriter(dbFile, false); //Changes are done, write and override the file.
            gson.toJson(storedProfiles, writer);
            writer.flush();
            writer.close();
            log("Saving " + uuid + "'s profile.");
        } catch (IOException e) {
            Utils.error("Couldn't save "+uuid+"'s profile data.");
        }
    }
    public static void saveLoadedProfiles(){
        saveProfiles(PlayerDataContainer.getProfiles());
    }
    public static void saveProfiles(Map<UUID, Profile> profiles){
        Gson gson = new GsonBuilder().create();
        File dbFile = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + PROFILES_JSON_PATH);
        if (!dbFile.exists()){
            //Create dummy DB entry
            createProfilesJSON(UUID.randomUUID());
            log("Created dummy database. Reattempting to save profiles...");
            saveProfiles(profiles);
            return;
        }
        try {
            Reader reader = new FileReader(dbFile);
            TypeToken<Map<UUID, Profile>> mapType = new TypeToken<Map<UUID, Profile>>(){};
            Map<UUID, Profile> storedProfiles = gson.fromJson(reader, mapType);

            for (UUID profileID : profiles.keySet()){
                Profile profileToPersist = PlayerDataContainer.getProfile(profileID);
                storedProfiles.put(profileID,profileToPersist);
            }

            Writer writer = new FileWriter(dbFile, false); //Changes are done, write and override the file.
            gson.toJson(storedProfiles, writer);
            writer.flush();
            writer.close();
            log("Saving profiles.");
        } catch (IOException e) {
            Utils.error("Couldn't save profiles.");
        }
    }

    public static Profile loadProfile(UUID uuid){
        try {
            Map<UUID, Profile> profiles = loadAllProfiles();
            if (!profiles.containsKey(uuid)){
                return new Profile();
            }
            return profiles.get(uuid);
        } catch (IOException ex){
            Utils.error("Unable to load profile: " + uuid.toString() + "\nReturning empty profile.");
            return new Profile();
        }
    }
    public static void reloadOnlinePlayers(Collection<? extends Player> onlinePlayers){
        //Always used after a shutdown, player data should be correctly stored and shouldn't need checking
        try {
            Map<UUID, Profile> profiles = loadAllProfiles();
            for (Player player : onlinePlayers){
                UUID currentPlayerID = player.getUniqueId();
                Profile currentProfile = profiles.get(currentPlayerID);
                PlayerDataContainer.instantiatePlayer(currentPlayerID,currentProfile);
                //Trigger a complete equipment re-evaluation (reinstantiate equipment cached data)
                PlayerDataContainer.getDataContainerFor(currentPlayerID).onNotify(ProfileEvents.REEVALUATE_ALL_EQUIPMENT);
            }
        } catch (IOException ex){
            Utils.error("Unable to load profile data on reload.");
        }
    }
    public static Map<UUID, Profile> loadAllProfiles() throws IOException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + PROFILES_JSON_PATH);
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
                TypeToken<Map<UUID, Profile>> mapType = new TypeToken<Map<UUID, Profile>>(){};
                return gson.fromJson(reader, mapType);
            } catch (FileNotFoundException e) {
                Utils.error("Unable to load profiles. (File not found)");
                throw e;
            }
        }
        return new HashMap<>();
    }




    public static void saveAllCachedProfiles(String shardDataFilepath) throws IOException { //Used for sharding. The result gets merged to main DB
        Gson gson = new GsonBuilder().create();
        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + shardDataFilepath);
        file.getParentFile().mkdir();
        //Check if the accessed file path exists:
        if (!file.exists()){
            file.createNewFile();
            Writer writer = new FileWriter(file, false);
            gson.toJson(PlayerDataContainer.getProfiles(), writer);
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
            gson.toJson(PlayerDataContainer.getProfiles(), writer);
            writer.flush();
            writer.close();
            Utils.log("Saved all cached player profiles successfully.");
        } catch (FileNotFoundException exception){
            Utils.error("Unable to save profiles for shard " + shardDataFilepath);
        }
    }
    public static boolean isNewPlayer(UUID uuid){ // Checks the profiles JSON for this player's entry
        Gson gson = new Gson();
        File file = new File(Inscripted.getPlugin().getDataFolder().getAbsolutePath() + PROFILES_JSON_PATH);
        if (file.exists()){
            try {
                Reader reader = new FileReader(file);
                JsonObject JSONProfileMap = gson.fromJson(reader, JsonObject.class);
                return !JSONProfileMap.has(uuid.toString());
            } catch (FileNotFoundException e) {
                Utils.error("File not found, creating...");
                createProfilesJSON(uuid);
                return true;
            }
        }
        return true;
    }
}
