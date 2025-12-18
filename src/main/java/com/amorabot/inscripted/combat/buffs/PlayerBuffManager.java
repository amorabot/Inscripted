package com.amorabot.inscripted.combat.buffs;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.combat.buffs.categories.BuffData;
import com.amorabot.inscripted.combat.buffs.categories.damage.Damage;
import com.amorabot.inscripted.combat.buffs.categories.damage.DamageDebuffTask;
import com.amorabot.inscripted.combat.buffs.categories.healing.Healing;
import com.amorabot.inscripted.combat.buffs.categories.healing.HealingBuffTask;
import com.amorabot.inscripted.combat.buffs.categories.stat.StatBuffCountdown;
import com.amorabot.inscripted.item.render.InscriptedPalette;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.player.profile.PlayerEvents;
import com.amorabot.inscripted.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class PlayerBuffManager {

    private static final boolean DEBUG_MODE = false;

    public static void addBuffToPlayer(BuffData buffData, UUID playerID){
        //TODO: encapsulate isOfflineBuffOwner
        Player player = Bukkit.getPlayer(playerID);
        if (player==null || !player.isOnline()){
            Utils.error("Ignoring buff instancing for offline player...");
            return;
        }
        PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(playerID);
        Buffs buff = buffData.getBuff();

        Map<Buffs, BuffData> playerBuffMap = dataContainer.getActiveBuffs();
        if (playerBuffMap.containsKey(buff)){ //If the player already has that buff do:
            BuffData currentActiveBuff = playerBuffMap.get(buff);
            if (buff.isStatBuff()){ //Stat buff override routine
                overrideStatBuff(currentActiveBuff);
                return;
            }
            //For damage and healing buffs, just cancel and override the previous instance
            BukkitScheduler scheduler = Inscripted.getScheduler();
            int currentBuffInstanceID = currentActiveBuff.getTaskID();
            if (!currentActiveBuff.getBuffTask().isCancelled()){
                //DOT DEBUFFS
                if (buff.isDamageBuff()){
                    boolean ignoreCurrentDotInstance = handleDoT(buffData,scheduler,currentBuffInstanceID);
                    if (ignoreCurrentDotInstance){return;}
                }
                //HEALING BUFFS
                if (buff.isHealingBuff()){
                    boolean ignoreCurrentHealInstance = handleHealingBuff(buffData,scheduler,currentBuffInstanceID);
                    if (ignoreCurrentHealInstance){return;}
                }
            }
        }

        //New instance must be started:
        buffData.activate(); //Activating the new buff task
        playerBuffMap.put(buff,buffData); //Updating the buff map
        player.sendMessage(Component.text(buff.getAlias() + " - ").color(InscriptedPalette.WHITE.getColor())
                .append(buffData.getMessage()));

        if (buff.isStatBuff()){ //Stat buff instantiation
            dataContainer.onNotify(PlayerEvents.EXTERNAL_STAT_CHANGE);
        }

    }
    private static void overrideStatBuff(BuffData statBuffData){
        if (!(statBuffData.getBuffTask() instanceof StatBuffCountdown runningStatBuffTask)){
            if (DEBUG_MODE){
                Utils.error("Invalid stat buff override attempt...");
            }
            return;
        }
        //Reset elapsed time
        runningStatBuffTask.setTicksElapsed(0);
        if (DEBUG_MODE){
            Utils.log("Refreshing "+runningStatBuffTask.getBuff()+" duration!");
        }
    }
    private static boolean handleHealingBuff(BuffData healingBuffData, BukkitScheduler scheduler, int runningTaskID){
        if (!(healingBuffData.getBuffTask() instanceof HealingBuffTask runningHealingBuffTask)){
            if (DEBUG_MODE){
                Utils.error("Invalid healing buff override attempt...");
            }
            return true;
        }
        Buffs healingBuff = runningHealingBuffTask.getBuff();
        int totalRemainingHealing;
        totalRemainingHealing = runningHealingBuffTask.getTotalRemainingHealing();
        int currentTotalHealing = healingBuffData.getStoredValue() * ((Healing)healingBuff.getBuffAnnotationData()).timesApplied();
        if (currentTotalHealing > totalRemainingHealing){
            if (DEBUG_MODE){Utils.log("Replacing old Healing buff!");}
            scheduler.cancelTask(runningTaskID);
            return false;
            // If not ignored, a new instance will be created in the outside (addBuffToPlayer) method
        }
        if (DEBUG_MODE) {Utils.log("Keeping old Healing buff instance!");}
        return true;
    }
    private static boolean handleDoT(BuffData dotBuffTask, BukkitScheduler scheduler, int runningTaskID){
        int totalRemainingDamage;
        if (!(dotBuffTask.getBuffTask() instanceof DamageDebuffTask runningDoTTask)){
            if (DEBUG_MODE) {
                Utils.error("Invalid DoT debuff override attempt...");
            }
            return true;
        }
        totalRemainingDamage = runningDoTTask.getTotalRemainingDamage();
        int currentDebuffTotalDamage = dotBuffTask.getStoredValue() * ((Damage)runningDoTTask.getBuff().getBuffAnnotationData()).timesApplied();
        if (currentDebuffTotalDamage > totalRemainingDamage){
            if (DEBUG_MODE) {
                Utils.log("Replacing old DoT!");
            }
            scheduler.cancelTask(runningTaskID);
            return false;
        } else {
            //If it's not stronger than the current one, just ignore the apply attempt
            if (DEBUG_MODE) {
                Utils.log("Keeping old DoT instance!");
            }
            return true;
        }
    }

    public static boolean hasActiveBuff(Buffs buff, UUID playerID){
        Player player = Bukkit.getPlayer(playerID);
        if (player==null || !player.isOnline()){
            Utils.error("Ignoring buff instance check for offline player...  (false)");
            return false;
        }

        PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(playerID);
        Map<Buffs, BuffData> playerBuffMap = dataContainer.getActiveBuffs();
        if (playerBuffMap.containsKey(buff)){
            return !playerBuffMap.get(buff).getBuffTask().isCancelled();
        }
        return false;
    }

    public static void clearAllBuffsFor(UUID playerID){ //TODO: expand with clearing only debuffs
        Player player = Bukkit.getPlayer(playerID);
        if (player==null || !player.isOnline()){
            Utils.error("Ignoring buff clearing for offline player...");
            return;
        }

        PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(playerID);
        Map<Buffs, BuffData> playerBuffMap = dataContainer.getActiveBuffs();
        for (Buffs buff : playerBuffMap.keySet()){
            BuffData buffData = playerBuffMap.get(buff);
            BuffTask buffTask = buffData.getBuffTask();
            if (buffTask != null){
                if (!buffTask.isCancelled()){
                    if (DEBUG_MODE) {
                        Utils.log("Expriring " + buff + " instance for " + playerID);
                    }
                    buffTask.expire(); //Also removes the stored data for that buff
                }
            }
        }
        playerBuffMap.clear();
        if (DEBUG_MODE) {
            Utils.log("Cleared all buffs for " + playerID);
        }
    }
    public static void expirePlayerStatBuffs(UUID playerID){
        PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(playerID);
        Map<Buffs, BuffData> playerBuffMap = dataContainer.getActiveBuffs();
        for (Buffs buff : playerBuffMap.keySet()){
            if (!buff.isStatBuff()){continue;}
            BuffData buffData = playerBuffMap.get(buff);
            BuffTask buffTask = buffData.getBuffTask();
            if (buffTask != null){
                if (!buffTask.isCancelled()){
                    if (DEBUG_MODE) {
                        Utils.log("Expriring stat buff: " + buff + " for " + playerID);
                    }
                    buffTask.expire(); //Also removes the stored data for that buff
                }
            }
        }
        //After all stat buffs are expired, recompile player data
        dataContainer.onNotify(PlayerEvents.EXTERNAL_STAT_CHANGE);
    }

    public static void removeBuffFrom(UUID playerID, Buffs buff){
        //Used typically within #expire() buff method, removes cached data safely for that buff
        PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(playerID);
        Map<Buffs, BuffData> playerBuffMap = dataContainer.getActiveBuffs();
        BuffData buffData = playerBuffMap.get(buff);
        BukkitRunnable buffTask = buffData.getBuffTask();
        if (DEBUG_MODE) {
            Utils.log("Removing buff data for " + buff + " from " + playerID);
        }
        if (!buffTask.isCancelled()){
            buffTask.cancel();
            if (DEBUG_MODE) {
                Utils.log("Removed: " + buffTask.isCancelled());
            }
        }
        playerBuffMap.remove(buff);
    }

    public static Set<Buffs> getActiveStatBuffsFor(UUID playerID){
        Set<Buffs> playerStatBuffs = new HashSet<>();
        PlayerDataContainer dataContainer = PlayerDataContainer.getDataContainerFor(playerID);
        Map<Buffs, BuffData> playerBuffMap = dataContainer.getActiveBuffs();
        for (Buffs buff : playerBuffMap.keySet()){
            if (!buff.isStatBuff()){continue;}
            if (playerBuffMap.get(buff).getBuffTask().isCancelled()){continue;}
            playerStatBuffs.add(buff);
        }
        return playerStatBuffs;
    }


    /*
    Spigot thread - https://www.spigotmc.org/wiki/scheduler-programming/

    Runnables cant be re-initialized once cancelled (https://www.spigotmc.org/threads/running-a-cancelled-task.596736/)

    One way around this is to have a "ignore" flag that keeps it running but doing nothing, for example
    */
}
