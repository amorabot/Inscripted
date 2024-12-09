package com.amorabot.inscripted.managers;

import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Mobs.Bestiary;
import com.amorabot.inscripted.components.Mobs.InscriptedMob;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class MobManager {
    //               spawnerID  entityID   mobData
    private static Map<String,Map<UUID, InscriptedMob>> mobMap = new HashMap<>(); //


    public static void instantiateMob(InscriptedMob mob){
        String spawnerID = mob.getSpawnerID();
        UUID mobID = mob.getMobEntity().getUniqueId();
        if (!mobMap.containsKey(spawnerID)){
            Map<UUID, InscriptedMob> freshMobIDMapping = new HashMap<>();
            freshMobIDMapping.put(mobID, mob);
            Utils.log("First mob for spawnerID " + spawnerID + "! Creating new map for it");
            mobMap.put(spawnerID,freshMobIDMapping);
            return;
        }
        Map<UUID, InscriptedMob> storedMobIDMapping = mobMap.get(spawnerID);
        storedMobIDMapping.putIfAbsent(mobID, mob);
        Utils.log("Successfully instantiated new mob!");
    }

    public static void instantiateMob(LivingEntity mob){
        if (!EntityStateManager.isMob(mob)){return;}
        InscriptedMob existingMobNewInstance = new InscriptedMob(mob);
        instantiateMob(existingMobNewInstance);
    }

    public static InscriptedMob getMobInstanceFor(LivingEntity mobEntity){
        if (!EntityStateManager.isMob(mobEntity)){
            Utils.error("Not a valid mob!");
            return null;
        }

        String spawnerID = EntityStateManager.getMobSpawnerID(mobEntity);
        if (!mobMap.containsKey(spawnerID)){
            Utils.error("Derialized invalid spawnerID data");
            return null;
        }

        UUID entityID = mobEntity.getUniqueId();
        Map<UUID, InscriptedMob> storedMobIDMapping = mobMap.get(spawnerID);
        if(!storedMobIDMapping.containsKey(entityID)){
            Utils.error("EntityID not present on memory!");
            return null;
        }

        return storedMobIDMapping.get(entityID);
    }

    public static void reloadAllMobsIntoMemory(){
        Utils.log("Reloading mobs!");
        World mainWorld = Inscripted.getPlugin().getWorld();
        assert mainWorld != null;
        for (LivingEntity l : mainWorld.getLivingEntities()){
            //Mob metadata is not persistent!! Cant rely on it here since it's wiped at this point
            if (l.getPersistentDataContainer().has(Bestiary.getMobPDCKey())){
                String spawnerID = getSpawnerDataFor(l);
                EntityStateManager.setInscriptedMobMeta(l,spawnerID);
                instantiateMob(l);
            } else {
                Utils.error(l.getName()+" is not a mob!!");
            }
        }
    }

    public static void clearSpawnerData(String spawnerKey){
        if (!mobMap.containsKey(spawnerKey)){return;}
        Map<UUID, InscriptedMob> storedMobIDMapping = mobMap.get(spawnerKey);
        Set<UUID> storedEntityIDs = storedMobIDMapping.keySet();
        Utils.log("Entities to remove: " + storedEntityIDs.size());

        for (UUID entityID : new HashSet<>(storedEntityIDs)){
            InscriptedMob removedMob = storedMobIDMapping.remove(entityID);
            removedMob.destroyData();
        }
    }


    public static String getSpawnerDataFor(LivingEntity l){
        if (!l.getPersistentDataContainer().has(Bestiary.getIdPDCKey())){
            return "None";
        }
        return l.getPersistentDataContainer().get(Bestiary.getIdPDCKey(), PersistentDataType.STRING);
    }
}
