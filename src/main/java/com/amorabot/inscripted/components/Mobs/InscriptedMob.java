package com.amorabot.inscripted.components.Mobs;

import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.components.HealthComponent;
import com.amorabot.inscripted.components.Items.relic.enums.Keystones;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

@Getter
public class InscriptedMob {

    private static boolean debugMode = true;

    //Regen task
    //Threat Map<Player,int>
    //HP Display

    private boolean inactive = false;
    private final Mob mobEntity; //Contains spawner data inside its PDC
    private MobStats stats;


    public InscriptedMob(Bestiary mob, Location loc, String spawnerData){
        this.mobEntity = mob.spawnAt(loc, spawnerData);//Stat data stored withing the PDC already

        //In case stats are not stored withing the PDC
        this.stats = mob.getStats().clone();
    }
    public InscriptedMob(LivingEntity existingMob){
        this.mobEntity = (Mob) existingMob;
        Bestiary bestiaryEntry = Bestiary.getBestiaryEntryFor(existingMob);
        this.stats = bestiaryEntry.getStats();
    }

    public void destroyData(){
        this.inactive = true;
        this.mobEntity.remove();
        this.stats = null;
    }

    public void takeDamage(int[] atkrDamage, Set<Keystones> atkrKeystones){
//        MobStats mobStats = getStatsFromPDC();
        MobStats mobStats = getStats();
        HealthComponent mobHP = mobStats.getMobHealth();
        mobHP.damage(atkrDamage, Set.of(), atkrKeystones);
        if (mobHP.getCurrentHealth()==0){
            kill();
            if (debugMode){Utils.log("Killing mob!");}
            return;
        }
        if (debugMode){Utils.log("Mobs updated hp: " + mobHP.getCurrentHealth());}
//        updatedMobPDC(mobStats);
    }

    public void kill(){
        this.inactive = true;
        this.mobEntity.setHealth(0);
        //Death API call + Drops call
        //Effect task?
    }

    public void respawnAt(Location loc){
        if (debugMode){Utils.log("Respawning mob!");}
        if (isInactive()){
            this.inactive = false;
//            MobStats mobStats = getStatsFromPDC();
            MobStats mobStats = getStats();
            HealthComponent mobHP = mobStats.getMobHealth();
            mobHP.setCurrentHealth(mobHP.getMaxHealth());
            mobHP.setCurrentWard(mobHP.getMaxWard());
//            updatedMobPDC(mobStats);

            mobEntity.spawnAt(loc, CreatureSpawnEvent.SpawnReason.CUSTOM);
            mobEntity.setHealth(1);
        }
    }

    public String getSpawnerID(){
        String spawnerID = EntityStateManager.getMobSpawnerID(getMobEntity());
        if (debugMode){ Utils.log("Fetching mob spawner ID data: " + spawnerID); }
        return spawnerID;
    }

//    public MobStats getStatsFromPDC(){
//        return mobEntity.getPersistentDataContainer().get(Bestiary.getMobPDCKey(), new MobStatsContainer());
//    }
//    public void updatedMobPDC(MobStats updatedStats){
//        mobEntity.getPersistentDataContainer().set(Bestiary.getMobPDCKey(), new MobStatsContainer(), updatedStats);
//    }


}
