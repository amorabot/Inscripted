package com.amorabot.inscripted.managers;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Items.modifiers.unique.Keystones;
import com.amorabot.inscripted.components.Player.stats.StatCompiler;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerPassivesManager {

    private static final Map<UUID, Map<Keystones, Integer>> passiveTasks = new HashMap<>();


    public static Map<UUID, Map<Keystones, Integer>> getPassiveTaskMap(){
        return passiveTasks;
    }

    public static void reloadOnlinePlayers(){
        passiveTasks.clear();
        for (Player player : Bukkit.getOnlinePlayers()){
            UUID playerID = player.getUniqueId();
            StatCompiler.manageKeystoneTasks(playerID,JSONProfileManager.getProfile(playerID).getKeystones());
        }
    }
    public static void removePlayer(UUID playerID){
        Set<Keystones> playerKeystones = JSONProfileManager.getProfile(playerID).getKeystones();
        for (Keystones keystones : playerKeystones){
            if (!keystones.isPassiveTask()){continue;}
            removePassiveTask(playerID, keystones); //Can be a effect or a stat keystone
        }
        passiveTasks.remove(playerID);
    }

    public static void addKeystonePassive(UUID playerID, Keystones keystone, int taskID){
        Utils.log("adding " + keystone + " passive ("+taskID+")");
        if (!passiveTasks.containsKey(playerID)){
            Map<Keystones, Integer> newPassiveTaskMap = new HashMap<>();
            newPassiveTaskMap.put(keystone, taskID);
            passiveTasks.put(playerID, newPassiveTaskMap);
            return;
        }
        Map<Keystones, Integer> currentPassiveTaskMap = passiveTasks.get(playerID);
        assert currentPassiveTaskMap != null;
        if (currentPassiveTaskMap.isEmpty()){
            Map<Keystones, Integer> newPassiveTaskMap = new HashMap<>();
            newPassiveTaskMap.put(keystone, taskID);
            passiveTasks.put(playerID, newPassiveTaskMap);
            return;
        }
        currentPassiveTaskMap.put(keystone, taskID);
        passiveTasks.put(playerID, currentPassiveTaskMap);
    }
    public static void removePassiveTask(UUID playerID, Keystones keystone){
        Map<Keystones, Integer> passiveTaskMap = passiveTasks.get(playerID);
        if (passiveTasks.isEmpty() || !passiveTaskMap.containsKey(keystone)){
            Utils.error("No task to remove for player " + playerID + " and " + keystone);
            return;
        }
        int taskID = passiveTaskMap.remove(keystone);

        BukkitScheduler scheduler = Inscripted.getScheduler();
        scheduler.cancelTask(taskID);

        if (keystone.isStatKeystone()){
            //Once the task is cancelled and the active keystone removed from the player's profile, there's no real need for recompilation.
            JSONProfileManager.getProfile(playerID).getStatsComponent().removeActiveStatKeystone(playerID, keystone, false);
        }


        Utils.log("Passive task successfully removed! ID(" + taskID + ")");
    }
    public static boolean isPassiveRunning(UUID playerID, Keystones keystone){
        if (passiveTasks.containsKey(playerID)){
            Map<Keystones, Integer> passiveTaskMap = passiveTasks.get(playerID);
            if (passiveTaskMap != null && !passiveTaskMap.isEmpty()){
                if (passiveTaskMap.containsKey(keystone)){
                    int taskID = passiveTaskMap.get(keystone);
                    return Inscripted.getScheduler().isCurrentlyRunning(taskID);
                }
                return false;
            }
            return false;
        }
        return false;
    }
}
