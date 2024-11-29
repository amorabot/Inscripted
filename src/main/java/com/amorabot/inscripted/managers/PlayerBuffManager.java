package com.amorabot.inscripted.managers;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.components.Buff;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.PlayerStats;
import com.amorabot.inscripted.components.Items.DataStructures.Enums.ValueTypes;
import com.amorabot.inscripted.components.Player.stats.StatCompiler;
import com.amorabot.inscripted.components.Player.stats.StatPool;
import com.amorabot.inscripted.components.buffs.Buffs;
import com.amorabot.inscripted.components.buffs.categories.BuffData;
import com.amorabot.inscripted.components.buffs.categories.damage.Damage;
import com.amorabot.inscripted.components.buffs.categories.damage.DamageDebuffTask;
import com.amorabot.inscripted.components.buffs.categories.healing.Healing;
import com.amorabot.inscripted.components.buffs.categories.healing.HealingBuffTask;
import com.amorabot.inscripted.components.buffs.categories.stat.Stat;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;

public class PlayerBuffManager {

    @Getter
    private static final Map<UUID, Map<Buffs, BuffData>> playerBuffsData = new HashMap<>();


    public static void addBuffToPlayer(BuffData buffData, Player player){
        UUID playerID = player.getUniqueId();
        Buffs buff = buffData.getBuff();

        Map<Buffs, BuffData> playerBuffMap = playerBuffsData.get(playerID);
        if (playerBuffMap.containsKey(buff)){ //If the player already has that buff do:
            BuffData previousBuffData = playerBuffMap.get(buff);
            if (buff.isStatBuff()){//Stat buff override routine
//                Stat prevStatBuffData = (Stat) previousBuffData.getBuff().getBuffAnnotationData();
//                Stat newStatBuffData = (Stat) buff.getBuffAnnotationData();
                Buff buffTask = previousBuffData.getBuffTask();
                if (!buffTask.isCancelled()){
                    buffTask.cancel();
                }
                //Prev. instance is now cancelled, it will be overriten
                buffData.activate();
                playerBuffMap.put(buff,buffData);
                //Skip recompilation, since the previous instance is gone and refreshed by the new one
                Utils.log("Refreshing "+buff+" duration!");
                return;
            }
            //For damage and healing buffs, just cancel and override the previous instance
            BukkitScheduler scheduler = Inscripted.getScheduler();
            int prevTaskID = previousBuffData.getTaskID();
            if (!previousBuffData.getBuffTask().isCancelled()){
                //DOT DEBUFFS
                if (buff.isDamageBuff()){
                    int totalRemainingDamage;
                    DamageDebuffTask prevTask = (DamageDebuffTask) previousBuffData.getBuffTask();
                    totalRemainingDamage = prevTask.getTotalRemainingDamage();
                    int currentDebuffTotalDamage = buffData.getStoredValue() * ((Damage)buff.getBuffAnnotationData()).timesApplied();
                    if (currentDebuffTotalDamage > totalRemainingDamage){
                        scheduler.cancelTask(prevTaskID);
                    } else {
                        //If it's not stronger than the current one, just ignore the apply attempt
                        return;
                    }
                }
                //HEALING BUFFS
                if (buff.isHealingBuff()){
                    int totalRemainingHealing;
                    HealingBuffTask prevTask = (HealingBuffTask) previousBuffData.getBuffTask();
                    totalRemainingHealing = prevTask.getTotalRemainingHealing();
                    int currentTotalHealing = buffData.getStoredValue() * ((Healing)buff.getBuffAnnotationData()).timesApplied();
                    if (currentTotalHealing > totalRemainingHealing){
                        scheduler.cancelTask(prevTaskID);
                    }else {
                        return;
                    }
                }
            }
        }

        buffData.activate(); //Activating the new buff task
        playerBuffMap.put(buff,buffData); //Updating the buff map

        if (buff.isStatBuff()){//Stat buff instantiation
            //In case a stat buff is being added, a recompilation of stats is needed, to get the newly updated active buff's stats
            Utils.log("Updating buff map -> recompilation needed");
            StatCompiler.updateProfile(playerID);
        }

    }

    public static boolean hasActiveBuff(Buffs buff, Player player){
        Map<Buffs, BuffData> playerBuffMap = playerBuffsData.get(player.getUniqueId());
        if (playerBuffMap.containsKey(buff)){
            return !playerBuffMap.get(buff).getBuffTask().isCancelled();
        }
        return false;
    }


    public static void reloadOnlinePlayers(){
        playerBuffsData.clear();
        for (Player player : Bukkit.getOnlinePlayers()){
            initializePlayer(player);
        }
    }

    public static void initializePlayer(Player player){
        UUID playerID = player.getUniqueId();
        if (playerBuffsData.containsKey(playerID)){return;}
        Map<Buffs, BuffData> playerBuffs = new HashMap<>();
        playerBuffsData.put(playerID, playerBuffs);
        Utils.log("Initializing buff data for " + player.getName());
    }

    public static void clearAllBuffsFor(Player player){ //TODO: expand with clearing only debuffs
        UUID playerID = player.getUniqueId();
        Map<Buffs, BuffData> playerBuffMap = playerBuffsData.get(playerID);
        for (Buffs buff : playerBuffMap.keySet()){
            BuffData buffData = playerBuffMap.get(buff);
            Buff buffTask = buffData.getBuffTask();
            if (buffTask != null){
                if (!buffTask.isCancelled()){
                    Utils.log("Expriring "+buff+" instance for " + player.getName());
                    buffTask.expire(); //Also removes the stored data for that buff
                }
            }
        }
        playerBuffMap.clear();
        Utils.log("Cleared all buffs for " + player.getName());
    }
    public static void expirePlayerStatBuffs(Player player){
        UUID playerID = player.getUniqueId();
        Map<Buffs, BuffData> playerBuffMap = playerBuffsData.get(playerID);
        for (Buffs buff : playerBuffMap.keySet()){
            if (!buff.isStatBuff()){continue;}
            BuffData buffData = playerBuffMap.get(buff);
            Buff buffTask = buffData.getBuffTask();
            if (buffTask != null){
                if (!buffTask.isCancelled()){
                    Utils.log("Expriring stat buff: "+buff+" for " + player.getName());
                    buffTask.expire(); //Also removes the stored data for that buff
                }
            }
        }
        //After all stat buffs are expired, recompile player data
        Utils.log("All stat de/buffs removed, recompiling player data...");
        StatCompiler.updateProfile(playerID);
    }

    public static void removeBuffFrom(Player player, Buffs buff){
        //Used typically within #expire() buff method, removes cached data safely for that buff
        Map<Buffs, BuffData> playerBuffMap = playerBuffsData.get(player.getUniqueId());
        BuffData buffData = playerBuffMap.get(buff);
        BukkitRunnable buffTask = buffData.getBuffTask();
        Utils.log("Removing buff data for "+buff+" from " + player.getName());
        if (!buffTask.isCancelled()){
            buffTask.cancel();
            Utils.log("removed: " + buffTask.isCancelled());
        }
        playerBuffMap.remove(buff);
    }

    public static StatPool getBuffStatsFor(UUID playerID){
        Player player = Bukkit.getPlayer(playerID);
        assert player != null;
        if (!player.isOnline()){
            return new StatPool();
        }

        StatPool buffStats = new StatPool();
        Set<Buffs> playerStatBuffs = getActiveStatBuffsFor(player);
        if (playerStatBuffs.isEmpty()){
            Utils.log("No buffs to be compiled!");
            return new StatPool();
        }
        for (Buffs statBuff : playerStatBuffs){
            Stat buffStatData = (Stat) (statBuff.getBuffAnnotationData());
            PlayerStats currentStat = buffStatData.targetStat();
            ValueTypes statType = buffStatData.valueType();
            int statValue = buffStatData.amount();
            if (statBuff.isDebuff()){
                statValue = -statValue;
            }
            Utils.log("Compiling " + statValue + " " + statType + " " + currentStat + "|| Buff: " + statBuff);
            buffStats.addStat(currentStat, statType, new int[]{statValue});
//            StatCompiler.putSingleValueIn(buffStatsMap, currentStat, statType, statValue);
        }
        return buffStats;
    }

    public static Set<Buffs> getActiveStatBuffsFor(Player player){
        Set<Buffs> playerStatBuffs = new HashSet<>();
        Map<Buffs, BuffData> playerBuffMap = playerBuffsData.get(player.getUniqueId());
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
