package com.amorabot.inscripted.tasks.base;

import com.amorabot.inscripted.Inscripted;
import com.amorabot.inscripted.player.PlayerDataContainer;
import com.amorabot.inscripted.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

@Getter
public abstract class PlayerboundTask extends BukkitRunnable {
    private static final boolean DEBUG_MODE = true;

    private final UUID playerID;

    public PlayerboundTask(UUID playerID){
        this.playerID = playerID;
    }

    @Override
    public void run() {
        if (!Inscripted.getPlugin().isEnabled()){
            this.cancel();
            return;
        }
        Player player = Bukkit.getPlayer(playerID);
        if (player == null || !player.isOnline()){
            abort("Error getting valid player instance for PlayerboundTask (" + this.getClass().getSimpleName()+") with taskID " + getTaskId());
            return;
        }
        taskRoutine(player);
    }

    protected abstract void taskRoutine(Player player);

    public void start(long delay, long timer) {
        runTaskTimer(Inscripted.getPlugin(),delay,timer);
        if (DEBUG_MODE) {
            Utils.log("Started task " + this.getClass().getSimpleName());
        }
        register();
    }
    public void abort(String message){
        Utils.error(message);
        if (!isCancelled()){
            this.cancel();
        }
        unregister();
    }


    //Register & Unregister default to accessing PlayerDataContainer, override for specific inheritors
    public void register() {
        PlayerDataContainer.getDataContainerFor(getPlayerID()).addTask(this);
    }
    public void unregister() {
        if (PlayerDataContainer.hasPlayerData(getPlayerID())){
            PlayerDataContainer.getDataContainerFor(getPlayerID()).removeTask(getTaskId());
        }
    }
}
