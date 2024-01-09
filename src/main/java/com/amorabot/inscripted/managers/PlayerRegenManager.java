package com.amorabot.inscripted.managers;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.tasks.PlayerRegenerationTask;
import com.amorabot.inscripted.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRegenManager {
    private static final int secondsBeforeWardRecharge = 4;
    private static final int regenTimerCooldown = 20;

    private static final Map<UUID, PlayerRegenerationTask> activeRegenTasks = new HashMap<>();
    private static final Map<UUID, Long> wardRegenCooldown = new HashMap<>();



    public static void startWardRegenCooldownFor(UUID playerID){
        wardRegenCooldown.put(playerID, System.currentTimeMillis());
    }

    public static boolean canRegenWard(UUID playerID){
        if (wardRegenCooldown.containsKey(playerID)){
            int timeSincelastHit = (int) ((System.currentTimeMillis()-wardRegenCooldown.get(playerID))/1000);
            //If not enough time's passed since the that player's last hit, they cant regen ward
            return timeSincelastHit >= secondsBeforeWardRecharge;
        }
        return false;
    }

    public static void reloadOnlinePlayers(){
        activeRegenTasks.clear();
        for (Player player : Bukkit.getOnlinePlayers()){
            addPlayer(player.getUniqueId());
        }
    }

    public static void addPlayer(UUID playerID){
        if (activeRegenTasks.containsKey(playerID)){
            Utils.log("player already registered in playerRegenManager");
            return;
        }
        PlayerRegenerationTask playerRegen = new PlayerRegenerationTask(playerID);
        playerRegen.runTaskTimer(Inscripted.getPlugin(), 0, regenTimerCooldown);
        activeRegenTasks.put(playerID, playerRegen);
        wardRegenCooldown.put(playerID, 0L);
    }
    public static void removePlayer(UUID playerID){
        if (!activeRegenTasks.containsKey(playerID)){return;}
        BukkitRunnable regenTask = activeRegenTasks.remove(playerID);
        regenTask.cancel();
        wardRegenCooldown.remove(playerID);
    }

    public static void shutdown(){
        for(PlayerRegenerationTask task : activeRegenTasks.values()){
            task.cancel();
        }
        activeRegenTasks.clear();
        wardRegenCooldown.clear();
    }
}
