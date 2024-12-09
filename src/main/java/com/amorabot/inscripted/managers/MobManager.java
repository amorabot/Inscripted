package com.amorabot.inscripted.managers;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Mobs.Bestiary;
import com.amorabot.inscripted.components.Mobs.MobSpawner;
import com.amorabot.inscripted.components.Mobs.Spawners;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class MobManager {
    private static final Map<String, MobSpawner> spawnerMap = new HashMap<>();

    public static void putSpawner(String spawnerID, MobSpawner spawner){
        spawnerMap.putIfAbsent(spawnerID,spawner);
    }

    //Used on reload // for testing
    public static void reinstantiateMobSpawners(){
        Utils.log("Reloading spawner mobs!");
        World mainWorld = Inscripted.getPlugin().getWorld();
        assert mainWorld != null;
        for (LivingEntity l : mainWorld.getLivingEntities()){
            //Mob metadata is not persistent!! Cant rely on it here since it's wiped at this point
            if (l.getPersistentDataContainer().has(Bestiary.getIdPDCKey())){
                String spawnerKey = Bestiary.getSpawnerIdFor(l);
                if (!spawnerMap.containsKey(spawnerKey)){
                    try {
                        Spawners mappedSpawnerKey = Spawners.valueOf(spawnerKey);
                        MobSpawner spawnerData = mappedSpawnerKey.getSpawnerData();
                        //Instantiate this entity directly into the spawner
                        spawnerData.reinstantiateMob(l);
                    } catch (IllegalArgumentException ex){
                        Utils.error("Invalid mob spawner key. (Reload re-instantiation)");
                    }
                    continue;
                }
                //Entities for that spawner have already been re-instantiated, lets access the spawner directly
                MobSpawner storedSpawner = spawnerMap.get(spawnerKey);
                storedSpawner.reinstantiateMob(l);
            }
        }
    }

    //Arbitrary reset on spawners
    public static void clearSpawners(boolean renewTasks){
        if (!spawningEnabled()){return;}
        Set<String> spawnerKeys = spawnerMap.keySet();
        for (String key : new HashSet<>(spawnerKeys)){
            Utils.log("Clearing " + key);
            MobSpawner ms = spawnerMap.get(key);
            ms.clear();
            if (renewTasks){
                MobSpawner.restartSpawnerTask(ms);
            }
        }
    }

    public static void stopMobSpawning(){
        for (String key : spawnerMap.keySet()){
            MobSpawner ms = spawnerMap.get(key);
            int spawnerTaskID = ms.getSpawnerTaskID();
            Bukkit.getScheduler().cancelTask(spawnerTaskID);
        }
    }

    public static boolean spawningEnabled(){
        return Spawners.isEnabled();
    }
}
