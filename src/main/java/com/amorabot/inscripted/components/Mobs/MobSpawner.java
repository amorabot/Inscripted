package com.amorabot.inscripted.components.Mobs;

import com.amorabot.inscripted.APIs.damageAPI.EntityStateManager;
import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.managers.MobManager;
import com.amorabot.inscripted.skills.ParticlePlotter;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class MobSpawner {

    private final String spawnerID;

    private final float range;
    private final int mobLimit;
    private final int maxPackSize;
    private final int period;

    private final Bestiary mobToSpawn;
    private final List<InscriptedMob> mobInstances = new ArrayList<>();
    private final Location location;
    @Setter
    private int spawnerTaskID;


    public MobSpawner(String spawnerID, Bestiary mob, Location loc, float range, int mobLimit, int maxPackSize, int period){
        this.spawnerID = spawnerID;

        this.mobToSpawn = mob;
        this.location = loc;
        this.range = range;

        this.mobLimit = mobLimit;
        this.maxPackSize = maxPackSize;
        this.period = period;

//        this.spawnerTaskID = createSpawnerTask(mob, loc, period, mobLimit, maxPackSize);
        this.spawnerTaskID = createSpawnerTask(this);
    }

    public static void restartSpawnerTask(MobSpawner spawnerInstance){
        Bukkit.getScheduler().cancelTask(spawnerInstance.getSpawnerTaskID());
        spawnerInstance.setSpawnerTaskID(createSpawnerTask(spawnerInstance));
        Utils.log("Restarted spawner task for: " + spawnerInstance.getSpawnerID());
    }

    private static int createSpawnerTask(MobSpawner spawnerInstance){
        if (!MobManager.spawningEnabled()){
            return -1;
        }
        return new BukkitRunnable(){
            final MobSpawner spawnerData = spawnerInstance;
            int activeMobs = 0;

            @Override
            public void run() {
                Location attemptLocation = spawnerData.getLocation();

                if (activeMobs>=spawnerData.getMobLimit()){
                    Utils.log("Spawner reached full capacity!!");
                    ParticlePlotter.plotColoredCircleAt(
                            attemptLocation.toVector(), attemptLocation.getWorld(),
                            200,100,100, 1.1F, spawnerData.getRange(), 30);
                    return;
                }

                if (attemptLocation.getNearbyPlayers(spawnerData.getRange()).isEmpty()){
                    Utils.log("No players inside spawner's range. Aborting spawn attempt.");
                    ParticlePlotter.plotColoredCircleAt(
                            attemptLocation.toVector(), attemptLocation.getWorld(),
                            230,230,130, 1.1F, spawnerData.getRange(), 30);
                    return;
                }

                int attemptedPackSize = Utils.getRandomIntBetween(Math.min(spawnerData.getMaxPackSize(),spawnerData.getRemainingCapacity()),0);
                if (attemptedPackSize==0){Utils.log("Nothing happened!");}
                spawnerData.spawnPack(attemptedPackSize);
                final int totalInstances = spawnerData.getMobInstances().size();
                final int inactiveInstances = spawnerData.getInactiveMobs().size();
                activeMobs = (totalInstances - inactiveInstances);

                Utils.log("Sucessful attempt!");
                ParticlePlotter.plotColoredCircleAt(
                        attemptLocation.toVector(), attemptLocation.getWorld(),
                        150,230,150, 1.1F, spawnerData.getRange(), 40);
            }
        }.runTaskTimer(Inscripted.getPlugin(),(int) (100+(Math.random()*61)), spawnerInstance.getPeriod()).getTaskId();
    }


    private void spawnPack(int amount){
        //Lets prioritize reutilizing dead & inactive mobs

        List<InscriptedMob> inactiveMobs = getInactiveMobs();
        int inacMobCount = inactiveMobs.size();
        if (inactiveMobs.isEmpty()){ //spawn "amount" new mobs
            Utils.log("Pack size of " + amount);
            for (int i = 0; i < amount; i++){
                instantiateNewMob();
            }
            return;
        }
        if (inacMobCount >= amount){ //re-instantate "amount" dead monsters
            Utils.log("Respawning " + amount + " mobs");
            for (int i = 0; i < amount; i++){
                respawnMob(inactiveMobs.get(i));
            }
            return;
        }

        //Mix of both
        final int newMobsToSpawn = amount - inacMobCount;
        for (int m = 0; m < inacMobCount; m++){
            respawnMob(inactiveMobs.get(m));
        }
        for (int n = 0; n < newMobsToSpawn; n++){
            instantiateNewMob();
        }
    }


    private void respawnMob(InscriptedMob deadMob){
        if (!deadMob.isInactive()){Utils.error("The mob is not dead to begin with!"); return;}
        deadMob.respawnAt(getLocation().clone());
        Utils.log("Respawn!");
    }


    public void instantiateNewMob(){
        InscriptedMob newMob = new InscriptedMob(getMobToSpawn(),getLocation().clone(),getSpawnerID());
        if (getRemainingCapacity()==0){
            Utils.log("Unable to instantiate new mob (Full capacity!)");
            List<InscriptedMob> inactiveMobs = getInactiveMobs();
            if (inactiveMobs.isEmpty()){Utils.log("No inactive mobs to respawn... aborting spawning attempt."); return;}
            //Try to respawn a inactive mob
            InscriptedMob inactiveMob = inactiveMobs.get(0);
            inactiveMob.respawnAt(getLocation().clone());
            return;
        }
        mobInstances.add(newMob);
        Utils.log("Remaining capacity: " + getRemainingCapacity());
    }

    public void reinstantiateMob(LivingEntity mob){
        if (!EntityStateManager.isMob(mob)){
            //TODO: Make enum for spawn circunstance? (PLAYER_MINION, REGULAR_SPAWN, BOSS_SPAWN, MINION,...)
            EntityStateManager.setInscriptedMobMeta(mob,"REGULAR_SPAWN");
        }
        InscriptedMob existingMobNewInstance = new InscriptedMob(mob);
        getMobInstances().add(existingMobNewInstance);
        Utils.log("Remaining capacity: " + getRemainingCapacity());
    }

    public int getRemainingCapacity(){
        return (getMobLimit() - getMobInstances().size());
    }
    public List<InscriptedMob> getInactiveMobs(){
        return getMobInstances().stream().filter(InscriptedMob::isInactive).toList();
    }

    public void clear(){
        Utils.log("Entities to remove: " + getMobInstances().size());
        for (InscriptedMob mob : getMobInstances()){
            mob.destroyData();
        }
        getMobInstances().clear();
    }
}
