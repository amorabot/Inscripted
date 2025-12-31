//package com.amorabot.inscripted.file.profile;
//
////import com.amorabot.inscripted.Inscripted;
////import com.amorabot.inscripted.components.DamageComponent;
////import com.amorabot.inscripted.components.DefenceComponent;
////import com.amorabot.inscripted.components.HealthComponent;
////import com.amorabot.inscripted.components.Items.Abstract.Item;
////import com.amorabot.inscripted.item.structure.io.ItemGSONAdapter;
////import com.amorabot.inscripted.components.Player.Attributes;
////import com.amorabot.inscripted.components.Player.Profile;
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.JsonObject;
//import com.google.gson.reflect.TypeToken;
//import org.bukkit.entity.Player;
//
//import java.io.*;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import static com.amorabot.inscripted.utils.Utils.log;
//
//public class JSONProfileManager {
//
////    private static Map<UUID, Profile> profiles = new HashMap<>();
////    private static final Inscripted plugin = Inscripted.getPlugin();
////    // CRUD Operations ->  Create - Read - Update - Delete
////
////    public static boolean containsProfile(UUID uuid){
////        return profiles.containsKey(uuid);
////    }
////
////    public static Profile getProfile(UUID uuid){
////        return profiles.get(uuid);
////    }
////    public static void reloadOnlinePlayers(Collection<? extends Player> onlinePlayers){
////        GsonBuilder builder = new GsonBuilder();
//////        builder.registerTypeAdapter(Item.class, new ItemGSONAdapter());
////        Gson gson = builder.create();
//////        Gson gson = new Gson();
////        File file = new File(plugin.getDataFolder().getAbsolutePath() + "/profiles.json");
////        if (file.exists()){
////            try {
////                Reader reader = new FileReader(file);
////                TypeToken<Map<UUID, Profile>> mapType = new TypeToken<Map<UUID, Profile>>(){};
////                Map<UUID, Profile> profileMap = gson.fromJson(reader, mapType); //All profiles from a given shard
////                for (Player player : onlinePlayers){
////                    UUID playerId = player.getUniqueId();
////                    if (!containsProfile(playerId)){ //If profile is not loaded, load
////                        profiles.put(playerId, profileMap.get(playerId));
////                        log("Profile reloaded: " + player.getDisplayName());
//////                        PlayerInterfaceRenderer.startupBossBars(player);
////                    }
////                }
////            } catch (FileNotFoundException e) {
////                throw new RuntimeException(e);
////            }
////
////        }
////    }
//}
